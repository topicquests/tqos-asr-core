/*
 * Copyright 2014, 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hyperbrane;

import java.io.*;
//import java.util.*;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minidev.json.*;
import net.minidev.json.parser.JSONParser;
//import org.topicquests.learner.api.IDataProvider;
import org.topicquests.hyperbrane.api.IDictionary;
import org.topicquests.os.asr.ASRCoreEnvironment;

/**
 * @author park
 * <p>This implements a dictionary partitioned into these sections:
 * <li>words -- id/word pairs</li>
 * <li>ids -- word/id pairs</li>
 * <li>sentences -- id/sentenceList pairs</li></p>
 */
public class ConcordanceDictionary  implements IDictionary {
	private ASRCoreEnvironment environment;
	private JSONObject dictionary;
	private JSONUtil util;
	private Long nextNumber = null;
	private long wordCount = 0;
	private long totalWordCount = 0;
	static final String 
		WORDS 		= "words",
		//an index of words, returning their id values
		IDS			= "ids",
		NUMBER		= "number",
		SIZE		= "size",
		WORD_COUNT	= "wordCount";
	
	/**
	 * @param env
	 * @throws Exception
	 */
	public ConcordanceDictionary(ASRCoreEnvironment env) throws Exception {
		environment = env;
		util = new JSONUtil();
		bootDictionary();
		nextNumber = (Long)dictionary.get(NUMBER);
		if (nextNumber == null)
			nextNumber = new Long(1);		
	}
	
	void bootDictionary() throws Exception {
		String path = environment.getStringProperty("WordDictionaryPath");
		dictionary = load(path);
		if (dictionary == null || dictionary.isEmpty()) {
			dictionary = new JSONObject();
			dictionary.put(WORDS, new JSONObject());
			dictionary.put(IDS, new JSONObject());
		} else {
			nextNumber = ((Long)dictionary.get(NUMBER)).longValue();
			wordCount = ((Long)dictionary.get(SIZE)).longValue();
			totalWordCount = ((Long)dictionary.get(WORD_COUNT)).longValue(); 
		}
		environment.logDebug("DictionarySize- "+dictionary.size());
	}
	
	public void saveDictionary() throws Exception {
		dictionary.put(NUMBER, nextNumber);
		dictionary.put(SIZE, new Long(wordCount));
		dictionary.put(WORD_COUNT, new Long(totalWordCount));
		String path = environment.getStringProperty("WordDictionaryPath");
		save(path, dictionary);
		environment.logDebug("DictionarySize+ "+dictionary.size());
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#getWord(java.lang.String)
	 */
	@Override
	public String getWord(String id) {
		synchronized(dictionary) {
			JSONObject words = getWords();
			return (String)words.get(id);
		}
	}
	
	JSONObject getWords() {
		return (JSONObject)dictionary.get(WORDS);
	}
	
	JSONObject getIDs() {
		return (JSONObject)dictionary.get(IDS);
	}
	

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#getId(java.lang.String)
	 */
	@Override
	public String getWordId(String word) {
		//This tests for the word and, if necessary, its lowercase version
		synchronized(dictionary) {
			JSONObject ids = getIDs();
			String result = (String)ids.get(word);
			if (result == null) {
				//does this word have caps?
				String lc = word.toLowerCase();
				if (!lc.equals(word)) {
					//see if it exists as lowercase
					result = (String)ids.get(lc);
				}
			}
			return result;
		}
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#addWord(java.lang.String, java.lang.String)
	 */
	@Override
	public String addWord(String theWord) {
		//Will get the word even if lower case
		String word = theWord.toLowerCase();
		String id = getWordId(word);
		synchronized(dictionary) {
			totalWordCount++;
			//update global statistics
			environment.getStats().addWordRead();
			if (id == null) {
				this.wordCount++;
				environment.getStats().addDictionaryWord();
				//this is a new word
				id = newNumericId();
				//TODO NOTE: if id == -1, there was an error in the database
				//TODO don't make duplicate lowercase, but always add word if it doesn't exist
				// This captures all spellings of a given word
				JSONObject words = getWords();
				words.put(id, word);
				words = getIDs(); //reuse variable
				words.put(word, id);
			}
		}
		return id;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#export(java.lang.String)
	 */
	@Override
	public void export(Writer out) throws Exception {
		synchronized(dictionary) {
			System.out.println("exporting "+dictionary.size());
			dictionary.put(NUMBER, nextNumber);
			dictionary.put(SIZE, new Long(wordCount));
			dictionary.put(WORD_COUNT, new Long(totalWordCount));
			dictionary.writeJSONString(out);
			out.flush();
			out.close();
		System.out.println("exported");
		}
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#getDictionary(java.lang.String)
	 */
	@Override
	public JSONObject getDictionary() {
		return dictionary;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#isEmpty(java.lang.String)
	 */
	@Override
	public boolean isEmpty() {
		synchronized(dictionary) {
			JSONObject obj = this.getWords();
			if (obj == null)
				return false;
			return obj.isEmpty();
		}
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#getWordIdOrAddWord(java.lang.String)
	 */
	@Override
	public String getWordIdOrAddWord(String word) {
		String result = this.getWordId(word);
		if (result == null)
			result = this.addWord(word);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#newNumericId(java.lang.String)
	 */
	@Override
	public String newNumericId() {
		synchronized(dictionary) {
			String result =nextNumber.toString();
			nextNumber += 1;
			return result;
		}
	}

	@Override
	public void setNumericId(long newId) {
		nextNumber = new Long(newId);
	}

	@Override
	public long getSize() {
		return this.wordCount;
	}

	JSONObject load(String filePath) throws Exception {
		JSONObject result = null;
		File myFile = new File(filePath);
		if (myFile.exists()) {
			FileInputStream fis = new FileInputStream(myFile);
			GZIPInputStream gis = new GZIPInputStream(fis);
			BufferedReader rdr = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
			String json = rdr.readLine();
			environment.logDebug("DICTIONARY.LOAD "+json);
			rdr.close();
			JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
			try {
				result = (JSONObject)parser.parse(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			result = new JSONObject();
		return result;
	}
	void save(String filePath, JSONObject jo) throws Exception {
		File myFile = new File(filePath);
		FileOutputStream fos = new FileOutputStream(myFile);
		GZIPOutputStream gos = new GZIPOutputStream(fos);
		PrintWriter out = new PrintWriter(gos);
		out.println(jo.toJSONString());
		out.flush();
		out.close();
	}
}

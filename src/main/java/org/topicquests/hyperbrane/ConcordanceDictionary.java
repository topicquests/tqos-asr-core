/*
 * Copyright 2014, 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hyperbrane;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minidev.json.*;
import net.minidev.json.parser.JSONParser;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
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
	//private JSONObject dictionary;
	private DB wordDatabase;
	private DB idDatabase;
	private BTreeMap<String, String> wordIds;
	private BTreeMap<String, String> idWords;
	private JSONUtil util;
	private Long nextNumber = null;
	private long wordCount = 0;
	private long totalWordCount = 0;
	private boolean isClosed = true;

	static final String 
		WORD_ID_NAME	= "WordId",
		ID_WORD_NAME	= "IdWord",
		WORDS 		= "words",
		//an index of words, returning their id values
		IDS			= "ids",
		NUMBER		= "numberx",
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
		//nextNumber = (Long)dictionary.get(NUMBER);
		//if (nextNumber == null)
		//	nextNumber = new Long(1);		
	}
	
	void bootDictionary() throws Exception {
		isClosed = false;
		String path = environment.getStringProperty("WordDictionaryPath");
		String idPath = environment.getStringProperty("IdDictionaryPath");
		File f = new File(path);
		environment.logDebug("ConcordanceDictionary.bootDictionary-1 "+path+" "+f);
		wordDatabase = DBMaker.fileDB(f)
				.closeOnJvmShutdown()
				.make();
		environment.logDebug("ConcordanceDictionary.bootDictionary-2 "+wordDatabase);

		 wordIds = wordDatabase
		        .treeMap(WORD_ID_NAME, Serializer.STRING, Serializer.STRING)
		        .counterEnable()
		        .createOrOpen();
		environment.logDebug("ConcordanceDictionary.bootDictionary-2a "+wordIds);
environment.logDebug("FOO "+wordIds.get(NUMBER)+" "+wordIds.get(SIZE)+" "+wordIds.get(WORD_COUNT));
		 if (wordIds.get(NUMBER) == null) {
			 wordIds.put(NUMBER, "0");
			 wordIds.put(SIZE, "0");
			 wordIds.put(WORD_COUNT, "0");
			 nextNumber = new Long(1);
			 wordCount = 0;
			 totalWordCount = 0;
		 } else {
			 String x = wordIds.get(NUMBER);
			 String y = wordIds.get(SIZE);
			 String z = wordIds.get(WORD_COUNT);
			 environment.logDebug("BAR "+x+" "+y+" "+z+" "+(y.equals("0")));
			 nextNumber = Long.parseLong(x); //getLong(x);
			 wordCount = Long.parseLong(y);//    Long.getLong(y);
			 totalWordCount = Long.parseLong(z); //getLong(z);
		 }
		environment.logDebug("ConcordanceDictionary.bootDictionary-3 "+wordIds);
		f = new File(idPath);
		 idDatabase = DBMaker.fileDB(f)
					.closeOnJvmShutdown()
					.make();
			environment.logDebug("ConcordanceDictionary.bootDictionary-4 "+idDatabase);
		 idWords = idDatabase
			        .treeMap(ID_WORD_NAME, Serializer.STRING, Serializer.STRING)
			        .counterEnable()
			        .createOrOpen();
		environment.logDebug("ConcordanceDictionary.bootDictionary-5 "+idWords);

/*		
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
*/
		environment.logDebug("DictionarySize- "+totalWordCount);
	}
	
	public void saveDictionary() { //throws Exception {
		environment.logDebug("DictionarySize+ "+totalWordCount);
		if (!isClosed) {
			System.out.println("PersistentMap shutting down");
			//TODO 
			//IF we store these on each addWord
			// Will run slower but will be fault tolerant
			 wordIds.put(NUMBER, nextNumber.toString());
			 wordIds.put(SIZE, Long.toString(wordCount));
			 wordIds.put(WORD_COUNT, Long.toString(totalWordCount));

			wordDatabase.commit();
			//idDatabase.commit();
			//database.compact();
			 if (!wordDatabase.isClosed())
				 wordDatabase.close();
			 if (!idDatabase.isClosed())
				 idDatabase.close();
			System.out.println("PersistentMap closed");
			isClosed = true;
		}

		/*
		dictionary.put(NUMBER, nextNumber);
		dictionary.put(SIZE, new Long(wordCount));
		dictionary.put(WORD_COUNT, new Long(totalWordCount));
		String path = environment.getStringProperty("WordDictionaryPath");
		save(path, dictionary);*/
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#getWord(java.lang.String)
	 */
	@Override
	public String getWord(String id) {
		synchronized(idWords) {
			return idWords.get(id);
		}
	}
	/*
	JSONObject getWords() {
		return (JSONObject)dictionary.get(WORDS);
	}
	
	JSONObject getIDs() {
		return (JSONObject)dictionary.get(IDS);
	}
	*/

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#getId(java.lang.String)
	 */
	@Override
	public String getWordId(String word) {
		//This tests for the word and, if necessary, its lowercase version
		synchronized(wordIds) {
			//JSONObject ids = getIDs();
			String result = wordIds.get(word);
			environment.logDebug("ConcordanceDictionary.getWordId "+word+" "+result);
			if (result == null) {
				//does this word have caps?
				String lc = word.toLowerCase();
				environment.logDebug("ConcordanceDictionary.getWordId-1 "+word+" "+lc);
				if (!lc.equals(word)) {
					//see if it exists as lowercase
					result = wordIds.get(lc);
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
		//update global statistics
		environment.getStats().addWordRead();
		//Will get the word even if lower case
		String word = theWord.toLowerCase();
		String id = getWordId(word);
		environment.logDebug("Dictionary.addWord "+word+" "+id);
		synchronized(wordIds) {
			totalWordCount++;
			
			if (id == null) {
				this.wordCount++;
				environment.getStats().addDictionaryWord();
				//this is a new word
				id = nextNumber.toString();
				nextNumber += 1;
				//TODO NOTE: if id == -1, there was an error in the database
				//TODO don't make duplicate lowercase, but always add word if it doesn't exist
				// This captures all spellings of a given word
				//JSONObject words = getWords();
				environment.logDebug("Dictionary.addWord-1 "+word+" "+id);
				wordIds.put(word, id);
				//wordIds.
				//words = getIDs(); //reuse variable
				idWords.put(id, word);
				wordDatabase.commit();
				idDatabase.commit();
			}
		}
		return id;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#export(java.lang.String)
	 */
	@Override
	public void export(Writer out) throws Exception {
		synchronized(wordIds) {
			System.out.println("exporting "+wordCount);
			JSONObject dictionary = new JSONObject();
			dictionary.put(NUMBER, nextNumber);
			dictionary.put(SIZE, new Long(wordCount));
			dictionary.put(WORD_COUNT, new Long(totalWordCount));
			Iterator<String>itr = wordIds.keyIterator();
			String key;
			while (itr.hasNext()) {
				key = itr.next();
				dictionary.put(key, wordIds.get(key));
			}
				
			dictionary.writeJSONString(out);
			out.flush();
			out.close();
		System.out.println("exported");
		}
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#getDictionary(java.lang.String)
	 * /
	@Override
	public JSONObject getDictionary() {
		return dictionary;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.IDictionary#isEmpty(java.lang.String)
	 */
	@Override
	public boolean isEmpty() {
		synchronized(wordIds) {
			return wordIds.isEmpty();
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
	 * /
	@Override
	public String newNumericId() {
		synchronized(dictionary) {
			String result = nextNumber.toString();
			nextNumber += 1;
			return result;
		}
	}
	*/

	@Override
	public void setNumericId(long newId) {
		nextNumber = new Long(newId);
	}

	@Override
	public long getSize() {
		return this.wordCount;
	}
/*
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
	*/
	
	void save(String filePath, JSONObject jo) throws Exception {
		
		/*File myFile = new File(filePath);
		FileOutputStream fos = new FileOutputStream(myFile);
		GZIPOutputStream gos = new GZIPOutputStream(fos);
		PrintWriter out = new PrintWriter(gos);
		out.println(jo.toJSONString());
		out.flush();
		out.close();*/
	}
}

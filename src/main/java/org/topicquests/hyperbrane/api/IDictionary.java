/*
 * Copyright 2014, 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hyperbrane.api;

import java.io.Writer;

import net.minidev.json.JSONObject;

/**
 * @author park
 * <p>A <em>dictionary</em> is a map which contains two mappings:
 * <li>numeric identity -- individual word, case insensitive</li>
 * <li>individual word, case insensitive -- numeric identity</li></p>
 * <p>The dictionary implementation must be persisted before shutting down;
 * it is an in-memory Map and is not self-persisted.</p>
 * <p>Typically, it is implemented as a JSONObject, and the resulting
 * JSON string is saved as a file and rebooted when needed.</p>
 */
public interface IDictionary {
		
	/**
	 * Return a unique next identifier
	 * @return
	 */
	//String newNumericId();
	
	/**
	 * Should not return <code>null</code>
	 * @param id
	 * @return
	 */
	String getWord(String id);
	
	/**
	 * <p>The intention is to bump numeric identifiers above
	 * a threshold, say, 300000 after bootstrapping. This means
	 * that all words below that identifier were imported and taken
	 * to be spell-checked. Words above that number are imported
	 * while reading.</p>
	 * @param newId
	 */
	void setNumericId(long newId);
	
//	List<String> listSentencesByWordId(String id);
	
//	List<String> listSentencesByWord(String word);
	
	/**
	 * Can return <code>null</code> if word doesn't exist
	 * @param word
	 * @return
	 */
	String getWordId(String word);
	
	/**
	 * Quick test; returns <code>true</code> if nothing in the dictionary
	 * @return
	 */
	boolean isEmpty();

	/**
	 * <p>Returns the identity of a word, even it it's not (yet) in the dictionary<p>
	 * <p>Behavior is this:
	 * <li>If word is not in dictionary, create it and return that ID</li>
	 * <li>Checking for a word entails checking against all lower case</li>
	 * <li>If a word being added is not all lower case, add the word, and add the word as all lower case with same ID</li>
	 * </p>
	 * @param word
	 * @return
	 */
	String getWordIdOrAddWord(String word);
	
	/**
	 * If word does not exist, it will be added with a new Id;
	 * Otherwise, the word's existing Id will be returned
	 * @param word
//	 * @param sentenceId can be <code>null</code>
	 * @return
	 */
	String addWord(String word /*, String sentenceId*/);
	
	
//	void addSentenceIdByWord(String word, String sentenceId);
	
//	void addSentenceIdByWordId(String id, String sentenceId);
	
	void export(Writer out) throws Exception;
	
	/**
	 * Return how many words stored
	 * @return
	 */
	long getSize();
	
	//JSONObject getDictionary();
	
	void saveDictionary(); // throws Exception;
}

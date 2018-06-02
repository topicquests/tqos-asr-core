/*
 * Copyright 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.os.asr.api;

import java.util.*;

import org.topicquests.hyperbrane.api.IWordGram;

/**
 * @author jackpark
 * Ways this can be used:<br/>
 * <p><ul><li>Register any {@link IWordGram that gets changed in SentenceBlackboard
 * operations</li>
 * <li>Register any sentence id that fails to form Triples</li>
 * <li>Call <code>listAllIntersectingSentences/code></li>
 * <li>For each intersectingSentence, take it from the registry and exercise it.</li>
 * <li>If the sentence fails, it will reregister itself</li>,/ul>
 * </p>
 */
public interface IWordGramChangeEventRegistry {

	void registerWordGram(IWordGram g);
	
	void registerSentenceId(String id);
	
	
	int registrySize();
	
	int sentenceSize();
	
	/**
	 * Can return <code>null</code> when empty
	 * @return
	 */
	IWordGram takeNextGram();
	
	String takeNextSentenceId();
	
	void removeSenenceId(String id);
	
	/**
	 * Intersect the sentence Ids associated with <code>g</code> with those
	 * on the registered sentence list
	 * @param g
	 * @return
	 */
	List<String> listUnhandledSentenceIds(IWordGram g);
	
	/**
	 * The primary way to use this
	 * @return
	 */
	Set<String> listAllIntersectingSentenceIds();
	
	/**
	 * Call periodically to prune any wordGram that does not have any sentences
	 * on the sentence list
	 */
	void pruneGrams();
}

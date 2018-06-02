/*
 * Copyright 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.os.asr.reader.api;

import java.util.List;

import org.topicquests.hyperbrane.api.IWordGram;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public interface ISemanticTuple {

	JSONObject getData();
	
	/**
	 * Triples carry <em>identity</em>
	 * @param id
	 */
	void setId(String id);
	String getId();
	
	/**
	 * <code>s</code> can be either {@link ITriple} or {@link IWordGram}
	 * @param s
	 */
	void setSubject(JSONObject s);
	JSONObject getSubject();
	
	void setPredicate(IWordGram p);
	JSONObject getPredicate();
	
	/**
	 * <code>o</code> can be either {@link ITriple} or {@link IWordGram}
	 * @param o
	 */
	void setObject(JSONObject o);
	JSONObject getObject();
	
	void addSentenceId(String id);
	List<String> listSentenceIds();
	
	String toString();
	void setSubjectRole(String roleId);
	String getSubjectRole();
	
	void setObjectRole(String roleId);
	String getObjectRole();
}

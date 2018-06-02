/*
 * Copyright 2014, 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hyperbrane.api;
import java.util.Date;
import java.util.List;

import org.topicquests.ks.tm.api.ITuple;

import net.minidev.json.JSONObject;


/**
 * @author park
 * <p>Note that an <code>INTuple</code> can create a graph
 * since the <code>objectId</code> might be that of another
 * <code>INTuple</code></p>
 */
public interface INTuple {
	//TODO: this code deals with identifiers: need to add actual values
	// which is non-trivial when the actual value might be a graph
	// let's just make it a JSON object
	//void setID(String id);
	//String getID();
	
	/////////////////////////
	// ITuple.setThemeLocator
	// can be used to record the SemanticFrame id which was
	// interpreted to craft this tuple
	/////////////////////////
	
	void setCreatorId(String id);
	String getCreatorId();
	
	void setDate(Date date);
	void setDate(String date);
	Date getDate();
	String getDateString();
	
	void addLabel(String label, String language);
	JSONObject getLabel();
	String getLabel(String language);
	List<String> listLabels();
	List<String> listLabels(String language);
	
	void setNodeType(String typeLocator);
	String getNodeType();
	
	void setProperty(String key, Object value);
	void addPropertyValue(String key, String value);
	Object getProperty(String key);
	void removeProperty(String key);
	void removePropertyValue(String key, String value);
	
	void setSentenceId(String id);
	String getSentenceId();
		
	void setDocumentId(String id);
	String getDocumentId();

	  /**
	   * <p>Object refers to the object of a {subject,predicate,object} triple.</p>
	   * <p>An object could be one of
	   * <li>A literal (typed) value</li>
	   * <li>A symbol, typically an identifier of another entity which could be one of
	   * <li>Another tuple</li>
	   * <li>A node</li></li></p>
	   * @param value
	   */
	  void setObject(String value);
		
	  /**
	   * Set the object's type
	   * @param typeLocator
	   */
	  void setObjectType(String typeLocator);
		
	  /**
	   * Return the object
	   * @return
	   */
	  String getObject();
		
	  /**
	   * Return the object's type
	   * @return
	   */
	  String getObjectType();
		
	  /**
	   * Set the object's role
	   * @param roleLocator
	   */
	  void setObjectRole(String roleLocator);
		
	  /**
	   * Return the object's role
	   * @return can return <code>null</code>
	   */
	  String getObjectRole();
		
	  /**
	   * <p>A subject is the subject in a {subject,predicate,object} triple</p>
	   * <p>A subject is always the locator (identifier) for another entity, which could be one of
	   * <li>A node</li>
	   * <li>A tuple</li></p>
	   * @param locator
	   */
	  void setSubjectLocator(String locator);
		
	  /**
	   * Return the subject locator
	   * @return
	   */
	  String getSubjectLocator();
		
	  /**
	   * SubjectType refers to whether this subject is an ITuple type or some other
	   * type in the typology
	   * @param subjectType
	   */
	  void setSubjectType(String subjectType);
		
	  /**
	   * Return the subject's type
	   * @return
	   */
	  String getSubjectType();
		
	  /**
	   * Roles are appropriate to relations among role-playing actors
	   * @param roleLocator
	   */
	  void setSubjectRole(String roleLocator);
		
	  /**
	   * Return the subject/s role
	   * @return can return <code>null</code>
	   */
	  String getSubjectRole();
	  
	void setObjectLexTypes(List<String>types);
	List<String> listObjectLexTypes();
	
	void setSubjectLexTypes(List<String>types);
	List<String> listSubjectLexTypes();
	
	void setPredicateLexTypes(List<String>types);
	List<String> listPredicateLexTypes();
	
	void setWords(String words);
	String getWords();
	
	 /**
	   * Used only during bootstrap
	   * Scopes are topics
	   * @param scopeLocator
	   */
	  void addScope(String scopeLocator);

	  /**
	   * 
	   * @return 
	   */
	  List<String> listScopes();

	
	JSONObject getData();
	
	String toJSONString();

}

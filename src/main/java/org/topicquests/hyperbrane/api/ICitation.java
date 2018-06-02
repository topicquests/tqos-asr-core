/*
 * Copyright 2012, TopicQuests
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.topicquests.hyperbrane.api;

import java.util.*;

import org.topicquests.ks.tm.api.IProxy;

import net.minidev.json.JSONObject;

/**
 * @author Park
 * @see {@link IBiblioLegend}
 */
public interface ICitation  {
	
	/**
	 * 
	 * @param title
	 * @param language defaults to "en" if <code>null</code>
	 * @param userId TODO
	 */
	void setDocumentTitle(String title, String language, String userId);
	
	/**
	 * 
	 * @param language
	 * @return defaults to "en" if <code>null</code>
	 */
	String getDocumentTitle(String language);
	
	void setAbstract(String text, String language);
	
	String getAbstract(String language);

	/**
	 * 
	 * @param abs
	 * @param language defaults to "en" if <code>null</code>
	 * @param userId TODO
	 */
	//void setDocumentAbstract(String abs, String language, String userId);
	
	/**
	 * 
	 * @param language defaults to "en" if <code>null</code>
	 * @return
	 */
	//String getDocumentAbstract(String language);
	
	/////////////////////////
	//Author locators occur after author become topics
	/////////////////////////
	
	void addAuthor(IAuthor author);
	
	IAuthor getAuthorByLocator(String locator);
	
	List<IAuthor> listAuthorsByLastName(String lastName);
	
	List<IAuthor> listAuthors();
	
	List<String> listAuthorLocators();
	
	///////////////////////////
	// Dealing just with authors
	///////////////////////////
	void setAuthorList(List<IAuthor>authors);
	
	/**
	 * 
	 * @param title TODO
	 * @param initials
	 * @param firstName TODO
	 * @param middleName TODO
	 * @param lastName
	 * @param suffix e.g. 2nd, II, III, etc, can be <code>null</code>
	 * @param degree e.g. M.D., PhD, ... can be <code>null</code>
	 * @param fullName TODO
	 * @param authorLocator TODO
	 * @param publicationName TODO
	 * @param publicationLocator TODO
	 * @param publisherName TODO
	 * @param publisherLocator TODO
	 * @param affiliationName TODO
	 * @param affiliationLocator TODO
	 * @param funderName TODO
	 * @param funderLocator TODO
	 * @param fundingContractId TODO
	 */
	void addAuthor(String title, String initials, String firstName, String middleName, String lastName, String suffix, String degree, String fullName, String authorLocator, String publicationName, String publicationLocator, String publisherName, String publisherLocator, String affiliationName, String affiliationLocator, String funderName, String funderLocator, String fundingContractId);
			
	/////////////////////////////
	// Publications after they are topics in the topic map
	/////////////////////////////
	
	/**
	 * Uses NodeType
	 * @param typeLocator
	 */
	void setPublicationTypeLocator(String typeLocator);
	
	String getPublicationTypeLocator();
	
	void setPublisherLocator(String publisherLocator);
	
	String getPublisherLocator();
	
	void setPublicationLocator(String locator);
	String getPublicationLocator();
	
	/////////////////////////////
	// Publications before they are topics
	/////////////////////////////
	
	void setPublicationType(String type);
	
	String getPublicationType();
		
	void setPublisherName(String name);
	
	String getPublisherName();
	
	void setDOI(String doi);
	
	String getDOI();
	
	void setISBN(String isbn);
	
	String getISBN();
	
	void setISSN(String issn);
	
	String getISSSN();
	
	/**
	 * Has its own field; the paper's title uses INode label field
	 * @param journalTitle
	 */
	void setPublicationTitle(String journalTitle);
	
	/**
	 * 
	 * @return
	 */
	String getPublicationTitle();
	
	void setPublicationDate(Date d);
	
	/**
	 *  
	 * @return can return <code>null</code> if not set
	 */
	Date getPublicationDate();
	
	void setJournalVolume(String vol);
	
	String getJournalVolume();
	
	void setJournalNumber(String num);
	
	String getJournalNumber();
	
	void setPages(String pages);
	
	String getPages();
	
	
	
}

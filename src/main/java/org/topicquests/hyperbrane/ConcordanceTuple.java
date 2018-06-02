/*
 * Copyright 2014, TopicQuests
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
package org.topicquests.hyperbrane;

import java.util.Date;
import java.util.List;

import net.minidev.json.*;

import org.topicquests.hyperbrane.api.INTuple;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.tm.Proxy;
import org.topicquests.os.asr.ASRCoreEnvironment;
import org.topicquests.pg.api.IPostgresConnection;
import org.topicquests.support.api.IResult;

import com.tinkerpop.blueprints.impls.sql.SqlGraph;

/**
 * @author park
 *
 */
public class ConcordanceTuple  implements INTuple {
	private SqlGraph theGraph;
	private JSONObject data;
	
	private final String 
		ID			 			= "id",
		DOC_ID				 	= "docId",
		WORDS					= "words",
		SENTENCE 				= "sentence",
		SUBJECT					= "subject",
		PREDICATE				= "predicate",
		OBJECT					= "object",
		SUBJECT_LEX_TYPES		= "subjLexTypes",
		PREDICATE_LEX_TYPES		= "predLexTypes",
		OBJECT_LEX_TYPES		= "objLexTypes";
	
	/**
	 * 
	 */
	public ConcordanceTuple() {
		data = new JSONObject();
		theGraph = ASRCoreEnvironment.getInstance().getTheGraph();
	}
	
	/**
	 * 
	 * @param jo
	 */
	public ConcordanceTuple(JSONObject jo) {
		data = jo;
		theGraph = ASRCoreEnvironment.getInstance().getTheGraph();
	}


	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#setSentenceId(java.lang.String)
	 */
	@Override
	public void setSentenceId(String id) {
		getData().put(SENTENCE, id);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#getSentenceId()
	 */
	@Override
	public String getSentenceId() {
		return (String)getData().get(SENTENCE);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#setDocumentId(java.lang.String)
	 */
	@Override
	public void setDocumentId(String id) {
		getData().put(DOC_ID,id);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#getDocumentId()
	 */
	@Override
	public String getDocumentId() {
		return (String)getData().get(DOC_ID);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#setSubjectId(java.lang.String)
	 * /
	@Override
	public void setSubjectId(String id) {
		data.put(SUBJECT, id);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#getSubjectId()
	 * /
	@Override
	public String getSubjectId() {
		return (String)data.get(SUBJECT);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#setPredicateId(java.lang.String)
	 * /
	@Override
	public void setPredicateId(String id) {
		data.put(PREDICATE, id);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#getPredicateId()
	 * /
	@Override
	public String getPredicateId() {
		return (String)data.get(PREDICATE);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#setObjectId(java.lang.String)
	 * /
	@Override
	public void setObjectId(String id) {
		data.put(OBJECT, id);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#getObjectId()
	 * /
	@Override
	public String getObjectId() {
		return (String)data.get(OBJECT);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#setObjectLexTypes(java.util.List)
	 */
	@Override
	public void setObjectLexTypes(List<String>types) {
		getData().put(OBJECT_LEX_TYPES, types);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#listObjectLexTypes()
	 */
	@Override
	public List<String> listObjectLexTypes() {
		return (List<String>)getData().get(OBJECT_LEX_TYPES);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#setSubjectLexTypes(java.util.List)
	 */
	@Override
	public void setSubjectLexTypes(List<String>types) {
		getData().put(SUBJECT_LEX_TYPES, types);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#listSubjectLexTypes()
	 */
	@Override
	public List<String> listSubjectLexTypes() {
		return (List<String>)getData().get(SUBJECT_LEX_TYPES);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#setPredicateLexTypes(java.util.List)
	 */
	@Override
	public void setPredicateLexTypes(List<String>types) {
		getData().put(PREDICATE_LEX_TYPES, types);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#listPredicateLexTypes()
	 */
	@Override
	public List<String> listPredicateLexTypes() {
		return (List<String>)getData().get(PREDICATE_LEX_TYPES);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#setTupleJSON(java.lang.String)
	 * /
	@Override
	public void setTupleJSON(String json) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.concordance.api.ITuple#getTupleJSON()
	 * /
	@Override
	public String getTupleJSON() {
		return data.toJSONString();
	}

//	@Override
//	public JSONObject getMap() {
//		return data;
//	}

//	@Override
//	public String toJSON() {
//		return data.toJSONString();
//	}
//	@Override
//	public void setType(String parentId) {
//		data.put(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE, parentId);
//	}

//	@Override
//	public String getType() {
//		return (String)data.get(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE);
//	}
*/
	@Override
	public void setWords(String words) {
		getData().put(WORDS, words);
	}

	@Override
	public String getWords() {
		return (String)getData().get(WORDS);
	}


	@Override
	public void setObject(String value) {
	    data.put(ITQCoreOntology.TUPLE_OBJECT_PROPERTY, value);
	}

	@Override
	public void setObjectType(String typeLocator) {
	    data.put(ITQCoreOntology.TUPLE_OBJECT_TYPE_PROPERTY, typeLocator);
	}

	@Override
	public String getObject() {
	    return data.getAsString(ITQCoreOntology.TUPLE_OBJECT_PROPERTY);
	}
	  @Override
	  public String getObjectType() {
	    return (String)data.get(ITQCoreOntology.TUPLE_OBJECT_TYPE_PROPERTY);
	  }

	  @Override
	  public void setObjectRole(String roleLocator) {
	    data.put(ITQCoreOntology.TUPLE_OBJECT_ROLE_PROPERTY, roleLocator);
	  }

	  @Override
	  public String getObjectRole() {
	    return (String)data.get(ITQCoreOntology.TUPLE_OBJECT_ROLE_PROPERTY);
	  }

	  @Override
	  public void setSubjectLocator(String locator) {
	    data.put(ITQCoreOntology.TUPLE_SUBJECT_PROPERTY, locator);
	  }

	  @Override
	  public String getSubjectLocator() {
	    return (String)data.get(ITQCoreOntology.TUPLE_SUBJECT_PROPERTY);
	  }

	  @Override
	  public void setSubjectType(String subjectType) {
	    data.put(ITQCoreOntology.TUPLE_SUBJECT_TYPE_PROPERTY, subjectType);
	  }

	  @Override
	  public String getSubjectType() {
	    return (String)data.get(ITQCoreOntology.TUPLE_SUBJECT_TYPE_PROPERTY);
	  }

	  @Override
	  public void setSubjectRole(String roleLocator) {
	    data.put(ITQCoreOntology.TUPLE_SUBJECT_ROLE_PROPERTY, roleLocator);
	  }

	  @Override
	  public String getSubjectRole() {
	    return (String)data.get(ITQCoreOntology.TUPLE_SUBJECT_ROLE_PROPERTY);
	  }


	@Override
	public void addScope(String scopeLocator) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<String> listScopes() {
		return (List<String>)data.get(ITQCoreOntology.SCOPE_LIST_PROPERTY_TYPE);
	}



	@Override
	public void setCreatorId(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCreatorId() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setDate(Date date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDate(String date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDateString() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public String toJSONString() {
		return data.toJSONString();
	}



	@Override
	public void addLabel(String label, String language) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JSONObject getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> listLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> listLabels(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public void setNodeType(String typeLocator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getNodeType() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setProperty(String key, Object value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addPropertyValue(String key, String value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Object getProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void removeProperty(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePropertyValue(String key, String value) {
		// TODO Auto-generated method stub
		
	}

}

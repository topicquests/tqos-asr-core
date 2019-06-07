/*
 * Copyright 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.os.asr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.topicquests.blueprints.pg.BlueprintsPgEnvironment;
import org.topicquests.hyperbrane.ConcordanceDocument;
import org.topicquests.hyperbrane.ConcordanceParagraph;
import org.topicquests.hyperbrane.ConcordanceSentence;
import org.topicquests.hyperbrane.ConcordanceWordGram;
import org.topicquests.hyperbrane.WordGramCache;
import org.topicquests.hyperbrane.api.IDictionary;
import org.topicquests.hyperbrane.api.IDocument;
import org.topicquests.hyperbrane.api.IHyperMembraneOntology;
import org.topicquests.hyperbrane.api.IParagraph;
import org.topicquests.hyperbrane.api.ISentence;
import org.topicquests.hyperbrane.api.IWordGram;
import org.topicquests.ks.api.ICoreIcons;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.os.asr.api.IASRCoreModel;
import org.topicquests.os.asr.api.IDictionaryClient;
import org.topicquests.os.asr.api.IDictionaryEnvironment;
import org.topicquests.os.asr.api.IDocumentProvider;
import org.topicquests.os.asr.api.ISentenceProvider;
import org.topicquests.os.asr.api.IStatisticsClient;
import org.topicquests.os.asr.common.api.IASRFields;
import org.topicquests.os.asr.reader.wordnet.WordNetUtility;
import org.topicquests.os.asr.reader.wordnet.api.IWordnetModel;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.sql.SqlGraph;
import com.tinkerpop.blueprints.impls.sql.SqlVertex;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class ASRCoreModel implements IASRCoreModel {
	protected ASRCoreEnvironment environment;
	private BlueprintsPgEnvironment graphEnvironment;
	private IDictionary dictionary;
	protected WordGramCache wgCache;
	private SqlGraph theGraph;
	private IWordnetModel wordnetModel;
	protected IDocumentProvider documentProvider;
	protected ISentenceProvider sentenceProvider;
	protected IStatisticsClient stats;
	private IDictionaryEnvironment dictionaryEnvironment;

	/**
	 * 
	 */
	public ASRCoreModel(ASRCoreEnvironment env) {
		environment = env;
		stats = environment.getStats();
		dictionaryEnvironment = environment.getDictionaryEnvironment();
		environment.logDebug("ASRCoreModel- "+env);
		environment.logDebug("ASRCoreModel-- "+env.getDocProvider());

		graphEnvironment = environment.getGraphEnvironment();
		theGraph = environment.getTheGraph();
		wgCache = environment.getWordGramCache();
		dictionary = environment.getDictionary();
		environment.logDebug("ASRCoreModel-1 "+env);
		documentProvider = environment.getDocProvider();
		environment.logDebug("ASRCoreModel-2 "+documentProvider);
		sentenceProvider = environment.getSentenceProvider();
		environment.logDebug("ASRCoreModel+ "+documentProvider+" "+sentenceProvider);
	}

	@Override
	public IResult updateWordGram(IWordGram wg) {
		IResult result = new ResultPojo();
		//it's in the wgCache -- should not need to store it.
		//but just in case
		if (wgCache.get((String)wg.getId()) == null)
			wgCache.add((String)wg.getId(), wg);
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.api.IASRCoreModel#wordGramId(java.util.List)
	 */
	@Override
	public String wordGramId(List<String> wordIds) {
		environment.logDebug("Gramolizer.wordGramId "+wordIds);
		int len = wordIds.size();
		if (len == 1)
			return singletonId(wordIds.get(0));
		StringBuilder buf = new StringBuilder();
		//must avoid adding a dot if word already has one
		boolean hasDot = false;
		String ix;
		for (int i=0;i<len;i++) {
			ix = wordIds.get(i);
			hasDot = ix.endsWith(".");
			if (i > 0 && !hasDot)
				buf.append(".");
			buf.append(ix);
			hasDot = false;
		}
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.api.IASRCoreModel#singletonId(java.lang.String)
	 */
	@Override
	public String singletonId(String wordId) {
		return wordId+".";
	}

	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.api.IASRCoreModel#addWord(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String addWord(String word, String sentenceId, String userId, String lexType) {
		//This is the return object; it is the wordId, not a gramId
		String result = dictionary.getWordId(word);
		environment.logDebug("ASRCoreModel.addWord- "+word+" "+result);
		////////////////////
		//There is a scenario in which the word exists, but is not yet in
		// a WordGram form
		String gramId = null;
		try {
			IWordGram g;
			IResult r;
			JSONObject jo;
			if (result == null) {
				//it's a new word, result = id
				result = dictionary.addWord(word);
				environment.logDebug("ASRCoreModel.addWord-1 "+word+" "+result);
				//MUST SEE IF WE HAVE THIS YET?
				g = this.newTerminal(result, userId, null, null);
				g.setWords(word);
				stats.addToKey(IASRFields.WG1);
				if (sentenceId != null)
					g.addSentenceId(sentenceId);
				if (lexType != null)
					g.addLexType(lexType);
				gramId = (String)g.getId();
				wgCache.add(gramId, g);
				
				environment.logDebug("ASRCoreModel.addWord-2 "+word+" "+g.getWords());
			} else {
				//The word exists, but needs to be counted
				stats.addToKey(IASRFields.WORDS_READ);
				gramId = this.singletonId(result);
				
				//get it as a singleton
				g = wgCache.get(gramId);
				environment.logDebug("ASRCoreModel.addWord "+result+" "+g);
				if (g != null) {
					//already exists
					if (lexType != null)
						g.addLexType(lexType);

					if (sentenceId != null) {
						g.addSentenceId(sentenceId);
//						wgCache.add(gramId, g);
					}
				} else {
					g = this.newTerminal(result, userId, null, null);
					if (sentenceId != null)
						g.addSentenceId(sentenceId);
					gramId = (String)g.getId();
					wgCache.add(gramId, g);

				}
			}
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		}
		return gramId;
	}

	/////////////////////////////
	//TODO
	// It is not clear that each id in <code>wordIds</code> corresponds to
	// a terminal
	/////////////////////////////
	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.api.IASRCoreModel#addWordGram(java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public IWordGram addWordGram(List<String> wordIds, String sentenceId, String userId, String topicLocator, String lexType) {
//System.out.println("ASRMa "+wordIds+" | "+sentenceId);
		String gramId = this.wordGramId(wordIds);
		IWordGram result = null;
			result = wgCache.get(gramId);
//			System.out.println("ASRMa-1 "+result);
			if (result == null) {
				//is new
				result = newWordGram(wordIds, userId, topicLocator, lexType);
				if (sentenceId != null) 
					result.addSentenceId(sentenceId);
				//database.putWordGram(result);
				
			} else {
				if (sentenceId != null) {
					result.addSentenceId(sentenceId);
				}
				if (topicLocator != null) {
					result.addTopicLocator(topicLocator);
				}
				if (lexType != null)
					result.addLexType(lexType);

			}
			wgCache.add(gramId, result);
			
			environment.logDebug("ADDWG "+topicLocator+" "+result.getWords());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.api.IASRCoreModel#wordGramId2WordIds(java.lang.String)
	 */
	@Override
	public List<String> wordGramId2WordIds(String wordGramId) {
		List<String>result = new ArrayList<String>();
		String [] words = wordGramId.split(".");
		int len = words.length;
		for (int i=0;i<len;i++)
			result.add(new String(words[i]));
		return result;
	}


	@Override
	public IWordGram newTerminal(String wordId, String userId, String topicLocator, String lexType) {
		String gramId = wordId.trim();
		int where = gramId.indexOf(".");
		if (where < 0)
			gramId = this.singletonId(wordId);
		environment.logDebug("ASRCoreModel.newTerminal- "+wordId+" "+gramId+" "+where);
		IWordGram result = this.getThisWordGram(gramId);
		environment.logDebug("ASRCoreModel.newTerminal "+gramId+" "+result);
		if (result == null) {
			SqlVertex v = (SqlVertex)theGraph.addVertex(gramId);
			result = new ConcordanceWordGram(v, environment);
			result.setGramType(IWordGram.COUNT_1);
			result.markIsNew();
			if (topicLocator != null)
				result.addTopicLocator(topicLocator);
			if (lexType != null)
				result.addLexType(lexType);
			result.setWords(dictionary.getWord(wordId));
			//pass it through wordnet turned off for now -- it's messed up
			//wordnetModel.processTerminal(result);
		} else {
			if (lexType != null)
				result.addLexType(lexType);
			if (topicLocator != null)
				result.addTopicLocator(topicLocator);
		}
		wgCache.add(gramId, result);
		return result;
	}

	@Override
	public IWordGram newWordGram(List<String> wordIds, String userId, String topicLocator, String lexType) {
		environment.logDebug("ASRCoreModel.newWordGram- "+wordIds);
		///////////////////
		// MODIFIED to return <code>null</code> if too long
		String gramId = this.wordGramId(wordIds);
		if (wordIds.size() > 8) {
			environment.logError("ASRCoreModel.newWordGram too many words "+wordIds, null);
			//throw new RuntimeException("ASRCoreModel.newWordGram too many words "+wordIds);
			return null;
		}
		SqlVertex v = (SqlVertex)theGraph.addVertex(gramId);

		IWordGram result = new ConcordanceWordGram(v, environment);
		result.setGramType(wordGramIdsToCountString(wordIds.size()));
		result.markIsNew();
		environment.logDebug("ASRCoreModel.newWordGram-1 "+gramId+" "+wordIds.size()+" "+result.getGramType());
		if (topicLocator != null)
			result.addTopicLocator(topicLocator);
		if (lexType != null)
			result.addLexType(lexType);

		StringBuilder buf = new StringBuilder();
		for (String idx:wordIds) {
			buf.append(this.dictionary.getWord(idx)+" ");
		}
		result.setWords(buf.toString().trim());
		String t = result.getGramType().trim();
		//NOTE: COUNT_1 seems to fail a lot: bad count errors on "singleton"
		//added trim(), and added sanity hack
		//TODO spend more time figuring out why singleton fails this test
		environment.logDebug("ASRCoreModel.newWordGram-1 "+t+" "+IWordGram.COUNT_1);
		if (t.equals(IWordGram.COUNT_1) || (wordIds.size()==1))
			stats.addToKey(IASRFields.WG1);
		else if (t.equals(IWordGram.COUNT_2)) //was missing else
			stats.addToKey(IASRFields.WG2);
		else if (t.equals(IWordGram.COUNT_3))
			stats.addToKey(IASRFields.WG3);
		else if (t.equals(IWordGram.COUNT_4))
			stats.addToKey(IASRFields.WG4);
		else if (t.equals(IWordGram.COUNT_5))
			stats.addToKey(IASRFields.WG5);
		else if (t.equals(IWordGram.COUNT_6))
			stats.addToKey(IASRFields.WG6);
		else if (t.equals(IWordGram.COUNT_7))
			stats.addToKey(IASRFields.WG7);
		else if (t.equals(IWordGram.COUNT_8))
			stats.addToKey(IASRFields.WG8);
		else {
			String msg = "ASRCoreModel.newWordGram bad count: "+gramId+" | "+t;
			environment.logError(msg, null);
			//environment.getEventRegistry().addWordGramEvent(IWordGramEvent.BAD_WORDGRAM, gramId);
		}
		environment.logDebug("ASRCoreModel.newWordGram+ "+result.getWords());
		return result;
	}

	///////////////////////////
	//Utilities
	private String wordGramIdsToCountString(int count) {
		switch(count) {
		case 1:
			return IWordGram.COUNT_1;
		case 2:
			return IWordGram.COUNT_2;
		case 3:
			return IWordGram.COUNT_3;
		case 4:
			return IWordGram.COUNT_4;
		case 5:
			return IWordGram.COUNT_5;
		case 6:
			return IWordGram.COUNT_6;
		case 7:
			return IWordGram.COUNT_7;
		case 8:
			return IWordGram.COUNT_8;
		}
		return "badshit";
	}

	@Override
	public IWordGram getWordGram(String id) {
		IWordGram g = wgCache.get(id);
		environment.logDebug("ASRCoreModel.getWordGram- "+id+" "+g);
		if (g != null && g.getRedirectToId() != null) {
			environment.logDebug("ASRCoreModel.getWordGram-1 "+id+" "+g);
			return getThisWordGram(g.getRedirectToId());
		}
		environment.logDebug("ASRCoreModel.getWordGram+ "+id+" "+g);
		return g;
	}

	@Override
	public IWordGram getThisWordGram(String id) {
		return wgCache.getThis(id);
	}

	@Override
	public IWordGram getThisWordGramByWords(String wordx) {
		String words = wordx.trim();
		environment.logDebug("ASRCoreModel.getThisWordGramByWords- "+words);
		IWordGram result = null;
		String id;
		if (words.indexOf(' ') > -1) {
			String [] wx = words.split(" ");
			StringBuilder buf = new StringBuilder();
			List<String>ids = new ArrayList<String>();
			int counter = 0;
			for (String word:wx) {
				
				id = dictionary.addWord(word.trim());
				ids.add(id);
				if (counter++ > 0)
					buf.append(".");
				buf.append(id);
			}
			result = getThisWordGram(buf.toString());
			environment.logDebug("ASRCoreModel.getThisWordGramByWords "+buf.toString()+" "+ids+" "+result);
			if (result == null)
				result = this.addWordGram(ids, null, "SystemUser", null, null);
		} else {
			id = dictionary.addWord(words);
			result = getThisWordGram(this.singletonId(id));
			if (result == null)
				result = this.newTerminal(id, "SystemUser", null, null);
			environment.logDebug("ASRCoreModel.getThisWordGramByWords "+id+" "+result);
		}
		return result;
	}



	@Override
	public boolean existsWordGram(String id) {
		return wgCache.getThis(id) != null;
	}

	@Override
	public String wordsToGramId(String words) {
		String [] wx = words.trim().split(" ");
		List<String>l = new ArrayList<String>();
		for (String w:wx) {
			l.add(dictionary.getWordId(w.trim()));
		}
		return this.wordGramId(l);
	}

	@Override
	public IWordGram generateWordGram(String label, String userId, String topicLocator) {
		environment.logDebug("ASRCoreModel.generate- "+label+" "+topicLocator);
		IWordGram result = null;
		String lbl = label.trim();
		if (!lbl.equals("")) {
			int where = lbl.indexOf(' ');
			String id;
			if (where == -1) {
				id = this.addWord(lbl, null, userId, null);
				result = wgCache.getThis(id);
				if (topicLocator != null && !topicLocator.equals(""))
					result.addTopicLocator(topicLocator);
			} else {
				String [] temp = lbl.split(" ");
				int len = temp.length;
				List<String>l = new ArrayList<String>();
				String w;
				for (int i=0;i<len;i++) {
					w = new String(temp[i].trim());
					id = this.addWord(w, null, userId, null);
					if (i < 8)
						l.add(id);
				}
				result = this.addWordGram(l, null, userId, topicLocator, null);
			}
		}
		return result;
	}

	@Override
	public void setWordnetModel(IWordnetModel m) {
		wordnetModel = m;
	}
	
	@Override
	public ISentence newSentence(String documentLocator, String sentence, String userId) {
		ISentence result = new ConcordanceSentence();
		result.setCreatorId(userId);
		Date d = new Date();
		result.setID(UUID.randomUUID().toString());
		result.setCreatorId(userId);
		result.setDate(d);
		result.setLastEditDate(d);
		result.setSentence(sentence);
		result.setDocumentId(documentLocator);
		stats.addToKey(IASRFields.DOCS_IMPORTED);
		return result;
	}

	@Override
	public IResult putSentence(ISentence s) {
		return sentenceProvider.putSentence(s);
	}

	@Override
	public IDocument newDocument(String documentLocator, String documentType, String userId) {
		IResult r = documentProvider.getDocument(documentLocator, null);
		IDocument result = (IDocument)r.getResultObject();
		environment.logDebug("ASRModel.newDocument- "+result);
		if (result == null) {
			result = new ConcordanceDocument();
			Date d = new Date();
			result.setId(UUID.randomUUID().toString());
			result.setTopicLocator(documentLocator);
			result.setCreatorId(userId);
			result.setDate(d);
			result.setLastEditDate(d);
			result.setNodeType(documentType);
			stats.addToKey(IASRFields.DOCS_IMPORTED);
		}
		return result;
	}

	@Override
	public IResult getSentence(String sentenceId) {
		return sentenceProvider.getSentence(sentenceId);
	}

	@Override
	public IResult putDocument(IDocument doc) {
		environment.logDebug("ASRModel.putDocument "+doc.getData().toJSONString());
		return documentProvider.putDocument(doc);
	}

	@Override
	public IResult getDocument(String locator, ITicket credentials) {
		return documentProvider.getDocument(locator, credentials);
	}

	@Override
	public IParagraph newParagraph(String paragraph, String language) {
		IParagraph result = new ConcordanceParagraph();
		result.setParagraph(paragraph, language);
		result.setID(UUID.randomUUID().toString());
		return result;
	}
	@Override
	public IResult updateSentence(ISentence s) {
		environment.logDebug("ASRCoreModel.updateSentence "+s.getData().toJSONString());
		return sentenceProvider.updateSentence(s);
	}

	@Override
	public IResult updateDocument(IDocument doc) {
		return documentProvider.updateDocument(doc);
	}


}

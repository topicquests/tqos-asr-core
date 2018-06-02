/**
 * 
 */
package org.topicquests.os.asr.reader.sent;

import java.util.*;

import org.topicquests.hyperbrane.api.ISentence;
import org.topicquests.hyperbrane.api.IWordGram;
import org.topicquests.os.asr.ASRCoreEnvironment;
import org.topicquests.os.asr.api.IASRCoreModel;
import org.topicquests.os.asr.api.IWordGramChangeEventRegistry;
import org.topicquests.os.asr.dbpedia.SpotlightUtil;
import org.topicquests.os.asr.reader.api.ISemanticTuple;
import org.topicquests.os.asr.reader.doc.DocumentBlackboard;
import org.topicquests.os.asr.reader.para.ParagraphBlackboard;
import org.topicquests.support.util.Tracer;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class SentenceBlackboard {
	private ASRCoreEnvironment environment;
	private IASRCoreModel model;
	private Tracer tracer;
	private ISentence _sentence;
	private IWordGramChangeEventRegistry changeRegistry;
	private DocumentBlackboard docBlackboard;
	private ParagraphBlackboard paraBlackboard;
	protected Sentence_Structure _struct;
	private String sentenceId;
	/** A list of lists of patterns associated with every WordGram
	 * in this sentence
	 */
	private List<List<String>> sentencePattern;
	private String triplePattern;
//	private List<IWordGram> nouns;
//	private List<IWordGram> verbs;
	private List<ISemanticTuple> triples;
	private List<IWordGram> terminals;
//	private List<IWordGram> topics;
	//moved to ISentence
//	private List<IWordGram> workingSentence;
	private Patternizer patternizer;
	private boolean isInitialized = false;
	private boolean containsParens = false;
	private boolean containsBrackets = false;
	private boolean hasSemicolon = false;
	private JSONObject dbPedia;
	private SpotlightUtil dbPediaUtil;
	private List<String>dbpIds;
	private boolean validWorkingSentence = false;
	private List<String>semanticFrameIds;
	
	private Object [] _workingSentence1;

	/**
	 * 
	 */
	public SentenceBlackboard(ASRCoreEnvironment env, ISentence sentence) {
		environment = env;
		model = environment.getCoreModel();
		changeRegistry = environment.getWordGramChangeEventRegistry();
		_sentence = sentence;
		checkForParens();
		checkForSemicolon();
		triples = new ArrayList<ISemanticTuple>();
		patternizer = new Patternizer(this);
		isInitialized = false;
		semanticFrameIds = new ArrayList<String>();
	}
		
	
	void checkForSemicolon() {
		if (_sentence.getSentence().indexOf(';') > -1)
			setHasSemicolon(true);
	}

	public void setDocumentBlackboard(DocumentBlackboard dBB) {
		docBlackboard = dBB;
	}
	
	/**
	 * Can return <code>null</code> on single-sentence debug runs
	 * @return
	 */
	public DocumentBlackboard getDocumentBlackboard() {
		return docBlackboard;
	}
	
	public void setParagraphBlackboard(ParagraphBlackboard pBB) {
		paraBlackboard = pBB;
	}
	
	/**
	 * Can return <code>null</code> on single-sentence debug runs
	 * @return
	 */
	public ParagraphBlackboard getParagraphBlackboard() {
		return paraBlackboard;
	}

	public ISentence getTheSentence() {
		return _sentence;
	}

	/**
	 * WorkingSentenceA is a kind of scratchpad for collecting
	 * all that is known about a sentence early in the reading cycle
	 * @param ws
	 */
	public void setWorkingSentenceA(Object [] ws) {
		_workingSentence1 = ws;
	}
	
	public Object [] getWorkingSentenceA() {
		return _workingSentence1;
	}
	
	/**
	 * Called from SentencePattern.populateFromSentencePattern
	 * which means that the sentence has been fully populated
	 * @param t
	 */
	public void setWorkingSentenceAValidity(boolean t) {
		validWorkingSentence = t;
	}

	public boolean workingSentenceAIsValid() {
		return validWorkingSentence;
	}
	
	public void setHasSemicolon(Boolean t) {
		hasSemicolon = t;
	}
	
	public boolean getHasSemicolon() {
		return hasSemicolon;
	}
	//////////////////////////////////
	// DbPedia Theory
	// After we have mapped a sentence to its wordgams,
	// we can pass the sentence to the SpotliteClient and
	// possibly get the DbPedia annotations on it.
	// IF we have annotations,
	//  we can then find all the wordgrams for which they relate
	//  and add the DbPedia URI to them.
	// When a wordgram has a DbPedia URI, it's as good as a topic
	//  In fact, it might already be a topic;
	//  If not, it will soon become a topic.
	// We then can read the DbPedia document, if we haven't already,
	//  and add to our knowledge base.
	///////////////////////////////////

	public void setDbPediaJSON(JSONObject jo) {
		dbPedia = jo;
		dbPediaUtil = new SpotlightUtil(jo);
	}
	
	public JSONObject getDbPediaJSON() {
		return dbPedia;
	}
	
	public void setDbPediaGramIds(List<String> d) {
		this.dbpIds = d;
	}
	
	/**
	 * Can return <code>null</code>
	 * @return
	 */
	public List<String> getDbPediaGramIds() {
		return dbpIds;
	}
	/**
	 * Can return <code>null</code>
	 * @param term
	 * @return
	 */
	public JSONObject getDbPediaTerm(String term) {
		if (dbPedia == null) return null;
		return dbPediaUtil.findTerm(term);
	}
	
	/**
	 * Can return <code>null</code>
	 * @return
	 */
	public List<JSONObject> listDbPediaJSON() {
		if (dbPedia == null) return null;
		return dbPediaUtil.listResources();
	}
	///////////////////////////
	// SemanticFrames
	///////////////////////////
	
	public List<String> listFrameIds() {
		return semanticFrameIds;
	}
	public void addFrameId(String id) {
		if (!semanticFrameIds.contains(id))
			semanticFrameIds.add(id);
	}
	public boolean hasFrames() {
		return !this.semanticFrameIds.isEmpty();
	}
	///////////////////////////
	// Utilities
	///////////////////////////
	
	void checkForParens() {
		String sentence = _sentence.getSentence();
		setContainsParens(sentence.indexOf('(') > -1);
		//environment.logDebug("PARENS "+t+" "+sentence);
	}
	void checkForBrackets() {
		String sentence = _sentence.getSentence();
		setContainsBrackets(sentence.indexOf('[') > -1);
	}
	public void setContainsParens(boolean t) {
		containsParens = t;
	}
	public void setContainsBrackets(boolean t) {
		containsBrackets = t;
	}
	
	/**
	 * If a sentence contains parens, it might contain metadata
	 * @return
	 */
	public boolean containsParens() {
		return containsParens;
	}
	
	public boolean containsBrackets() {
		return containsBrackets;
	}
	public void setSentenceStructure(Sentence_Structure s) {
		_struct = s;
	}
	
	
	public Sentence_Structure getSentenceStructure() {
		return _struct;
	}
	
	public void setTracer(Tracer t) {
		tracer = t;
	}
	
	public void trace(String message) {
		tracer.trace(System.currentTimeMillis(), message);
	}
	
	public void registerChangedWordGram(IWordGram g) {
		changeRegistry.registerWordGram(g);
	}
	
	/**
	 * Returns <code>false</code> if unable to make a valid triple pattern
	 * @return
	 */
	public boolean updateTriplePattern() {
		boolean result = true;
		if (workingSentenceAIsValid())
			triplePattern = patternizer.toTriplePattern(_sentence.getWorkingSentence());
		else {
			triplePattern = null;
			result = false;
		}
		return result;
	}
	
	/**
	 * Can return <code>null</code>
	 * @return
	 */
	public String getTriplePattern() {
		return triplePattern;
	}
	
	public void updateFullPattern() {
		sentencePattern = patternizer.toFullPattern(_struct);
	}
	
	public List<List<String>> getSentencePattern() {
		return sentencePattern;
	}
	
	public void setSentenceId(String id) {
		sentenceId = id;
	}
	
	public String getSentenceId() {
		return sentenceId;
	}
	/**
	 * The first rule in a chain causes {@link BaseMethod} to initialize
	 * the SentenceBlackboard. Others don't need to repeat that.
	 */
	public void setIsInitialized() {
		isInitialized = true;
	}
	
	public boolean getIsInitialized() {
		return isInitialized;
	}
	public void setTerminals(List<IWordGram>t) {
		terminals = t;
	}
	
	public List<IWordGram> listTerminals() {
		return terminals;
	}
	
	public void setWorkingSentence(List<IWordGram>t) {
		_sentence.setWorkingSentence(t);
	}
	
	/**
	 * Can return <code>null</code> if it doesn't exist yet
	 * @return
	 */
	public List<IWordGram> getWorkingSentence() {
		List<IWordGram> result = _sentence.getWorkingSentence();
		if (result == null) {
			List<String>wgids = _sentence.getWorkingSentenceIds();
			if (wgids != null) {
				Iterator<String>itr = wgids.iterator();
				result = new ArrayList<IWordGram>();
				IWordGram wg;
				String id;
				while (itr.hasNext()) {
					id = itr.next();
					if (id != null) {
						wg = model.getThisWordGram(id);
						result.add(wg);
					} else
						result.add(null);
				}
				//hydrate list and return it to the sentence
				_sentence.setWorkingSentence(result);
			}
		}
		return result;
	}
	
	/**
	 * <p>Migrate local workingSentence to final
	 * workingSentence in {@link SentenceBlackboard}</p>
	 * <p>That final workingSentence represents <em>all</em>
	 * that we know about the sentence at this time.</p>
	 * <p>Subsequent processing will update the final workingSentence
	 * until either some triples are formed, or parsing failure occurs.</p.
	 */
	public boolean finalizeWorkingSentence() {
		if (!workingSentenceAIsValid())
			return false;
		int len = getWorkingSentenceA().length;
		List<IWordGram> ws = new ArrayList<IWordGram>();
		Object o;
		for (int i=0;i<len;i++) {
			o = getWorkingSentenceA()[i];
			if (o instanceof IWordGram)
				ws.add((IWordGram)o);
			//TODO what if it's still a String "unk" ?
			// meaning populating WorkingSentenceA did not fill in all the blanks
		}
		setWorkingSentence(ws);
		return true;
	}

	
//	public void setVerbs(List<IWordGram>v) {
//		verbs = v;
//	}
//	public void addVerb(IWordGram v) {
//		verbs.add(v);
//	}

//	public void addNoun(IWordGram n) {
//		nouns.add(n);
//	}
	
//	public void setTopics(List<IWordGram> t) {
//		topics = t;
//	}
	
//	public void addTopic(IWordGram t) {
//		topics.add(t);
//	}
	
//	public List<IWordGram> listTopics() {
//		return topics;
//	}
	
//	public void setNouns(List<IWordGram> n) {
//		nouns = n;
//	}

	public void addSemanticTuple(ISemanticTuple t) {
		triples.add(t);
	}

//	public List<IWordGram> listVerbs() {
//		return verbs;
//	}
	
//	public List<IWordGram> listNouns() {
//		return nouns;
//	}
	
	public List<ISemanticTuple> listTriples() {
		return triples;
	}
	
//	public ITriple newTriple() {
//		return new SemanticTuple(UUID.randomUUID().toString());
//	}
}

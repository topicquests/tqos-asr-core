/**
 * 
 */
package org.topicquests.os.asr.reader.doc;

import java.util.*;
import org.topicquests.hyperbrane.api.IDocument;
import org.topicquests.os.asr.ASRCoreEnvironment;
import org.topicquests.support.util.Tracer;

/**
 * @author jackpark
 * <p>A Blackboard for whole document reading</p>
 */
public class DocumentBlackboard {
	private ASRCoreEnvironment environment;
	private Tracer tracer;
	private IDocument _document;
	private String _thisUserId;
	private Set<String>frameIds;
	
	/**
	 * 
	 */
	public DocumentBlackboard(ASRCoreEnvironment env, IDocument doc, String userId) {
		environment = env;
		_document = doc;
		_thisUserId = userId;
		frameIds = new HashSet<String>();
	}
	///////////////////////////////
	//Should contain stacks for things like:
	//  Subjects of triples
	//  Objects of triples
	//		Those for anaphoric reference
	//  MAYBE ACCUMULATE ExpectationFailures?
	//TODO
	//  What else?
	///////////////////////////////
	
	public IDocument getTheDocument() {
		return _document;
	}
	
	public String getThisUserId() {
		return _thisUserId;
	}
	
	public void addSynonyms(String sourceGramId, String targetGramId) {
		//TODO
	}
	
	public String getSourceSynonymId(String targetGramId) {
		//TODO
		return null;
	}
	
	public String getTargetSynonymId(String sourceGramId) {
		//TODO
		return null;
	}
	
	/**
	 * <code>ids</code> are collected during sentence reading
	 * @param ids
	 */
	public void addFrameIds(List<String> ids) {
		frameIds.addAll(ids);
	}
	
	public Iterator<String> listFrameIds() {
		return frameIds.iterator();
	}
}

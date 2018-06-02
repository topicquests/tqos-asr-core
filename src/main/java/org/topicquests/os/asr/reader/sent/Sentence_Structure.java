/**
 * 
 */
package org.topicquests.os.asr.reader.sent;

import java.util.*;

import org.topicquests.hyperbrane.api.ISentence;
import org.topicquests.hyperbrane.api.IWordGram;
import org.topicquests.os.asr.ASRCoreEnvironment;
import org.topicquests.os.asr.api.IASRCoreModel;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class Sentence_Structure {
	private ASRCoreEnvironment environment;
	private IASRCoreModel model;

	public IWordGram thePredicate;
	public Object _LHS;
	public Object _RHS;
	public List<IWordGram>inversePredicates;
	public List<IWordGram>predicates;
	public List<IWordGram>terminals;
	public List<IWordGram>pairs;
	public List<IWordGram>triples;
	public List<IWordGram>quads;
	public List<IWordGram>fivers;
	public List<IWordGram>sixers;
	public List<IWordGram>seveners;
	public List<IWordGram>eighters;
	
	
	/**
	 * 
	 */
	public Sentence_Structure(ASRCoreEnvironment env, ISentence s) {
		environment = env;
		model = environment.getCoreModel();
		terminals = new ArrayList<IWordGram>();
		pairs = new ArrayList<IWordGram>();
		triples = new ArrayList<IWordGram>();
		quads = new ArrayList<IWordGram>();
		fivers = new ArrayList<IWordGram>();
		sixers = new ArrayList<IWordGram>();
		seveners = new ArrayList<IWordGram>();
		eighters = new ArrayList<IWordGram>();
		List<String>l = s.listWordGramIds();
		if (l != null && !l.isEmpty()) {
			IWordGram g;
			int t;
			Iterator<String>itr = l.iterator();
			while (itr.hasNext()) {
				g = model.getThisWordGram(itr.next());
				t = g.getGramSize();
				plug(t, g);
			}
			
		}
	}
	
	void plug(int siz, IWordGram g) {
		switch (siz) {
        case 1:  terminals.add(g);
                 break;
        case 2:  terminals.add(g);
        	break;
        case 3:  terminals.add(g);
        	break;
        case 4:  terminals.add(g);
        	break;
        case 5:  terminals.add(g);
        	break;
        case 6:  terminals.add(g);
        	break;
        case 7:  terminals.add(g);
        	break;
        case 8:  terminals.add(g);
        	break;
        default: environment.logError("SentenceStruct.plug bad gram: "+g.getId(), null);
        	break;
		}
            
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf = buf.append(terminals.size()+".");
		buf = buf.append(pairs.size()+".");
		if (triples != null)
			buf = buf.append(triples.size()+".");
		if (quads != null)
			buf = buf.append(quads.size()+".");
		if (fivers != null)
			buf = buf.append(fivers.size()+".");
		if (sixers != null)
			buf = buf.append(sixers.size()+".");
		if (seveners != null)
			buf = buf.append(seveners.size()+".");
		if (eighters != null)
			buf = buf.append(eighters.size()+".");

		return buf.toString();
	}

}

/**
 * 
 */
package org.topicquests.os.asr.reader.wordnet;

import java.util.Iterator;
import java.util.List;

import org.topicquests.hyperbrane.api.IDictionary;
import org.topicquests.hyperbrane.api.ILexTypes;
import org.topicquests.hyperbrane.api.IWordGram;
import org.topicquests.os.asr.ASRCoreEnvironment;
import org.topicquests.os.asr.api.IASRCoreModel;
import org.topicquests.os.asr.reader.wordnet.api.IWordnetModel;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;

/**
 * @author jackpark
 *
 */
public class WordnetModel implements IWordnetModel {
	private ASRCoreEnvironment environment;
	private WordNetUtility wordnetUtil;
	private IASRCoreModel model;
	private IDictionary dictionary;

	/**
	 * used by ASRModel
	 */
	public WordnetModel(ASRCoreEnvironment env) {
		environment = env;
		wordnetUtil = environment.getWordnetUtil();
		dictionary = environment.getDictionary();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.reader.wordnet.api.IWordnetModel#addHypernym(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult addHypernym(String coreWord, String hypernymWord) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.reader.wordnet.api.IWordnetModel#addHyponym(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult addHyponym(String coreWord, String hyponymWord) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.reader.wordnet.api.IWordnetModel#addSynonym(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult addSynonym(String coreWord, String synonymWord) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public IResult processTerminal(IWordGram terminal) {
		environment.logDebug("WordnetModel.processTerminal "+terminal.getWords());
		IResult result = new ResultPojo();
		String word = terminal.getWords();
		try {
			POS[] p = wordnetUtil.getPOS(word);
			IndexWord iw;
			if (p != null) {
				for (POS x: p) {
					addPOS(x.getLabel(), terminal);
					iw = wordnetUtil.getWord(x, word);
					if (iw != null) {
						List<Synset> lss = wordnetUtil.listRelated(iw, PointerType.HYPERNYM);
						dealWithHypernyms(lss, terminal);
						lss = wordnetUtil.listRelated(iw, PointerType.HYPONYM);
						dealWithHyponyms(lss, terminal);
						lss = wordnetUtil.listRelated(iw, null);
						dealWithSynonyms(lss, terminal);
					}
					//model.updateWordGram(terminal);
				}
			}
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}
//SYNONYM 11. 
//[[Synset: [Offset: 122675] [POS: adjective] Words: antecedent -- (preceding in time or order)]]

	void dealWithSynonyms(List<Synset> syns, IWordGram g) {
		environment.logDebug("WordnetModel.dealWithSynonyms "+syns);
		//WordnetModel.dealWithSynonyms [[Synset: [Offset: 1403340] [POS: adjective] Words: windward -- (on the side exposed to the wind; "the windward islands")]]
		if (syns == null && !syns.isEmpty()) return;
		if (g.hasSynonyms()) return;
		Synset s;
		Word [] words;
		String word, pos;
		Iterator<Synset> itr = syns.iterator();
		while (itr.hasNext()) {
			s = itr.next();
			words = s.getWords();
			String lemmaId;
			if (words != null) {
				for (Word w: words) {
					word = w.getLemma();
					pos = w.getPOS().getLabel();
					pos = posToLexType(pos);
					lemmaId = handleLemma(word, pos);
					g.addSynonym(lemmaId);
				}
			}
		}
		environment.logDebug("SYNONYM "+g.getId()+" "+syns);
	}
	// *HYPONYM [[Synset: [Offset: 1644883] [POS: verb] Words: get_over -- (to bring (a necessary but unpleasant task) to an end; "Let's get this job over with"; "It's a question of getting over an unpleasant task")], 
	//[Synset: [Offset: 1645087] [POS: verb] Words: run -- (carry out; "run an errand")], [Synset: [Offset: 1645174] [POS: verb] Words: consummate -- (make perfect; bring to perfection)], [Synset: [Offset: 1645293] [POS: verb] Words: consummate -- (fulfill sexually; "consummate a marriage")], [Synset: [Offset: 2567910] [POS: verb] Words: do, perform -- (get (something) done; "I did my job")], [Synset: [Offset: 2569639] [POS: verb] Words: dispatch, discharge, complete -- (complete or carry out; "discharge one's duties")]]

	//Typical synset words line_storm, equinoctial_storm
	void dealWithHyponyms(List<Synset> syns, IWordGram g) {
		environment.logDebug("WordnetModel.dealWithHyponyms "+syns);
		//WordnetModel.dealWithHyponyms [[Synset: [Offset: 11522282] [POS: noun] Words: line_storm, equinoctial_storm -- (a violent rainstorm near the time of an equinox)], [Synset: [Offset: 11522406] [POS: noun] Words: thundershower -- (a short rainstorm accompanied by thunder and lightning)]]
		if (syns == null && !syns.isEmpty()) return;
		if (g.hasHyponyms()) return;
		Synset s;
		Word [] words;
		String word, pos;
		Iterator<Synset> itr = syns.iterator();
		while (itr.hasNext()) {
			s = itr.next();
			words = s.getWords();
			if (words != null) {
				for (Word w: words) {
					word = w.getLemma();
					pos = w.getPOS().getLabel();
					pos = posToLexType(pos);
					handleLemma(word, pos);
					g.addHyponymWord(word);
				}
			}
		}
		environment.logDebug("HYPONYM "+g.getId()+" "+syns);
	}
	//*HYPERNYM [[Synset: [Offset: 485097] [POS: verb] Words: complete, finish -- (come or bring to a finish or an end; "He finished the dishes"; "She completed the requirements for her Master's Degree"; "The fastest runner finished the race in just over 2 hours; others finished in over 4 hours")], 
	//[Synset: [Offset: 1646466] [POS: verb] Words: effect, effectuate, set_up -- (produce; "The scientists set up a shock wave")]]

	void dealWithHypernyms(List<Synset> syns, IWordGram g) {
		environment.logDebug("WordnetModel.dealWithHypernyms "+syns);

		if (syns == null && !syns.isEmpty()) return;
		if (g.hasHypernyms()) return;
		Synset s;
		Word [] words;
		String word, pos;
		Iterator<Synset> itr = syns.iterator();
		String lemmaId;
		while (itr.hasNext()) {
			s = itr.next();
			words = s.getWords();
			if (words != null) {
				for (Word w: words) {
					word = w.getLemma();
					pos = w.getPOS().getLabel();
					pos = posToLexType(pos);
					lemmaId = handleLemma(word, pos);
					g.addHypernymWord(lemmaId);
				}
			}
		}
		environment.logDebug("HYPERNYM "+g.getId()+" "+syns);
	}
	
	//////////////////////////
	//TODO
	// handleLemma appears to create an endless loop that most likely
	// crashes the return stack. getThisWordGramByWords is called
	// only if we don't have the terminalId, which provokes
	// newTerminal, which calls processTerminal, which starts the cycle
	// again. In theory, the looping should stop when all the lemmas
	// are exhausted. 
	// For now, it's crashing
	// For now, we shall bypass this. The idea being, fetch the terminals
	// later if they are ever needed
	// THE SOLUTION: locally make a Terminal
	///////////////////////////
	List<String>loopStopper  = null;
	/**
	 * Go deal with this word as a wordgram
	 * @param w
	 * @param lexType could be <code>null</code>
	 */
	String handleLemma(String w, String lexType) {
		environment.logDebug("WordnetModel.handleLemma "+w);
		//if (true)
		//	return;
		String word = w.replaceAll("_", " ").trim();
		environment.logDebug("LEMMA "+word);
		
		return "";
		/**int where = word.indexOf(' ');
		if (where == -1) {
			if (!loopStopper.contains(word)) {
			
			
			
			String id = dictionary.getWordId(word);
			environment.logDebug("LEMMA "+id+" "+word);
			IWordGram g = null;
			//do not want to mess with a wordgram if it aready exists
			if (id == null) {
				IWordGram g = newTerminal(word, lexType);
			}
			
			return (String)g.getId();
		} else {
			
		}*/
	
	}
	
	String getGramId(String word) {
		StringBuilder buf = new StringBuilder();
		int where = word.indexOf(' ');
		String id;
		if (where == -1) {
			id = dictionary.getWordId(word);
		}
		return buf.toString();
	}
	IWordGram newTerminal(String word, String lexType) {
		IWordGram result = null;
		
		return result;
	}
//noun
//adjective
//adverb
//verb
	void addPOS(String pos, IWordGram g) {
		String lt = posToLexType(pos);
		environment.logDebug("POS "+lt+" "+g.getId()+" "+pos);
		if (lt != null) 
			g.addLexType(lt);
	}
	
	/**
	 * Can return <code>null</code>
	 * @param pos
	 * @return
	 */
	String posToLexType(String pos) {
		if (pos.equalsIgnoreCase("verb"))
			return ILexTypes.VERB;
		else if (pos.equalsIgnoreCase("noun"))
			return ILexTypes.NOUN;
		else if (pos.equalsIgnoreCase("adjective"))
			return ILexTypes.ADJECTIVE;
		else if (pos.equalsIgnoreCase("adverb"))
			return ILexTypes.ADVERB;
		return null;
	}
	
	@Override
	public void setModel(IASRCoreModel asrModel) {
		model = asrModel;
	}

}

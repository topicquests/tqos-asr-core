/*
 * Copyright 2014, 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.os.asr.reader.sent;

import java.util.*;

import org.topicquests.hyperbrane.api.ILexTypes;
import org.topicquests.hyperbrane.api.IWordGram;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 * 
 */
public class Patternizer {
	private SentenceBlackboard blackboard;
	/**
	 * 
	 */
	public Patternizer(SentenceBlackboard bb) {
		blackboard = bb;
	}

	/**
	 * <p>This is a <em>pattern detection</em> approach where we reduce a sentence
	 * to patterns.</p>
	 * <p>NVN is the simplest possible sentence that makes a single triple</p>
	 * <p>We pay attention <em>only</em> to N and V; we rely on prior processing
	 * to make proper determinations of what is an N and what is a V; for triples,
	 * we ignore everything else in the sentence.</p>
	 * <p>NVNVN is a nested triple, in which we assume NV is the leading triple,
	 * and the following NVN nests, e.g. {N, V, {N, V, N}}</p>
	 * <p>These are really trivial sentences.</p>
	 * <p>This approach is going to get rather <em>hairy</em> when we have complex
	 * sentences with conjunctions and so forth.</p>
	 * <p>NOTE: this will return a "VI" if the predicate has an inverse</p>
	 * @param sentence
	 * @return
	 */
	public String toTriplePattern(List<IWordGram>sentence) {
		StringBuilder buf = new StringBuilder();
		IWordGram w;
		int len = sentence.size();
		for (int i=0;i<len;i++) {
			w = sentence.get(i);
			if (w.isVerb()) {
				if (w.getIsInversePredicate()) {
					buf.append("VI");
				} else
					buf.append("V");
			}
			else if (w.isNoun() && !w.isMeta())
				buf.append("N");
			else if (w.isConjunction())
				buf.append("C");
			//TODO WHY NOT isTopic?
		}
		return buf.toString();
	}
	
/*	
	 A full pattern ought to give hints about wordgram size.
	   Instead of DNV..
	   Perhaps (1D)(2N)(2V)(3?)...
	
	 That's because we need to look at all options
	 All options include this:
	 A sentence is, in fact, a collection of WordGrams of all sizes
	 which means, that, for each position in the Terminals
	 	which, in fact, reflects the sentence
	 we might have competing higher order wordgrams
	 e.g.
	 	A b c d e f  terminals
	 	(A, ab, abc, abcd, abcde, abcdef)
	 	(b, bcd, bcde, bcdef)
	 	(c, cde, cdef)
	 	(d, de, def)
	 	(e, ef)
	 	(f)
	 AND: each one of those cells must be studied, weeded, etc.
	 A growing WorkingSentence is what we make of those cells.
*/	
	/**
	 * This produces a full pattern for a given sentence
	 * @param sentence
	 * @return
	 */
	public List<List<String>> toFullPattern(Sentence_Structure struc) {
		List<List<String>> result = new ArrayList<List<String>>();
		List<List<IWordGram>> l = draftWorkingSentence(struc);
		int len = l.size();
		for (int i=0;i<len;i++)
			result.add(analyzeList(l.get(i)));
		System.out.println("FULL "+result);
		return result;
	}
	
	/**
	 * <p>Turn <code>wgs</code> into a list of SentenceCodes, one for each
	 * WordGram in the list.
	 * </p>
	 * <p>If a gram has the inversePredicateFlag, the code will
	 * include "I"</p>
	 * @param wgs
	 * @return
	 */
	List<String> analyzeList(List<IWordGram> wgs) {
		List<String> result = new ArrayList<String>();
		int len2, len = wgs.size();
		IWordGram g;
		List<String>lt;
		StringBuilder buf = new StringBuilder();
		boolean hasInverse = false;
		for (int i=0;i<len;i++) {
			buf.setLength(0);
			g = wgs.get(i);
			hasInverse = g.getIsInversePredicate();
			buf = buf.append(Integer.toString(g.getGramSize()));
			//First, see if this has an inverse predicate
			if (hasInverse) {
				buf.append("I");
			}
			//see if this WordGram has topic locators
			//and process from there
			if (g.listTopicLocators() != null)
				buf.append("T");
			else if (g.hasDBPedia())
				buf.append("DBP");
			else {
				lt = g.listLexTypes();
				if (lt != null) {
					if (lt.contains(ILexTypes.META_TYPE))
						buf.append(ILexTypes.META_TYPE);
					else if (lt.contains(ILexTypes.ADVERBIAL_PHRASE))
						buf.append(ILexTypes.ADVERBIAL_PHRASE);
					else {
						len2 = lt.size();
						for (int j=0;j<len2;j++)
							buf.append(lt.get(j));
					}
				} else
					buf.append("?");
			}
			buf.append(":"+g.getId());
			result.add(buf.toString());
		}
		
		return result;
	}
	
	/**
	 * <p>An example Sentence Pattern: (1?2?3?4?)(1T2?3?)(1?2T)(1nv)</p>
	 * <p>We can take that pattern apart.
	 * @param struc
	 * @return
	 */
	List<List<IWordGram>> draftWorkingSentence(Sentence_Structure struc) {
		List<List<IWordGram>> result = new ArrayList<List<IWordGram>>();
		int finalLength = struc.terminals.size();
		int pointer = 0;
		IWordGram g;
		for (int i=0;i<finalLength;i++) {
			//FOR each word position in sentence
			result.add(makeList(i, struc));
		}
		return result;
	}
	
	void paintList(List<IWordGram>l) {
		Iterator<IWordGram>itr = l.iterator();
		while (itr.hasNext())
			System.out.println(((JSONObject)itr.next()).toJSONString());
	}
	
	/**
	 * For this word position <code>pointer</code> in the sentence
	 * compose a list of WordGrams: the terminal at that position,
	 * the pair at that position, the triple if available...
	 * @param pointer where we are in the sentence
	 * @param struc
	 * @return
	 */
	List<IWordGram> makeList(int pointer, Sentence_Structure struc) {
		List<IWordGram> lw = new ArrayList<IWordGram>();
		int limit = pointer;
		lw.add(struc.terminals.get(pointer));
		///////////////////////
		// On higher-order WordGrams, if a Redirect returns a shorter
		// WordGram, then it's already been included, so ignore the
		// shorter gram
		///////////////////////
		if (struc.pairs != null && limit < struc.pairs.size() &&
			struc.pairs.get(pointer).getGramSize() == 2)
				lw.add(struc.pairs.get(pointer));
		if (struc.triples != null && limit < struc.triples.size() &&
			struc.triples.get(pointer).getGramSize() == 3)
			lw.add(struc.triples.get(pointer));
		if (struc.quads != null && limit < struc.quads.size() &&
			struc.quads.get(pointer).getGramSize() == 4)
			lw.add(struc.quads.get(pointer));
		if (struc.fivers != null && limit < struc.fivers.size() &&
			struc.fivers.get(pointer).getGramSize() == 5)
			lw.add(struc.fivers.get(pointer));
		if (struc.sixers != null && limit < struc.sixers.size() &&
			struc.sixers.get(pointer).getGramSize() == 6)
			lw.add(struc.sixers.get(pointer));
		if (struc.seveners != null && limit < struc.seveners.size() &&
			struc.seveners.get(pointer).getGramSize() == 7)
			lw.add(struc.seveners.get(pointer));
		if (struc.eighters != null && limit < struc.eighters.size() &&
			struc.eighters.get(pointer).getGramSize() == 8)
			lw.add(struc.eighters.get(pointer));
		return lw;
	}
}

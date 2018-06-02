/*
 * Copyright 2014, 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.os.asr.reader.sent;

import java.util.*;

import org.topicquests.hyperbrane.api.IWordGram;

/**
 * @author jackpark
 *
 */
public class SentenceStructure {

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
	public SentenceStructure() {
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

/*
 * Copyright 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.os.asr.events;

import java.util.*;
import org.topicquests.hyperbrane.api.IWordGram;
import org.topicquests.os.asr.api.IWordGramChangeEventRegistry;

/**
 * @author jackpark
 *
 */
public class WordGramChangeEventRegistry implements IWordGramChangeEventRegistry {
	private List<IWordGram> gramList;
	private List<String> sentenceIdList;
	/**
	 * 
	 */
	public WordGramChangeEventRegistry() {
		gramList = new ArrayList<IWordGram>();
		sentenceIdList = new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.api.IWordGramChangeEventRegistry#registerWordGram(org.topicquests.hyperbrane.api.IWordGram)
	 */
	@Override
	public void registerWordGram(IWordGram g) {
		synchronized(gramList) {
			gramList.add(g);
		}
	}

	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.api.IWordGramChangeEventRegistry#size()
	 */
	@Override
	public int registrySize() {
		synchronized(gramList) {
			return gramList.size();
		}
	}

	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.api.IWordGramChangeEventRegistry#takeNext()
	 */
	@Override
	public IWordGram takeNextGram() {
		synchronized(gramList) {
			if (!gramList.isEmpty())
				return gramList.remove(0);
			return null;
		}
	}

	@Override
	public void registerSentenceId(String id) {
		synchronized(sentenceIdList) {
			sentenceIdList.add(id);
		}
	}

	@Override
	public int sentenceSize() {
		synchronized(sentenceIdList) {
			return sentenceIdList.size();
		}
	}

	@Override
	public String takeNextSentenceId() {
		synchronized(sentenceIdList) {
			return sentenceIdList.get(0);
		}
	}

	@Override
	public void removeSenenceId(String id) {
		synchronized(sentenceIdList) {
			sentenceIdList.remove(id);
		}
	}

	@Override
	public List<String> listUnhandledSentenceIds(IWordGram g) {
		synchronized(sentenceIdList) {
			List<String> result = new ArrayList<String>();
			List<String> sids = g.listSentenceIds();
			if (sids != null) {
				Iterator<String> itr = sids.iterator();
				String s;
				while (itr.hasNext()) {
					s = itr.next();
					if (sentenceIdList.contains(s))
						result.add(s);
				}
			}
			return result;
		}
	}

	@Override
	public Set<String> listAllIntersectingSentenceIds() {
		Set<String>result = new HashSet<String>();
		synchronized(sentenceIdList) {
			synchronized(gramList) {
				Iterator<String>str;
				Iterator<IWordGram> itr = gramList.iterator();
				IWordGram g;
				List<String>sids;
				String id;
				while (itr.hasNext()) {
					g = itr.next();
					sids = g.listSentenceIds();
					if (sids != null) {
						str = sids.iterator();
						while (str.hasNext()) {
							id = str.next();
							if (sentenceIdList.contains(id))
								result.add(id);
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public void pruneGrams() {
		// TODO Auto-generated method stub
		
	}


}

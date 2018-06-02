/*
 * Copyright 2014, 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.hyperbrane.api;

import org.topicquests.support.api.IResult;

/**
 * @author park
 *
 */
public interface INormalizeAgent {
	
	/**
	 * <p>This agent registers itself with an {@link IWordGram}.
	 * Said wordgram lives inside the sentence being normalized.
	 * An example is an agent that recognizes "is caused by" and
	 * causes the polarity of subject and object of the sentence
	 * to be reversed, normalizing that predicate phrase to the wordgram
	 * for "cause", and outputing a new sequence of {@link IWordGram}s 
	 * for that sentence. Those would be carried in a <em>normalized</em>
	 * list</p>
	 * @param doc
	 * @param sentence
	 * @return
	 */
	IResult acceptSentence(IDocument doc, ISentence sentence);
	

}

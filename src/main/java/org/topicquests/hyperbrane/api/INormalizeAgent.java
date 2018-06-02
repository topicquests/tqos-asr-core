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

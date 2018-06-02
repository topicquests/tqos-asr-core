/*
 * Copyright 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.os.asr.api;

import org.topicquests.hyperbrane.api.IWordGram;
import org.topicquests.os.asr.reader.sent.SentenceBlackboard;
/**
 * @author jackpark
 *
 */
public interface IDbPediaModel {

	/**
	 * By the time we get here, a sentence has been turned into
	 * its collection of {@link IWordGram} objects. We can now
	 * feed the sentence to DbPedia Spotlight and update all
	 * appropriate wordgrams.
	 * @param bb
	 */
	void updateSentenceWithDbPedia(SentenceBlackboard bb);
}

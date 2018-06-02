/**
 * 
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

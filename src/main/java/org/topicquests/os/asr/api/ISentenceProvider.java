/**
 * 
 */
package org.topicquests.os.asr.api;

import org.topicquests.hyperbrane.api.ISentence;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public interface ISentenceProvider {

	IResult getSentence(String locator);
	
	IResult putSentence(ISentence sentence);
	
	IResult updateSentence(ISentence sentence);
	
}

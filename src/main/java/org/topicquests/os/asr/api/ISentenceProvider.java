/*
 * Copyright 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
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

/*
 * Copyright 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.os.asr.dbpedia;

import org.topicquests.os.asr.ASRCoreEnvironment;

/**
 * @author jackpark
 *
 */
public class DbPediaEventHandler {
	private ASRCoreEnvironment environment;

	/**
	 * 
	 */
	public DbPediaEventHandler(ASRCoreEnvironment env) {
		environment = env;
	}

	/**
	 * <p>A dbPedia request stems from a dbPediaSpotlight hit in which
	 * a <code>dbPediaURL</code> is returned in the context of a particular
	 * <code>documentId</code></p>
	 * <p>The goal is to fetch and harvest that <code>dbPediaURL</code> and
	 * harvest it, in the event it has not already been fetched.</p>
	 * @param documentId
	 * @param dbPediaURL
	 */
	public void newDbPediaRequest(String documentId, String dbPediaURL) {
		//TODO
		System.out.println("DbPediaEventHandler "+documentId+" "+dbPediaURL);
	}
}

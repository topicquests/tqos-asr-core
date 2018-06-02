/**
 * 
 */
package org.topicquests.os.asr.dbpedia;

import org.topicquests.os.asr.ASRCoreEnvironment;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class SpotlightClient {
	private ASRCoreEnvironment environment;
	private DBpediaSpotlightClient client;

	/**
	 * 
	 */
	public SpotlightClient(ASRCoreEnvironment env) {
		environment = env;
		client = new DBpediaSpotlightClient(environment);
	}
	
	public IResult annotateSentence(String sentence) {
		IResult result = new ResultPojo();
		try {
			JSONObject jo = client.extract(sentence);
			result.setResultObject(jo);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			//result.addErrorString(e.getMessage());
			//e.printStackTrace();
		}
		return result;
	}

}

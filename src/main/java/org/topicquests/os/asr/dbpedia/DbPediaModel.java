/**
 * 
 */
package org.topicquests.os.asr.dbpedia;

import java.util.*;
import org.topicquests.support.api.IResult;
import org.topicquests.hyperbrane.api.IWordGram;
import org.topicquests.os.asr.ASRCoreEnvironment;
import org.topicquests.os.asr.api.IASRCoreModel;
import org.topicquests.os.asr.api.IDbPediaModel;
import org.topicquests.os.asr.reader.sent.SentenceBlackboard;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class DbPediaModel implements IDbPediaModel {
	private ASRCoreEnvironment environment;
	private IASRCoreModel model;
	private SpotlightClient spotliteClient;

	/**
	 * 
	 */
	public DbPediaModel(ASRCoreEnvironment env) {
		environment = env;
		model = environment.getCoreModel();
		spotliteClient = environment.getSpotlightClient();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.os.asr.api.IDbPediaModel#updateSentenceWithDbPedia(org.topicquests.os.asr.reader.SentenceBlackboard)
	 */
	@Override
	public void updateSentenceWithDbPedia(SentenceBlackboard bb) {
		IResult r = spotliteClient.annotateSentence(bb.getTheSentence().getSentence());
		environment.logDebug("CheckingDbPedia "+bb.getSentenceId()+" "+r.getResultObject());
		JSONObject jo = (JSONObject)r.getResultObject();
		SpotlightUtil util = new SpotlightUtil(null);
		if (jo != null) {
			bb.setDbPediaJSON(jo);
			List<JSONObject> ljo = bb.listDbPediaJSON();
			String term, uri;
			IWordGram wg;
			if (ljo != null) {
				Iterator<JSONObject>itr = ljo.iterator();
				while (itr.hasNext()) {
					jo = itr.next();
					term = util.getTerm(jo);
					uri = util.getURI(jo);
					wg = model.getThisWordGramByWords(term);
					if (wg != null) {
						wg.addDbPediaJSON(jo);
					} else {
						environment.logError("DbPediaModel failed to find "+term, null);
					}
					bb.getDocumentBlackboard().getTheDocument().addDbPediaURI(uri);
				}
			}
			
		}

	}

}

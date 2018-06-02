/**
 * 
 */
package org.topicquests.os.asr.api;

import java.util.Iterator;

import org.topicquests.hyperbrane.api.IDocument;
import org.topicquests.ks.api.ITicket;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public interface IDocumentProvider {

	Iterator<JSONObject> iterateDocuments();
	
	IResult getDocument(String locator, ITicket credentials);
	
	IResult putDocument(IDocument node);
	
	IResult updateDocument(IDocument node);

}

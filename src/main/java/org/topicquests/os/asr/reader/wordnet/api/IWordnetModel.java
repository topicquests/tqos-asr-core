/**
 * 
 */
package org.topicquests.os.asr.reader.wordnet.api;

import org.topicquests.hyperbrane.api.IWordGram;
import org.topicquests.os.asr.api.IASRCoreModel;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public interface IWordnetModel {

	/**
	 * <p>A new <code>terminal</code> is passed in where it is
	 * fully analyzed by {@link WordNetUtility}</p>
	 * @param terminal
	 * @return
	 */
	IResult processTerminal(IWordGram terminal);
	
	void setModel(IASRCoreModel asrModel);
	
	/**
	 * <p>hypernyn: a word with a broad meaning that 
	 * more specific words fall under; a superordinate. 
	 * For example, color is a hypernym of red.</p>
	 * 
	 * @param coreWord
	 * @param hypernymWord
	 * @return
	 */
	IResult addHypernym(String coreWord, String hypernymWord);
	
	/**
	 * <p>hyponym: a word of more specific meaning 
	 * than a general or superordinate term applicable to it. 
	 * For example, spoon is a hyponym of cutlery.</p>
	 * 
	 * @param coreWord
	 * @param hyponymWord
	 * @return
	 */
	IResult addHyponym(String coreWord, String hyponymWord);
	
	/**
	 * <p>synonym: a word having the same or nearly the same meaning
	 *  as another in the language, as happy, joyful, elated</p>
	 *  
	 * @param coreWord
	 * @param synonymWord
	 * @return
	 */
	IResult addSynonym(String coreWord, String synonymWord);
}

/*
 * Copyright 2014, 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.os.asr.reader.wordnet;

import java.io.*;
import java.util.*;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.IndexWordSet;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.relationship.Relationship;
import net.didion.jwnl.data.relationship.RelationshipFinder;
import net.didion.jwnl.data.relationship.RelationshipList;
import net.didion.jwnl.dictionary.Dictionary;

import org.topicquests.os.asr.ASRCoreEnvironment;


/**
 * @author park
 * @see http://blog.roland-kluge.de/?p=430
 *
 */
public class WordNetUtility {
	private ASRCoreEnvironment environment;
	private Dictionary wordnet;
	private final String propsFile = "config/file_properties.xml"; //TODO config property
	//NOTE: that's hardwired; it will work if one installs per the system's layout
	/**
	 * 
	 */
	public WordNetUtility(ASRCoreEnvironment env) throws Exception {
		environment = env;
		JWNL.initialize(new FileInputStream(propsFile));
		wordnet = Dictionary.getInstance(); //dict.getDictionary();
	}
		
	public Dictionary getDictionary() {
		return wordnet;
	}
	
	/**
	 * <p>Can return <code>null</code> if nothing found</p>
	 * <p>Use pos.getLabel() e.g. "noun"
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public POS[] getPOS(String s) throws Exception {
        // Look up all IndexWords (an IndexWord can only be one POS)
        IndexWordSet set = wordnet.lookupAllIndexWords(s);
        // Turn it into an array of IndexWords
        IndexWord[] words = set.getIndexWordArray();
        // Make the array of POS
        POS[] pos = new POS[words.length];
        for (int i = 0; i < words.length; i++) {
            pos[i] = words[i].getPOS();
        }
        return pos;
    }

	public Relationship getRelationship (IndexWord start, IndexWord end, PointerType type) throws Exception {
        // All the start senses
        Synset[] startSenses = start.getSenses();
        // All the end senses
        Synset[] endSenses = end.getSenses();
        // Check all against each other to find a relationship
        for (int i = 0; i < startSenses.length; i++) {
            for (int j = 0; j < endSenses.length; j++) {
                RelationshipList list = RelationshipFinder.getInstance().findRelationships(startSenses[i], endSenses[j], type);
                if (!list.isEmpty())  {
                    return (Relationship) list.get(0);
                }
            }
        }
        return null;
    }

	public List<Synset> listRelationshipSenses (Relationship rel) throws Exception {
        List<Synset> result = new ArrayList<Synset>();
        PointerTargetNodeList nodelist = rel.getNodeList();
        Iterator<PointerTargetNode> itr = nodelist.iterator();
        while (itr.hasNext()) {
            PointerTargetNode related = itr.next();
            result.add(related.getSynset());
        }
        return result;
    }
	
	/**
	 * Returns either Hypernyms or Hyponyms or Synonyms if <code>type</code>==<code>null</code>
	 * @param sense
	 * @param type can be <code>null</code>
	 * @return can return <code>null</code>
	 * @throws Exception
	 */
	public List<Synset> listRelated (Synset sense, PointerType type) throws Exception {
        PointerTargetNodeList relatedList;
        // Call a different function based on what type of relationship you are looking for
        if (type != null && type == PointerType.HYPERNYM) {
            relatedList = PointerUtils.getInstance().getDirectHypernyms(sense);
        } else if (type != null && type == PointerType.HYPONYM){
            relatedList = PointerUtils.getInstance().getDirectHyponyms(sense);
        } else {
            relatedList = PointerUtils.getInstance().getSynonyms(sense);
        }
        if (relatedList == null)
        	return null;
        // Iterate through the related list and make an ArrayList of Synsets to send back
        Iterator<PointerTargetNode> itr = relatedList.iterator();
        List<Synset>result = new ArrayList<Synset>();
        while (itr.hasNext()) {
            PointerTargetNode related = itr.next();
            Synset s = related.getSynset();
            result.add(s);
        }
        return result;
    }
	
	/**
	 * Returns either Hypernyms or Hyponyms or Synonyms if <code>type</code>==<code>null</code>
	 * @param word
	 * @param type can be <code>null</code>
	 * @return can return <code>null</code>
	 * @throws Exception
	 */
	public List<Synset> listRelated(IndexWord word, PointerType type) throws Exception {
        if (word.getSense(1) == null)
        	return null;
		PointerTargetNodeList relatedList;
        environment.logDebug("WordNetUtility.listRelated "+word.getSense(1)+" | "+word.toString());
        // Call a different function based on what type of relationship you are looking for
        if (type != null && type == PointerType.HYPERNYM) {
            relatedList = PointerUtils.getInstance().getDirectHypernyms(word.getSense(1));
        } else if (type != null && type == PointerType.HYPONYM){
            relatedList = PointerUtils.getInstance().getDirectHyponyms(word.getSense(1));
        } else {//TODO this is related only to adjectives
            relatedList = PointerUtils.getInstance().getSynonyms(word.getSense(1));
        }
        if (relatedList == null)
        	return null;
        // Iterate through the related list and make an ArrayList of Synsets to send back
        Iterator<PointerTargetNode> itr = relatedList.iterator();
        List<Synset>result = new ArrayList<Synset>();
        while (itr.hasNext()) {
            PointerTargetNode related = itr.next();
            Synset s = related.getSynset();
            result.add(s);
        }
        return result;	
	}
	
	/**
	 * Returns an {@link IndexWord} for the given <code>pos</code> and <code>s</code>
	 * 
	 * @param pos
	 * @param s
	 * @return can return <code>null</code>
	 * @throws Exception
	 */
	public IndexWord getWord(POS pos, String s) throws Exception {
        IndexWord word = wordnet.getIndexWord(pos,s);
        return word;
    }
	
	/**
	 * Returns a list of {@link Synset} objects
	 * @param w
	 * @return can return <code>null</code>
	 * @throws Exception
	 */
	public Synset[] getSenses(IndexWord w) throws Exception {
		return w.getSenses();
	}
	
	public Word[] getWords(Synset ss) {
		return ss.getWords();
	}
	
}

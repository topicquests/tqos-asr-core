/**
 * 
 */
package org.topicquests.os.asr;

import org.topicquests.ks.StatisticsUtility;

/**
 * @author park
 *
 */
public class StatisticsUtilityExtension {
	private final String
		ONTOLOGY_COUNT 			= "Ontology Count",
		ONTOLOGY_CLASS_COUNT	= "Ontology Class Count",
		ONTOLOGY_PROPERTY_COUNT	= "Ontology Property Count",
		TOPIC_COUNT				= "Topic Count",
		TERMINAL_COUNT			= "Terminal Count",
		PAIR_COUNT				= "Pair Count",
		TRIPLE_COUNT			= "Triple Count",
		QUAD_COUNT				= "Quad Count",
		FIVER_COUNT				= "Fiver Count",
		SIXER_COUNT				= "Sixer Count",
		SEVENER_COUNT			= "Sevener Count",
		EIGHTER_COUNT			= "Eighter Count",
		SENTENCE_COUNT			= "Sentence Count",
		SENTENCE_COMPLETE_COUNT = "Completed Sentence Count",
		DOCUMENT_COUNT			= "Document Count",
		DICTIONARY_SIZE			= "Dictionary Size",
		TOTAL_WORDS_READ		= "Total Words Read",
		TUPLE_COUNT				= "Tuple Count",
		SEM_TRIPLE_COUNT		= "Semantic Triple Count",
		FILE_NAME			 	= "TextHarvesterStatistics.json",
		CONVERSATION_COUNT		= "Conversation Count";
	private StatisticsUtility data;
	
	/**
	 * 
	 */
	public StatisticsUtilityExtension(StatisticsUtility d) {
		data = d;
	}
	
	
	public void addDictionaryWord() {
		data.addToKey(DICTIONARY_SIZE);
	}
	
	public void addWordRead() {
		data.addToKey(TOTAL_WORDS_READ);
	}
	
	public void addOntology() {
		data.addToKey(ONTOLOGY_COUNT);
	}
	
	public void addOntologyClass() {
		data.addToKey(ONTOLOGY_CLASS_COUNT);
	}
	
	public void addOntologyProperty() {
		data.addToKey(ONTOLOGY_PROPERTY_COUNT);
	}
	
	public void addTopic() {
		data.addToKey(TOPIC_COUNT);
	}
	
	public void addTerminal() {
		data.addToKey(TERMINAL_COUNT);
	}
	
	public void addPair() {
		data.addToKey(PAIR_COUNT);
	}

	public void addTriple() {
		data.addToKey(TRIPLE_COUNT);
	}
	
	public void addQuad() {
		data.addToKey(QUAD_COUNT);
	}
	
	public void addFiver() {
		data.addToKey(FIVER_COUNT);
	}
	
	public void addSixer() {
		data.addToKey(SIXER_COUNT);
	}
	
	public void addSevener() {
		data.addToKey(SEVENER_COUNT);
	}
	
	public void addEighter() {
		data.addToKey(EIGHTER_COUNT);
	}
	
	public void addSentence() {
		data.addToKey(SENTENCE_COUNT);
	}

	public void addCompletedSentence() {
		data.addToKey(SENTENCE_COMPLETE_COUNT);
	}

	public void addDocument() {
		data.addToKey(DOCUMENT_COUNT);
	}
	
	public void addConversation() {
		data.addToKey(CONVERSATION_COUNT);
	}
	public void addTuple() {
		data.addToKey(TUPLE_COUNT);
	}

	public void addSemanticTriple() {
		data.addToKey(SEM_TRIPLE_COUNT);
	}
	
	public String getStats() {
		System.out.println("GETTING STATS");
		return data.getStats();
	}
	
	public void saveStats() throws Exception {
		data.saveData();
	}
	
}

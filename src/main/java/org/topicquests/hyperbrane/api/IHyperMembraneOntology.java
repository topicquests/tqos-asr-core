/**
 * 
 */
package org.topicquests.hyperbrane.api;

/**
 * @author park
 *
 */
public interface IHyperMembraneOntology {
	public static final String
		//types
		DOCUMENT_TYPE				= "HyperDocumentType",
		SENTENCE_TYPE				= "HyperSentenceType",
//		WORD_GRAM_TYPE				= "HyperWordgramWordGramType",
		TUPLE_TYPE					= "HyperWordgramTupleType",
		//Lens Codes
		ENTITY_LENS					= "EntityLens",
		CAUSE_LENS					= "CauseLens",  // core.tm
		CHANGE_LENS					= "ChangeLens", // core.tm
		TAXON_LENS					= "TaxonomicLens", // core.tm  (not so sure about this)
		BIO_LENS					= "BioMedLens", //biomed.tm
		ANIMAL_LENS					= "AnimalLens", // ???
		INC_DEC_LENS				= "IncreaseDecreaseLens", //??
		NEGATION_LENS				= "NegationLens", //??
		BIND_CODE					= "BindCode", //??
		//Properties
		PARAGRAPH_PROPERTY_TYPE		= "ParagraphPropertyType";
	

}

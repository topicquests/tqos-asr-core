/*
 * Copyright 2014, TopicQuests
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.topicquests.hyperbrane.api;

/**
 * @author park
 *
 */
public interface ILexTypes {
	public static final String
		NOUN				= "n",
		INFERRED_NOUN		= "in",
		NOUN_PHRASE			= "np",
		INFERRED_NOUNPHRASE	= "inp",
		PROPER_NOUN			= "npn",
		GERUND				= "ng",
		DETERMINER			= "det",
		VERB				= "v",
		INFERRED_VERB		= "iv",
		VERB_PHRASE			= "vp",
		INFERRED_VERBPRASE	= "ivp",
		TUPLE_TYPE			= "tup",
		ADJECTIVE			= "adj", //note: it's an "a" for wordnet/framenet
		ADVERB				= "adv",
		ADVERBIAL_PHRASE	= "advp",
		PREPOSITION			= "prep",
		PRONOUN				= "pro",
		CONJUNCTION			= "cnj",
		C_CONJUNCTION		= "ccnj",
		CONJUNCTIVE_ADVERB 	= "cadvp",
		R_CONJUNCTION		= "corj",
		QUESTION_WORD		= "qw",
		STOP_WORD			= "sw",
		NUMBER				= "num",
		PERCENT_NUMBER		= "pnum",
		DATE				= "date",
		EMAIL				= "email",
		IP_ADDRESS			= "ipA",
		TIME				= "time",
		HREF				= "href",
		GEO_LOC				= "geoL",
		META_TYPE			= "MTA";


}

/**
 * 
 */
package org.topicquests.os.asr;

import java.util.*;
import java.io.*;

import org.topicquests.ks.StatisticsUtility;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.kafka.KafkaConsumer;
import org.topicquests.ks.kafka.KafkaProducer;
import org.topicquests.asr.general.GeneralDatabaseEnvironment;
import org.topicquests.asr.general.document.api.IDocumentClient;
import org.topicquests.asr.sentence.api.ISentenceClient;
import org.topicquests.backside.kafka.consumer.api.IMessageConsumerListener;
import org.topicquests.blueprints.pg.BlueprintsPgEnvironment;
import org.topicquests.hyperbrane.ConcordanceDictionary;
import org.topicquests.hyperbrane.WordGramCache;
import org.topicquests.hyperbrane.api.IDictionary;
import org.topicquests.os.asr.api.IASRCoreModel;
import org.topicquests.os.asr.api.IDbPediaModel;
import org.topicquests.os.asr.api.IDocumentProvider;
import org.topicquests.os.asr.api.ISentenceProvider;
import org.topicquests.os.asr.api.IWordGramChangeEventRegistry;
//import org.topicquests.os.asr.dbpedia.DbPediaEventHandler;
import org.topicquests.os.asr.dbpedia.DbPediaModel;
import org.topicquests.os.asr.dbpedia.SpotlightClient;
import org.topicquests.os.asr.events.WordGramChangeEventRegistry;
import org.topicquests.os.asr.reader.wordnet.WordNetUtility;
import org.topicquests.os.asr.reader.wordnet.WordnetModel;
import org.topicquests.os.asr.reader.wordnet.api.IWordnetModel;
import org.topicquests.support.RootEnvironment;

import com.tinkerpop.blueprints.impls.sql.SqlGraph;

/**
 * @author jackpark
 *
 */
public class ASRCoreEnvironment extends RootEnvironment {
	private static ASRCoreEnvironment instance;
	private StatisticsUtilityExtension stats;
	private IDictionary dictionary;
	private IASRCoreModel model;
	private IWordGramChangeEventRegistry changeRegistry;
	private BlueprintsPgEnvironment blueprints;
	private Set<String> multiTopicGrams;
	private SqlGraph theGraph;
	private IWordnetModel wordnetModel;
	private WordNetUtility wordnetUtil;
	private ISentenceProvider sentenceProvider;
	private IDocumentProvider documentProvider;
	private IDocumentClient documentDatabase;
	private GeneralDatabaseEnvironment generalEnvironment;
	private SpotlightClient spotliteClient;
	private IDbPediaModel dbPediaModel;
//	private DbPediaEventHandler dbPediaEventHandler;
	private ISentenceClient sentenceDatabase;
//	private KafkaProducer kafkaProducer;
//	private KafkaConsumer kafkaConsumer;
	//We must have a live TopicMap because some ASR objects extend Proxy
	private SystemEnvironment topicMapEnvironment;
	protected WordGramCache cache;


	/**
	 * 
	 */
	public ASRCoreEnvironment() {
		super("asr-props.xml", "logger.properties");
		topicMapEnvironment = new SystemEnvironment();
		multiTopicGrams = new HashSet<String>();
		blueprints = new BlueprintsPgEnvironment();
		changeRegistry = new WordGramChangeEventRegistry();
		//TODO this needs fixing
		//IPostgreSqlProvider provider = new PostgreSqlProvider(getGraphName(), "AsrSchema");
		spotliteClient = new SpotlightClient(this);
		dbPediaModel  = new DbPediaModel(this);
//		dbPediaEventHandler = new DbPediaEventHandler(this);

		String schemaName = getStringProperty("DatabaseSchema");
		 
		generalEnvironment = new GeneralDatabaseEnvironment(schemaName);
		sentenceDatabase = generalEnvironment.getSentenceClient();
		documentDatabase = generalEnvironment.getDocumentClient();
		documentProvider = new DocumentProvider(this);
		sentenceProvider = new SentenceProvider(this);
		String graphName = getStringProperty("GraphName");
		theGraph = blueprints.getGraph(graphName);
		logDebug("ASRCoreEnvironment+ "+theGraph);
		logDebug("ASRCoreEnvironment++ "+documentDatabase+" "+documentProvider);
		logDebug("ASRCoreEnvironment+++ "+getDocProvider());
		logDebug("ASRCoreEnvironment++++ "+getSentenceProvider());
		logDebug("ASRCoreEnvironment-- "+getFoo());
		try {
			stats = new StatisticsUtilityExtension(StatisticsUtility.getInstance());
			cache = new WordGramCache(this, 8192);
			dictionary = new ConcordanceDictionary(this);
			wordnetUtil = new WordNetUtility(this);
			model = new ASRCoreModel(this);
			wordnetModel = new WordnetModel(this);
			model.setWordnetModel(wordnetModel);
			wordnetModel.setModel(model);
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.shutDown();
			throw new RuntimeException(e);			
		}
		instance = this;
	}
	
	Object getFoo() {
		logDebug("ASRCoreEnvironment.getFoo");
		logDebug("ASRCoreEnvironment.getFoo+ "+documentProvider);
		logDebug("ASRCoreEnvironment.getFoo++ "+getDocProvider());
		return getDocProvider();
	}
	
	public IDocumentProvider getDocProvider() {
		return documentProvider;
	}
	/**
	 * We need this for objects like IParagraph, etc, to
	 * gain access to the dataproviders etc.
	 * @return
	 */
	public static ASRCoreEnvironment getInstance() {
		return instance;
	}
	
	public SystemEnvironment getTopicMapEnvironment() {
		return topicMapEnvironment;
	}
	
	//public IDocumentProvider getDocumentProvider() {
	//	return documentProvider;
	//}

	/**
	 * Agents which extend ASRCore can have their own KafkaProducer
	 * @param clientId
	 * @return
	 */
	public KafkaProducer getKafkaProducer(String clientId) {
		return new KafkaProducer(this, clientId);
	}
	
	/**
	 * Agents can have their own KafkaConsumer instances
	 * @param topic
	 * @param listener
	 * @return
	 */
	public KafkaConsumer getKafkaConsumer(String topic, IMessageConsumerListener listener) {
		return new KafkaConsumer(this, topic, listener);
	}
	
	public IDocumentClient getDocumentDatabase () {
		return documentDatabase;
	}
	public ISentenceClient getSentenceDatabase() {
		return sentenceDatabase;
	}

	public ISentenceProvider getSentenceProvider() {
		return sentenceProvider;
	}
	
	public String getGraphName() {
		return getStringProperty("GraphName");
	}
	
	/**
	 * The WordGram graph
	 * @return
	 */
	public SqlGraph getTheGraph() {
		return theGraph;
	}
	
	public WordNetUtility getWordnetUtil() {
		return wordnetUtil;
	}
	
	public IWordGramChangeEventRegistry getWordGramChangeEventRegistry() {
		return changeRegistry;
	}

	public SpotlightClient getSpotlightClient() {
		return spotliteClient;
	}
	
	public IDbPediaModel getDbPediaModel() {
		return dbPediaModel;
	}


	/**
	 * When a WordGram has more than one topicLocator, we
	 * add its <code>id</code> to this set.
	 * @param id
	 */
	public void addMultiGramID(String id) {
		this.multiTopicGrams.add(id);
	}
		
	//public RethinkClient getRethinkClient() {
	//	return rethink.getClient();
	//}
	
	public BlueprintsPgEnvironment getGraphEnvironment() {
		return blueprints;
	}	
	
	public WordGramCache getWordGramCache() {
		return cache;
	}
		
	public IASRCoreModel getCoreModel() {
		return model;
	}
	
	public IWordnetModel getWordnetModel() {
		return wordnetModel;
	}

	public IDictionary getDictionary() {
		return dictionary;
	}

	public StatisticsUtilityExtension getStats() {
		return stats;
	}


	public void shutDown() {
		System.out.println("ShuttingDown");
		try {
			stats.saveStats();
		} catch (Exception x) {
			logError(x.getMessage(), x);
			x.printStackTrace();
		}
		cache.flushAll();
		
		saveMultiTopicGrams();
		try {
			if (dictionary != null)
				dictionary.saveDictionary();
		} catch (Exception e) {
			logError(e.getMessage(), e);
			e.printStackTrace();
		}
		//blueprints shuts down theGraph
		blueprints.shutDown();
		
	}
	
	void saveMultiTopicGrams() {
		try {
			File f = new File("MultiGrams.txt");
			FileOutputStream fos = new FileOutputStream(f);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			PrintWriter out = new PrintWriter(bw);
			Iterator<String>itr = this.multiTopicGrams.iterator();
			while (itr.hasNext())
				out.println(itr.next());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

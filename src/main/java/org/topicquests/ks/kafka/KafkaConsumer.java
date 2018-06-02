/**
 * 
 */
package org.topicquests.ks.kafka;

import org.topicquests.backside.kafka.consumer.StringMessageConsumer;
import org.topicquests.backside.kafka.consumer.api.IMessageConsumerListener;
import org.topicquests.support.api.IEnvironment;

/**
 * @author jackpark
 *
 */
public class KafkaConsumer extends StringMessageConsumer {

	/**
	 * @param e
	 * @param topic
	 * @param listener
	 */
	public KafkaConsumer(IEnvironment e, String topic, IMessageConsumerListener listener) {
		super(e, (String)null, topic, listener, false);
		//All the work isin the listener
	}

}

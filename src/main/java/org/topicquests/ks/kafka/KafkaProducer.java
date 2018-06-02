/**
 * 
 */
package org.topicquests.ks.kafka;


import org.topicquests.backside.kafka.producer.MessageProducer;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.support.api.IEnvironment;

/**
 * @author park
 *
 */
public class KafkaProducer extends MessageProducer {

  /**
   * @param env
   */
  public KafkaProducer(IEnvironment env, String clientId) {
    super(env, clientId);
  }

  /**
   * This will be a JSON string of the form {verb:<verb>,cargo:<cargo>)
   * @param msg
   * @param partition
   */
  public void sendMessage(String topic, String message, String key, Integer partition) {
    environment.logDebug("TQElasticKSProducer "+message);
    super.sendMessage(topic, message, key, partition);
  }

  /* (non-Javadoc)
   * @see org.topicquests.backside.kafka.apps.AbstractKafkaApp#close()
   */
  @Override
  public void close() {
    super.close();
  }

}

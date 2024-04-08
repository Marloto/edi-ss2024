package de.thi.informatik.edi.jms;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

@EnableJms
@Configuration
public class ActiveMQConfiguration {
	
	// Liest Konfigurationen aus verschiedenen Quellen
	@Value("${destination.a:foo}")
	private String destinationA;

	// Erzeugt Objekte als Beans, die per Dependency
	// Injection genutzt werden können
	@Bean
	public Queue createQueue() {
		return new ActiveMQQueue(destinationA);
	}
	
	
    
    @Bean
    public JmsListenerContainerFactory<?> topicFactory(ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        // You could still override some of Boot's default if necessary.
        factory.setPubSubDomain(true);
        return factory;
    }
}
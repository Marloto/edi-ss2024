package de.thi.informatik.edi.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
    Logger logger = LoggerFactory.getLogger(getClass());

    @JmsListener(destination = "${destination.a:foo}")
    public void handleFromDestA(String content) {
    	logger.info("Message received (1.1) : "+content);
    }
    
    @JmsListener(destination = "${destination.a:foo}")
    public void handleFromDestA2(String content) {
    	logger.info("Message received (1.2) : "+content);
    }
}
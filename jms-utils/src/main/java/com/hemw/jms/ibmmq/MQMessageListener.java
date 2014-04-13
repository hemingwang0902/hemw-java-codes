package com.hemw.jms.ibmmq;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * MQ 消息监听器<br>
 * 创建日期：2012-5-25
 * 
 * @author <a href="mailto:hemw@mochasoft.com.cn">何明旺</a>
 * @version 1.0
 * @since 1.0
 */
public class MQMessageListener implements MessageListener {
    @Resource
    private JmsTemplate jmsTemplate;
    
    private String destinationName;

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage txtMsg = (TextMessage) message;
            try {
                System.out.println("收到 MQ 消息：" + txtMsg.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(final String message){
        jmsTemplate.send(destinationName, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);                
            }
        });
    }
    
    public static void main(String[] args) {
        @SuppressWarnings("resource")
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("com/hmw/jms/ibmmq/applicationContext-ibmmq.xml");
        MQMessageListener mQMessageListener = applicationContext.getBean(MQMessageListener.class);
        mQMessageListener.sendMessage("哈哈，发送一个 MQ 消息咯~~~");
    }
    
}
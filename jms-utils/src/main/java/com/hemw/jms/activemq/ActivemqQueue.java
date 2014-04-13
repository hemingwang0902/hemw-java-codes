package com.hemw.jms.activemq;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * ActiveMQ 点对对发送和接收消息
 * 
 * @author Carl He
 */
public class ActivemqQueue {
    private static String url = "tcp://192.168.1.131:61616";
    private static String user = ActiveMQConnection.DEFAULT_USER;
    private static String pwd = ActiveMQConnection.DEFAULT_PASSWORD;

    /**
     * 建立Connection
     * 
     * @return
     * @throws JMSException
     */
    protected Connection createConnection() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, pwd, url);
        Connection connection = connectionFactory.createConnection();

        // 如果需要持久化，则设置 clientID
        // connection.setClientID(clientID);

        connection.start();
        return connection;
    }

    /**
     * 建立Session
     * 
     * @param connection
     * @return
     * @throws JMSException
     */
    protected Session createSession(Connection connection) throws JMSException {
        boolean transacted = false; // 是否支持事务
        int ackMode = Session.AUTO_ACKNOWLEDGE;
        Session session = connection.createSession(transacted, ackMode);
        return session;
    }

    /**
     * 建立消息生产者
     * 
     * @param session
     * @param queueName 队列名称
     * @return
     * @throws JMSException
     */
    protected MessageProducer createProducer(Session session, String queueName) throws JMSException {
        Destination destination = session.createQueue(queueName);
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT); // 设置持久化模式，此处为不持久化

        // 设置消息存活时间（毫秒）
        // producer.setTimeToLive(timeToLive);

        return producer;
    }

    /**
     * 发送消息
     * 
     * @param queueName 队列名称
     * @param text 消息内容
     * @throws JMSException
     */
    public void sendMessage(String queueName, String text) throws JMSException {
        Connection connection = createConnection();
        Session session = createSession(connection);
        Message message = session.createTextMessage(text);
        MessageProducer producer = createProducer(session, queueName);
        producer.send(message);
        
        producer.close();
        session.close();
        connection.stop();
        connection.close();
    }

    /**
     * 用消息监听方式接收消息<br>
     * 注：QueueReceiver/QueueBrowser 可直接接收消息，但是更多的情况下我们采用消息监听方式，即实现 javax.jms.MessageListener 接口
     * @param queueName 队列名称
     * @throws JMSException
     */
    public void receiveMessage(String queueName) throws JMSException {
        Connection connection = createConnection();
        Session session = createSession(connection);
        Destination destination = session.createQueue(queueName);
        MessageConsumer consumer = session.createConsumer(destination);
        /*
        while (true) {
            //设置接收者接收消息的时间，为了便于测试，这里谁定为5min
            TextMessage message = (TextMessage) consumer.receive(5 * 60 * 1000);
            if (null != message) {
                System.out.println("收到消息：" + message.getText());
            } else {
                break;
            }
        }
        */
        consumer.setMessageListener(new MessageListener() {
            /*
             * (non-Javadoc)
             * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
             */
            public void onMessage(Message message) {
                TextMessage textMsg = (TextMessage) message;
                try {
                    // 消息 header 中常有的 属性定义
                    System.out.println("消息编码：" + textMsg.getJMSMessageID());
                    System.out.println("目标对象：" + textMsg.getJMSDestination());
                    // 消息的模式:分为持久模式和非持久模式, 默认是非持久的模式（2）
                    System.out.println("消息模式：" + textMsg.getJMSDeliveryMode());
                    System.out.println("消息发送时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(textMsg.getJMSTimestamp())));
                    // 这里是一个整型值: 0表示该消息永远不会过期
                    System.out.println("消息失效时间：" + textMsg.getJMSExpiration());
                    // 优先级 0~9,0表示最低
                    System.out.println("消息优先级：" + textMsg.getJMSPriority());
                    System.out.println("关联编码：" + textMsg.getJMSCorrelationID());
                    // 回复消息的地址(Destination类型),由发送者设定
                    System.out.println("回复消息的地址：" + textMsg.getJMSReplyTo());
                    // jms 不使用该字段，一般类型是由 用户自己定义
                    System.out.println("消息类型：" + textMsg.getJMSType());
                    // 如果是 真,表示客户端收到过该消息,但是并没有签收
                    System.out.println("是否签收过：" + textMsg.getJMSRedelivered());
                    // 消息属性 (properties)
                    System.out.println("用户编码：" + textMsg.getStringProperty("JMSXUserID"));
                    System.out.println("应用程序编码：" + textMsg.getStringProperty("JMSXApp1ID"));
                    System.out.println("已经尝试发送消息的次数：" + textMsg.getStringProperty("JMSXDeliveryCount"));
                    // 消息体(body) 中传递的内容
                    System.out.println("消息内容：" + textMsg.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws JMSException {
        ActivemqQueue test = new ActivemqQueue();
        String queueName = "queue.test";
//        test.sendMessage(queueName, "这是一个测试消息，哈哈……");
        test.receiveMessage(queueName);
    }
}
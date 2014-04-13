package com.hemw.jms.activemq;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * <ul>
 * 消息驱动Bean，在EJB中称为MDB，用来监听队列中的消息，异步接收处理消息。<br>
 * 补充：可以用 Spring 的消息代理来简化这个消息监听器，并且可以不用实现 javax.jms.MessageListener 接口
 * 
 * @author Carl He
 * 
 */
public class MyMessageListener implements MessageListener {
    private JmsTemplate jmsTemplate;
    /** 消息转换器 */
    private MessageConverter messageConverter;

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public void sendMessage(final MessageBean bean) {
        jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return messageConverter.toMessage(bean, session);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message paramMessage) {
        try {
            MessageBean mesDescription = (MessageBean) messageConverter.fromMessage(paramMessage);
            System.out.println("消息编码：" + mesDescription.getJMSMessageID());
            System.out.println("目标对象：" + mesDescription.getJMSDestination());
            // 消息的模式:分为持久模式和非持久模式, 默认是非持久的模式（2）
            System.out.println("消息模式：" + mesDescription.getJMSDeliveryMode());
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String temp = f.format(mesDescription.getJMSDate());
            System.out.println("消息发送时间：" + temp);
            // 这里是一个整型值: 0表示该消息永远不会过期
            System.out.println("消息失效时间：" + mesDescription.getJMSExpiration());
            // 优先级 0~9,0表示最低
            System.out.println("消息优先级：" + mesDescription.getJMSPriority());
            System.out.println("关联编码：" + mesDescription.getJMSCorrelationID());
            // 回复消息的地址(Destination类型),由发送者设定
            System.out.println("回复消息的地址：" + mesDescription.getJMSReplyTo());
            // jms 不使用该字段，一般类型是由 用户自己定义
            System.out.println("消息类型：" + mesDescription.getJMSType());
            // 如果是 真,表示客户端收到过该消息,但是并没有签收
            System.out.println("是否签收过：" + mesDescription.getJMSRedelivered());

            System.out.println("应用程序编码：" + mesDescription.getAppId());
            System.out.println("应用程序名称：" + mesDescription.getAppName());
            System.out.println("已经尝试发送消息的次数：" + mesDescription.getJmsxDeliveryCount());
            System.out.println("消息标题：" + mesDescription.getTitle());
            System.out.println("消息内容：" + mesDescription.getContent());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        @SuppressWarnings("resource")
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("com/hmw/jms/activemq/applicationContext-activemq.xml");
        MyMessageListener myMessageListener = applicationContext.getBean(MyMessageListener.class);
        
        MessageBean bean = new MessageBean();
        bean.setAppId("X-001");
        bean.setAppName("人力资源管理系统");
        bean.setTitle("待办提醒");
        bean.setContent("您有一条新待办，请点击查看详情");
        bean.setAdditionalInfo("没有什么附加信息");
        bean.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
        bean.setJMSType("text");
        bean.setJMSExpiration(0);
        bean.setJMSPriority(5);
        myMessageListener.sendMessage(bean);
    }
}

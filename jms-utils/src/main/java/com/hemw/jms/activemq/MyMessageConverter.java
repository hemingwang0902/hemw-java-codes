package com.hemw.jms.activemq;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * 消息转换器<br>
 * 创建日期：2012-8-29
 * 
 * @author <a href="mailto:hemw@mochasoft.com.cn">何明旺</a>
 * @version 1.0
 * @since 1.0
 */
public class MyMessageConverter implements MessageConverter {
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.jms.support.converter.MessageConverter#fromMessage(javax.jms.Message)
     */
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        if (message == null)
            throw new MessageConversionException("message is null, can't convert the message to com.hmw.jms.activemq.BeanMessage!");

        MessageBean bean = new MessageBean();
        // BeanUtils.copyProperties(message, beanMessage);

        // JMS 消息自身的一些属性
        bean.setJMSCorrelationID(message.getJMSCorrelationID());
        bean.setJMSDeliveryMode(message.getJMSDeliveryMode());
        bean.setJMSDestination(message.getJMSDestination());
        bean.setJMSExpiration(message.getJMSExpiration());
        bean.setJMSMessageID(message.getJMSMessageID());
        bean.setJMSPriority(message.getJMSPriority());
        bean.setJMSRedelivered(message.getJMSRedelivered());
        bean.setJMSReplyTo(message.getJMSReplyTo());
        bean.setJMSTimestamp(message.getJMSTimestamp());
        bean.setJMSType(message.getJMSType());
        // 自定义的 JMS 消息属性
        bean.setJmsxDeliveryCount(message.getStringProperty("JMSXDeliveryCount"));
        bean.setAppId(message.getStringProperty("appId"));
        bean.setAppName(message.getStringProperty("appName"));
        bean.setTitle(message.getStringProperty("title"));
        bean.setContent(message.getStringProperty("content"));
        bean.setAdditionalInfo(message.getObjectProperty("additionalInfo"));

        return bean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.jms.support.converter.MessageConverter#toMessage(java.lang.Object, javax.jms.Session)
     */
    public Message toMessage(Object obj, Session session) throws JMSException, MessageConversionException {
        if (!(obj instanceof MessageBean))
            throw new MessageConversionException("Object isn't a BeanMessage, object type is " + (obj == null ? "null" : obj.getClass().getName()));

        MessageBean bean = (MessageBean) obj;
        MapMessage message = session.createMapMessage();
        message.setStringProperty("appId", bean.getAppId());
        message.setStringProperty("appName", bean.getAppName());
        message.setStringProperty("title", bean.getTitle());
        message.setStringProperty("content", bean.getContent());
        message.setObjectProperty("additionalInfo", bean.getAdditionalInfo());
        // BeanUtils.copyProperties(beanMessage, message);
        message.setJMSDeliveryMode(bean.getJMSDeliveryMode());
        message.setJMSType(bean.getJMSType());
        message.setJMSExpiration(bean.getJMSExpiration());
        message.setJMSPriority(bean.getJMSPriority());

        return message;
    }
}

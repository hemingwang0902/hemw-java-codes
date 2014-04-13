package com.hemw.jms.activemq;

import java.io.Serializable;
import java.util.Date;

import javax.jms.Destination;

/**
 * 描述 JMS 消息的 javaBean
 * 
 * @author Carl He
 */
public class MessageBean implements Serializable {

    private static final long serialVersionUID = -872228835079592810L;

    /** 消息ID */
    private String jMSMessageID;
    /** 消息发送时间戳 */
    private long jMSTimestamp;
    /** 此属性主要是用来关联多个Message，如：需要回复一个消息的时候，通常把回复的消息的JMSCorrelationID设置为原来消息的jMSMessageID */
    private String jMSCorrelationID;
    /** 回复消息的目标对象 */
    private Destination jMSReplyTo;
    /** 目标对象 */
    private Destination jMSDestination;
    /** 消息的模式:分为持久模式和非持久模式, 默认是非持久的模式（2） */
    private int jMSDeliveryMode;
    /** 是否签收过：如果为true,表示客户端收到过该消息,但是并没有签收 */
    private boolean jMSRedelivered;
    /** 消息类型：jms 不使用该字段，一般类型是由 用户自己定义 */
    private String jMSType;
    /** 消息失效时间，0表是永不失效 */
    private long jMSExpiration;
    /** 消息优先级:0~9, 0表示最低 */
    private int jMSPriority;

    /** 已经尝试发送消息的次数 */
    private String jmsxDeliveryCount;
    /** 应用程序ID */
    private String appId;
    /** 应用程序名称 */
    private String appName;
    /** 消息标题 */
    private String title;
    /** 消息内容 */
    private String content;
    /** 消息的附加信息 */
    private Object AdditionalInfo;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getJMSMessageID() {
        return jMSMessageID;
    }

    public void setJMSMessageID(String messageID) {
        jMSMessageID = messageID;
    }

    public long getJMSTimestamp() {
        return jMSTimestamp;
    }

    public void setJMSTimestamp(long timestamp) {
        jMSTimestamp = timestamp;
    }

    public String getJMSCorrelationID() {
        return jMSCorrelationID;
    }

    public void setJMSCorrelationID(String correlationID) {
        jMSCorrelationID = correlationID;
    }

    public Destination getJMSReplyTo() {
        return jMSReplyTo;
    }

    public void setJMSReplyTo(Destination replyTo) {
        jMSReplyTo = replyTo;
    }

    public Destination getJMSDestination() {
        return jMSDestination;
    }

    public void setJMSDestination(Destination destination) {
        jMSDestination = destination;
    }

    public int getJMSDeliveryMode() {
        return jMSDeliveryMode;
    }

    public void setJMSDeliveryMode(int deliveryMode) {
        jMSDeliveryMode = deliveryMode;
    }

    public boolean getJMSRedelivered() {
        return jMSRedelivered;
    }

    public void setJMSRedelivered(boolean redelivered) {
        jMSRedelivered = redelivered;
    }

    public String getJMSType() {
        return jMSType;
    }

    public void setJMSType(String type) {
        jMSType = type;
    }

    public long getJMSExpiration() {
        return jMSExpiration;
    }

    public void setJMSExpiration(long expiration) {
        jMSExpiration = expiration;
    }

    public int getJMSPriority() {
        return jMSPriority;
    }

    public void setJMSPriority(int priority) {
        jMSPriority = priority;
    }

    public String getJmsxDeliveryCount() {
        return jmsxDeliveryCount;
    }

    public void setJmsxDeliveryCount(String jmsxDeliveryCount) {
        this.jmsxDeliveryCount = jmsxDeliveryCount;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getAdditionalInfo() {
        return AdditionalInfo;
    }

    public void setAdditionalInfo(Object additionalInfo) {
        AdditionalInfo = additionalInfo;
    }

    public Date getJMSDate() {
        return new Date(jMSTimestamp);
    }

}

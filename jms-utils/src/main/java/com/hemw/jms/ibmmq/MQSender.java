package com.hemw.jms.ibmmq;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

/**
 * IBM MQ 消息发送类<br>
 * 创建日期：2012-5-25
 * 
 * @author <a href="mailto:hemw@mochasoft.com.cn">何明旺</a>
 * @version 1.0
 * @since 1.0
 */
public class MQSender {
    static {
        // 建立MQ客户端调用上下文环境
        MQEnvironment.hostname = "198.17.20.20"; // 服务器ip地址
        MQEnvironment.port = 1414; // 服务器MQ服务端口
        MQEnvironment.CCSID = 1208; // 服务器MQ服务使用的编码
        MQEnvironment.channel = "SYSTEM.ADMIN.SVRCONN"; // 服务器连接通道名
    }

    public void sendMessage(String text) {
        try {
            MQQueueManager queueManager = new MQQueueManager("QM1"); // 队列管理器名称
            MQQueue queue = queueManager.accessQueue("test", MQConstants.MQOO_OUTPUT); // 队列名称

            MQMessage message = new MQMessage();
            message.format = MQConstants.MQFMT_STRING; // 字符串
            message.encoding = MQEnvironment.CCSID;
            message.characterSet = MQEnvironment.CCSID;
            // message.writeUTF(text);
            message.writeString(text);

            MQPutMessageOptions pmo = new MQPutMessageOptions(); // 消息属性
            pmo.options = MQConstants.MQPMO_LOGICAL_ORDER;

            queue.put(message, pmo); // 往队列上放置消息
            queueManager.commit(); // 提交事务处理
            queue.close(); // 关闭队列
            queueManager.disconnect(); // 关闭队列管理器
        } catch (MQException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MQSender().sendMessage("发送一个中文消息…");
    }

}

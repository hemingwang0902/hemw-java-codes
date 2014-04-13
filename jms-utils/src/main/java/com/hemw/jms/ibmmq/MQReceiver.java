package com.hemw.jms.ibmmq;

import java.io.IOException;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

/**
 * 接收 IBM MQ 消息<br>
 * 创建日期：2012-5-25
 * 
 * @author <a href="mailto:hemw@mochasoft.com.cn">何明旺</a>
 * @version 1.0
 * @since 1.0
 */
public class MQReceiver {
    static {
        // 建立MQ客户端调用上下文环境
        MQEnvironment.hostname = "198.17.20.20"; // 服务器ip地址
        MQEnvironment.port = 1414; // 服务器MQ服务端口
        MQEnvironment.CCSID = 1208; // 服务器MQ服务使用的编码
        MQEnvironment.channel = "SYSTEM.ADMIN.SVRCONN"; // 服务器连接通道名
    }

    public static MQMessage receiveMessage() {
        try {
            MQQueueManager queueManager = new MQQueueManager("QM1"); // 队列管理器名称
            MQQueue queue = queueManager.accessQueue("test", MQConstants.MQOO_INPUT_AS_Q_DEF); // 队列名称
            
            MQMessage message = new MQMessage();
            MQGetMessageOptions gmo = new MQGetMessageOptions(); // 消息属性
            gmo.options = MQConstants.MQGMO_LOGICAL_ORDER;
            queue.get(message, gmo);
            /*
            while (queue.getCurrentDepth() > 0) {
                // 接收消息
            }
            */
            queue.close(); // 关闭队列
            queueManager.disconnect(); // 关闭队列管理器
            return message;
        } catch (MQException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) throws IOException {
        MQMessage mqMessage = receiveMessage();
        System.out.println(mqMessage.readLine());
    }
    
}

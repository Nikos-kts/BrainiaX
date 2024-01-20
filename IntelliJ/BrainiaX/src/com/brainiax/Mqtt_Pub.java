package com.brainiax;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Mqtt_Pub implements Runnable {

    String topic = "BrainiaXCmd";
    String content = "Message from MqttPublishSample";
    int qos = 2;
    String broker = "tcp://localhost:1883";
    String clientId = "JavaApp"; MemoryPersistence persistence = new MemoryPersistence();

    public Mqtt_Pub(String message)
    {
        this.content = message;
    }

    @Override
    public void run() {

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            //System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            //System.out.println("Connected");
            //System.out.println("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            //System.out.println("Message: " + message + " published");
            sampleClient.disconnect();
            //System.out.println("Disconnected");

        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }

}

package com.brainiax;


//import com.sun.jmx.snmp.Timestamp;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


/**
 * Created by Nick on 3/1/2018.
 */
public class Mqtt_Sub implements Runnable, MqttCallback {

    Buffer buffer;
    String topic = "BrainiaXTime";
    int qos = 2;
    String broker = "tcp://localhost:1883";
    String clientId = "JavaAppSub"; MemoryPersistence persistence = new MemoryPersistence();

    public Mqtt_Sub(Buffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            Mqtt_Sub main = new Mqtt_Sub(buffer);
            sampleClient.setCallback(main);

            //System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            //System.out.println("Connected");

           // System.out.println("Subscribing to topic \"" + topic + "\" qos " + qos);
            sampleClient.subscribe(topic, qos);

        } catch (MqttException me){
            System.out.println("reason " +  me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }


    @Override
    public void connectionLost(Throwable cause){
        System.out.println("Connection lost! " + cause);
        System.exit(1);
    }

    @Override
    public void deliveryComplete (IMqttDeliveryToken token){

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        //String time = new Timestamp(System.currentTimeMillis()).toString();
        System.out.println("Time:\t"  + " Topic:\t" + topic + " Message:\t" + new String(message.getPayload()) + " QoS:\t" + message.getQos());
        buffer.setFrequency(Integer.parseInt( new String(message.getPayload())));
    }


}

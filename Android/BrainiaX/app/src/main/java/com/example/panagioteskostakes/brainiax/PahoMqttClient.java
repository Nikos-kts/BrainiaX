package com.example.panagioteskostakes.brainiax;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.Toast;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;


class PahoMqttClient {


    private String message;
    private Context con;
    private Button actbut;


    //Function Connect that connects the device to a broker and makes a MQTT Callback
    void connect(@NonNull MqttAndroidClient client) throws MqttException {


        try {
            IMqttToken token = client.connect(getMqttConnectionOption());
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getContext(),"Successful connection",Toast.LENGTH_SHORT).show();
                    System.out.println("Successful connection");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getContext(),"Failed to connect",Toast.LENGTH_SHORT).show();
                    System.out.println("Failure " + exception.toString());
                }
            });

            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean b, String s) {

                }

                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    message = mqttMessage.toString();
                    actbut.performClick();
                    System.out.println("Time:\t" +time +
                            " Message:\t" + message +
                            " QoS:\t" + mqttMessage.getQos());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //Function Disconnect that disconnects the device from a broker
    void disconnect(@NonNull MqttAndroidClient client) throws MqttException {
        IMqttToken mqttToken = client.disconnect();
        mqttToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Toast.makeText(getContext(),"Successfully disconnected",Toast.LENGTH_SHORT).show();
                System.out.println("Successfully disconnected");
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Toast.makeText(getContext(),"Failed to disconnect",Toast.LENGTH_SHORT).show();
                System.out.println("Failed to disconnect ");
            }
        });
    }


    @NonNull
    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        return mqttConnectOptions;
    }

    //Function Subscribe that subscribes a device to broker
    void subscribe(@NonNull MqttAndroidClient client, @NonNull final String topic, int qos) throws MqttException {
        IMqttToken token = client.subscribe(topic, qos);

        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Toast.makeText(getContext(),"Subscribed successfully",Toast.LENGTH_SHORT).show();
                System.out.println("Subscribed successfully");
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Toast.makeText(getContext(),"Failed to subscribe",Toast.LENGTH_SHORT).show();
                System.out.println("Failed to subscribe " + topic);

            }
        });

    }

    //Function Unsubscribe that unsubscribes a device from broker
    void unSubscribe(@NonNull MqttAndroidClient client, @NonNull final String topic) throws MqttException {

        IMqttToken token = client.unsubscribe(topic);

        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Toast.makeText(getContext(),"Unsubscribed successfully",Toast.LENGTH_SHORT).show();
                System.out.println("Unsubscribed successfully");
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Toast.makeText(getContext(),"Failed to unsubscribe",Toast.LENGTH_SHORT).show();
                System.out.println("Failed to unsubscribe " + topic);
            }
        });
    }

    //Function publish that sends a message to broker
    void publish(@NonNull MqttAndroidClient client, @NonNull String msg, int qos, @NonNull String topic)
            throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload;
        encodedPayload = msg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setId(320);
        message.setRetained(true);
        message.setQos(qos);
        client.publish(topic, message);
    }



    String getMessage(){return message;}
    void setContext(Context con1){con = con1;}
    private Context getContext(){return con;}
    void  setActbut(Button but){actbut = but;}
}



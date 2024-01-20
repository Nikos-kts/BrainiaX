package com.brainiax;


/**
 * Created by Nick on 14/2/2018.
 */
public class Consumer implements Runnable {

    Buffer buffer;
    String commandToSend;
    Thread mqttThread;

    public Consumer(Buffer buffer){
        this.buffer = buffer;
        new Thread(this, "Consumer").start();
    }

    @Override
    public void run(){
        while(true){
           commandToSend = buffer.get();
             mqttThread = new Thread(new Mqtt_Pub(commandToSend));
            mqttThread.start();
            try {
                mqttThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminateConsumer(){
        mqttThread.interrupt();
    }
}

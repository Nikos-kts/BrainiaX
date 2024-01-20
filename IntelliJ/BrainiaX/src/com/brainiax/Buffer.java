package com.brainiax;

/**
 * Created by Nick on 15/2/2018.
 */
public class Buffer {
    String[] pinakas;
    String result;
    public int index;
    int frequency;

    public Buffer(){
        this.pinakas = new String[264];
        this.index = 0;
        this.frequency = 4;
    }

    public synchronized String get() {
        if(index <= 0) {
            try {
                wait();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        index--;
        result = pinakas[index];
        pinakas[index] = null;
        notify();
        return result;
    }

    public synchronized  void put (String element){
        if(index > 263) {
            try {
                wait();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        this.pinakas[index] = element;
        index++;
        notify();
    }

    public void setFrequency(int frequency){
        this.frequency = frequency;
    }

    public int getFrequency(){
        return frequency;
    }
}

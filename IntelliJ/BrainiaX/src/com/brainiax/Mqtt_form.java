package com.brainiax;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * Created by Nick on 27/11/2017.
 */
public class Mqtt_form  {
    private JPanel commandPanel;
    private JLabel commandLabel;
    private JTextField MqttCommand;
    private JButton confirmBtn;
    private JButton exitBtn;
    private Thread currentThread = null;

    public Mqtt_form() {

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (currentThread != null && currentThread.isAlive())
                {
                    currentThread.interrupt();
                }
                System.exit(0);

            }
        });


        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!Objects.equals(MqttCommand.getText(), "")) {
                    Thread mqttThread = new Thread(new Mqtt_Pub(MqttCommand.getText()));
                    currentThread = mqttThread;
                    mqttThread.run();
                    MqttCommand.setText("");
                }
            }
        });
        MqttCommand.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    confirmBtn.doClick();
                }
            }
        });
    }
    public JPanel getJPanel() {
        return this.commandPanel;
    }


}

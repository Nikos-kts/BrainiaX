package com.brainiax;

import featureSelectionMetricsPackage.FeatureVector;
import featureSelectionMetricsPackage.calculateEntropy;
import featureSelectionMetricsPackage.kNN;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.swing.UIManager.*;

public class Main {


    public static void main(String[] args) {


        /* Fixing the Frame Window */
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available set the GUI to another look and feel.
        }
        JFrame frame = new JFrame("Mqtt_form");
        frame.setContentPane(new Mqtt_form().getJPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);


         /* Creating buffer */
        Buffer buffer = new Buffer();

        /* Subscribing to Broker */
        Thread mqttThreadSub = new Thread(new Mqtt_Sub(buffer));
        mqttThreadSub.start();



        /* Spawning consumer thread */
        Consumer consumer = new Consumer(buffer);

        /* Reading the csv Files from folder */
        final File folder = new File("../Data Final for Software Development");
        String csvFile = "../../../../Data Final for Software Development/1.EyesClosed1_1.csv";
        FeatureVector[] X;
        X = getXtotal("../Training Set for Software Development/Training Set.csv");
        if( X == null){
            System.out.println("The total X is null. Aborting. . . ");
            System.exit(1);
        }//edw ftiaxnw to sunolo X apo to training set


        String line;
        String csvSplitBy = ",";
        String result = "";
        int lineCounter = 0;
        double NoOfFiles = 0;
        double SucceededClassifications = 0.0;
        double FailedClassifications = 0.0;
        int index = 0,mark = 0;
        NumberFormat formatter = new DecimalFormat("#.000");

        FeatureVector featureVector = null;
        kNN classifier = new kNN(); // kanw ena instance tou kNN algo gia na ton usarw gia ola ta csv files

        int Marker, MarkerH;
        double[] dataVector = new double[14];
        int[] channelQualities = new int[14];
        double[] AF3 = new double[1500], F7 = new double[1500], F3 = new double[1500], FC5 = new double[1500], T7 = new double[1500], P7 = new double[1500], O1 = new double[1500],
                O2 = new double[1500], P8 = new double[1500], T8 = new double[1500], FC6 = new double[1500], F4 = new double[1500], F8 = new double[1500], AF4 = new double[1500];
        double[][] channels = new double[1000][14];
        calculateEntropy AF3_entropy = null, F7_entropy = null, F3_entropy = null, FC5_entropy = null, T7_entropy = null, P7_entropy = null, O1_entropy = null, O2_entropy = null,
                P8_entropy = null, T8_entropy = null, FC6_entropy = null, F4_entropy = null, F8_entropy = null, AF4_entropy = null;


        for (final File fileEntry : folder.listFiles()) { //kanw iterate ola ta csv arxeia
            if (fileEntry.getName().contains(".csv")) {
                csvFile = folder + "/" + fileEntry.getName();
            }
            else
                continue;
         //   System.out.println("Read: \"" + csvFile + "\"");

            System.out.println("Opening " + fileEntry.getName());


            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

                br.readLine();
                String[] content;


                while ((line = br.readLine()) != null) { //mexri to telos tou csv file

                    lineCounter++;
                    // Parse Channels
                    content = line.split(csvSplitBy);
                    int qos = 0;
                    for (int i = 14; i < 28; i++)
                    {
                        if(Double.parseDouble(content[i]) != 4.0) qos++;
                    }

                    Marker = (int) Double.parseDouble(content[28]);
                    MarkerH = (int) Double.parseDouble(content[29]);
                    if(Marker != 0 && MarkerH != 0) mark++;

                    if(qos == 0) {
                        AF3[index] = Double.parseDouble(content[0]);
                        F7[index] = Double.parseDouble(content[1]);
                        F3[index] = Double.parseDouble(content[2]);
                        FC5[index] = Double.parseDouble(content[3]);
                        T7[index] = Double.parseDouble(content[4]);
                        P7[index] = Double.parseDouble(content[5]);
                        O1[index] = Double.parseDouble(content[6]);
                        O2[index] = Double.parseDouble(content[7]);
                        P8[index] = Double.parseDouble(content[8]);
                        T8[index] = Double.parseDouble(content[9]);
                        FC6[index] = Double.parseDouble(content[10]);
                        F4[index] = Double.parseDouble(content[11]);
                        F8[index] = Double.parseDouble(content[12]);
                        AF4[index] = Double.parseDouble(content[13]);
                        index++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            double[] AF3a = new double[index], F7a = new double[index], F3a = new double[index], FC5a = new double[index], T7a = new double[index],
                    P7a = new double[index], O1a = new double[index], O2a = new double[index], P8a = new double[index], T8a = new double[index],
                    FC6a = new double[index], F4a = new double[index], F8a = new double[index], AF4a = new double[index];

            for(int i = 0; i < index; i++)
            {
                AF3a[i] = AF3[i]; F7a[i] = F7[i]; F3a[i] = F3[i]; FC5a[i] = FC5[i]; T7a[i] = T7[i]; P7a[i] = P7[i]; O1a[i] = O1[i];
                O2a[i] = O2[i]; P8a[i] = P8[i]; T8a[i] = T8[i]; FC6a[i] = FC6[i]; F4a[i] = F4[i]; F8a[i] = F8[i]; AF4a[i] = AF4[i];
            }

            /* Creating the DataVector to be inserted in Feature Vector */
            dataVector[0] = AF3_entropy.calculateEntropy(AF3a);
            dataVector[1] = F7_entropy.calculateEntropy(F7a);
            dataVector[2] = F3_entropy.calculateEntropy(F3a);
            dataVector[3] = FC5_entropy.calculateEntropy(FC5a);
            dataVector[4] = T7_entropy.calculateEntropy(T7a);
            dataVector[5] = P7_entropy.calculateEntropy(P7a);
            dataVector[6] = O1_entropy.calculateEntropy(O1a);
            dataVector[7] = O2_entropy.calculateEntropy(O2a);
            dataVector[8] = P8_entropy.calculateEntropy(P8a);
            dataVector[9] = T8_entropy.calculateEntropy(T8a);
            dataVector[10] = FC6_entropy.calculateEntropy(FC6a);
            dataVector[11] = F4_entropy.calculateEntropy(F4a);
            dataVector[12] = F8_entropy.calculateEntropy(F8a);
            dataVector[13] = AF4_entropy.calculateEntropy(AF4a);


            for(int i = 0; i < 1500; i++)
            {
                AF3[i] = 0.0; F7[i] = 0.0; F3[i] = 0.0; FC5[i] = 0.0; T7[i] = 0.0; P7[i] = 0.0; O1[i] = 0.0;
                O2[i] = 0.0; P8[i] = 0.0; T8[i] = 0.0; FC6[i] = 0.0; F4[i] = 0.0; F8[i] = 0.0; AF4[i] = 0.0;
            }

            if (lineCounter > 2 && mark == 2) { //Throwing the empty csv files
                NoOfFiles++;
                if (csvFile.contains("Opened")) {//AF3, F7, F3, FC5, T7, P7, O1, O2, P8, T8, FC6, F4, F8, AF4;
                    featureVector = new FeatureVector("EyesOpened", dataVector);
                } else if (csvFile.contains("Closed")) {
                    featureVector = new FeatureVector("EyesClosed", dataVector);
                }


                //Time to classify the sample
                result = classifier.classify(featureVector, X);

                //make producer
                if( result.contains("Opened"))
                    buffer.put("Open Flashlight");
                else if (result.contains("Closed"))
                    buffer.put("Close Flashlight");

                if (Objects.equals(result, featureVector.getLabelClassName())) {

                    System.out.println("Experiment Class: " + featureVector.getLabelClassName() +  " Classified class: " + result + " SUCCEEDED");
                    SucceededClassifications++;
                }
                else {

                    System.out.println("Experiment Class: " + featureVector.getLabelClassName() + " Classified class: " + result + " FAILED");
                    FailedClassifications++;
                }

           }
            lineCounter = 0;
            index = 0;
            mark = 0;
            try {
                TimeUnit.SECONDS.sleep(buffer.getFrequency());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        consumer.terminateConsumer();
        System.out.println((int)SucceededClassifications + " " + (int)FailedClassifications + " " + (int)NoOfFiles);
        System.out.println("The percentage of succeeded classifications is :" + formatter.format((SucceededClassifications/NoOfFiles)*100) + "%");
    }



    /* Method for Creating total X */
    private static FeatureVector[] getXtotal(String file){

        String line;
        String[] content;
        double[] data = new double[14];
        FeatureVector[] featureVectors = new FeatureVector[36];
        int index = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            br.readLine();

            while ((line = br.readLine()) != null) {
                content = line.split(",");
                data[0] =  Double.parseDouble(content[1]);
                data[1] = Double.parseDouble(content[2]);
                data[2] = Double.parseDouble(content[3]);
                data[3] = Double.parseDouble(content[4]);
                data[4] = Double.parseDouble(content[5]);
                data[5] = Double.parseDouble(content[6]);
                data[6] = Double.parseDouble(content[7]);
                data[7] = Double.parseDouble(content[8]);
                data[8] = Double.parseDouble(content[9]);
                data[9] = Double.parseDouble(content[10]);
                data[10] = Double.parseDouble(content[11]);
                data[11] = Double.parseDouble(content[12]);
                data[12] = Double.parseDouble(content[13]);
                data[13] = Double.parseDouble(content[14]);

                featureVectors[index++] = new FeatureVector(content[0], data);

            }
            return featureVectors;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

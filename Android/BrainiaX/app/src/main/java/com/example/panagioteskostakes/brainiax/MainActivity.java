package com.example.panagioteskostakes.brainiax;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    Button buton,butoff,tetbut,turnon, turnoff,pub;
    private Timer timerAsync;
    private TimerTask timerTaskAsync;
    CameraManager camman;
    String camid;
    MediaPlayer mp1;
    MediaPlayer mp2;
    String firststr, str, secondstr, frequency = "10";
    MqttAndroidClient client;
    PahoMqttClient pahoMqttClient;
    String localhost  = "192.168.43.73";
    String port       = "1883";
    String broker     = "tcp://"+localhost+":1883";
    String clientId   = "BrainiaXAndroid";
    String topic1     = "BrainiaXCmd";
    String topic2     =  "BrainiaXTime";
    int qos           = 2;
    int turon         = 0;
    int turoff        = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checking internet connectivity
        startBackgroundInternetCheck();

        //Creating an object of class PahoMqttClient and trying to connect to broker using the default url
        pahoMqttClient = new PahoMqttClient();
        pahoMqttClient.setContext(getApplicationContext());
        client = new MqttAndroidClient(MainActivity.this,broker,clientId);
        mp1 = MediaPlayer.create(getApplicationContext(), R.raw.flashon);
        mp2 = MediaPlayer.create(getApplicationContext(), R.raw.flashoff);

        Toast.makeText(getApplicationContext(),"Connecting to broker.....",Toast.LENGTH_SHORT).show();

        try {
            pahoMqttClient.connect(client);
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Failed to connect to broker " + broker,Toast.LENGTH_SHORT).show();
        }

        //Creating the buttons of application
        buton = findViewById(R.id.wifion);
        butoff = findViewById(R.id.wifioff);
        tetbut = findViewById(R.id.textbut);
        turnon = findViewById(R.id.turnon);
        turnoff = findViewById(R.id.turnoff);
        pub = findViewById(R.id.pub_button);



        //*********Button: Subscribe*********
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Trying to subscribe to broker that is or isn't connected to
                Toast.makeText(getApplicationContext(), "Subscribing to broker....", Toast.LENGTH_LONG).show();
                try {
                    pahoMqttClient.subscribe(client, topic1, qos);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });

        //*********Button: Unsubscribe*********
        butoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Trying to unsubscribe from broker
                try {
                    pahoMqttClient.unSubscribe(client, topic1);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });



        //*********Button: Publish*********
        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Trying to unsubscribe from broker
                try {
                    pahoMqttClient.publish(client,frequency,qos,topic2);
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });



        //*********Button: Activate Command*********
        tetbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Taking the message that was published and trying to split word-by-word
                str = pahoMqttClient.getMessage();
                System.out.println(str);
                if(str == null){
                    str = "error";
                }
                firststr = str;
                secondstr = "";
                String[] words = str.split(" ", 2);
                if(words.length != 1) {
                    firststr = words[0].toUpperCase();
                    secondstr = words[1];
                }

                //Checking and activating the commands or returning error pop up message
                switch (firststr) {
                    case "OPEN":
                        secondstr = secondstr.toUpperCase();
                        if (secondstr.equals("FLASHLIGHT")) {
                            //Checking if the activation command is already in use
                            turoff = 0;
                            if(turon == 1)
                            {
                                Toast.makeText(getApplicationContext(), "Flashlight already activated", Toast.LENGTH_LONG).show();

                                break;
                            }
                            turon = 1;

                            //Checking first if the device supports flash
                            boolean isFlashavailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

                            if(!isFlashavailable)
                            {
                                Toast.makeText(getApplicationContext(), "Error: Your device doesn't support flash", Toast.LENGTH_LONG).show();
                                return;
                            }
                            camman = (CameraManager) getSystemService(CAMERA_SERVICE);
                            try {
                                if (camman != null) {
                                    camid = camman.getCameraIdList()[0];
                                }
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }

                            //Trying to activate flashlight with sound effect and a pop up message
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (camman != null) {
                                        //MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.flashon);
                                        mp1.start();
                                        camman.setTorchMode(camid,true);
                                        Toast.makeText(getApplicationContext(), "Flashlight Activated!", Toast.LENGTH_LONG).show();
                                    }
                                }

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: Enter a right command", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case "CLOSE":
                        secondstr = secondstr.toUpperCase();
                        if (secondstr.equals("FLASHLIGHT")) {
                            //Checking if the deactivation command is already in use
                            turon = 0;
                            if(turoff == 1)
                            {
                                Toast.makeText(getApplicationContext(), "Flashlight already deactivated", Toast.LENGTH_LONG).show();

                                break;
                            }
                            turoff = 1;

                            //Checking first if the device supports flash
                            boolean isFlashavailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

                            if(!isFlashavailable)
                            {
                                Toast.makeText(getApplicationContext(), "Error: Your device doesn't support flash", Toast.LENGTH_LONG).show();
                                return;
                            }
                            //Trying to deactivate flashlight with sound effect and a pop up message
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.flashoff);
                                    mp2.start();
                                    camman.setTorchMode(camid,false);
                                    Toast.makeText(getApplicationContext(), "Flashlight Deactivated!", Toast.LENGTH_LONG).show();
                                }

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: Enter a right command", Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Error: Enter a right command", Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });

        //Passing activation command button to pahoclient for Perfomclick purposes
        pahoMqttClient.setActbut(tetbut);

        //*********Button: Turn On Flash*********
        turnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking if the activation command is already in use
                turoff = 0;
                if(turon == 1)
                {
                    Toast.makeText(getApplicationContext(), "Flashlight already activated", Toast.LENGTH_LONG).show();

                    return;
                }
                turon = 1;

                boolean isFlashavailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

                if(!isFlashavailable)
                {
                    Toast.makeText(getApplicationContext(), "Error: Your device doesn't support flash", Toast.LENGTH_LONG).show();
                    return;
                }
                camman = (CameraManager) getSystemService(CAMERA_SERVICE);
                try {
                    if (camman != null) {
                        camid = camman.getCameraIdList()[0];
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

                //Trying to activate flashlight with sound effect and a pop up message
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (camman != null) {
                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.flashon);
                            mp.start();
                            camman.setTorchMode(camid,true);
                            Toast.makeText(getApplicationContext(), "Flashlight Activated!", Toast.LENGTH_LONG).show();
                        }
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        //*********Button: Turn Off Flash*********
        turnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking if the deactivation command is already in use
                turon = 0;
                if(turoff == 1)
                {
                    Toast.makeText(getApplicationContext(), "Flashlight already deactivated", Toast.LENGTH_LONG).show();

                    return;
                }
                turoff = 1;

                //Checking first if the device supports flash
                boolean isFlashavailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

                if(!isFlashavailable)
                {
                    Toast.makeText(getApplicationContext(), "Error: Your device doesn't support flash", Toast.LENGTH_LONG).show();
                    return;
                }
                //Trying to deactivate flashlight with sound effect and a pop up message
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.flashoff);
                        mp.start();
                        camman.setTorchMode(camid,false);
                        Toast.makeText(getApplicationContext(), "Flashlight Deactivated!", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerAsync.cancel();
        timerTaskAsync.cancel();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //Creating the menu bar with Settings, Exit and a Set IP for setting a new IP
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivityForResult(new Intent(Settings.ACTION_SETTINGS),0);
                return true;
            case R.id.ip:
                Set_IP();
                return true;
            case R.id.frequency:
                Set_Frequency();
                return true;
            case R.id.quit:
                onClickExit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Set_Frequency() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText freEditText = new EditText(MainActivity.this);
        freEditText.setHint("00");
        builder.setMessage("Set message frequency (>3)");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        freEditText.setLayoutParams(lp);
        builder.setView(freEditText);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){


            @Override
            public void onClick(DialogInterface dialog, int id) {
                frequency = freEditText.getText().toString();
                System.out.println(frequency);
                dialog.cancel();
            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }

        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Creating the Set IP menubar item
    private void Set_IP() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText subEditText = new EditText(MainActivity.this);
        final EditText subEditText2 = new EditText(MainActivity.this);
        subEditText.setHint("0.0.0.0");
        subEditText2.setHint("0000");
        builder.setMessage("Set IP and port for connection to MQTT Broker");
        LinearLayout lp = new LinearLayout(this);
        lp.setOrientation(LinearLayout.VERTICAL);
        lp.addView(subEditText);
        lp.addView(subEditText2);
        builder.setView(lp);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){


            @Override
            public void onClick(DialogInterface dialog, int id) {
                localhost = subEditText.getText().toString();
                port = subEditText2.getText().toString();
                System.out.println(localhost+":"+port);
                broker     = "tcp://"+localhost+":"+port;
                MqttAndroidClient reclient = new MqttAndroidClient(MainActivity.this,broker,clientId);
                Toast.makeText(getApplicationContext(),"Reconnecting to broker....",Toast.LENGTH_SHORT).show();

                try {
                    pahoMqttClient.connect(reclient);
                } catch (MqttException e) {
                    Toast.makeText(getApplicationContext(),"Failed to reconnect to broker " + broker,Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                client = reclient;
                dialog.cancel();
            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }

        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Creating the Exit menubar item
    private void onClickExit() { onBackPressed(); }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to quit?");
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){


            @Override
            public void onClick(DialogInterface dialog, int id) {
                WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifi != null) {
                    wifi.setWifiEnabled(false);
                }

                try {
                    pahoMqttClient.disconnect(client);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                finish();
                System.exit(-1);
            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }

        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Background Task for internet check
    public void startBackgroundInternetCheck() {
        final Handler handler = new Handler();
        timerAsync = new Timer();
        timerTaskAsync = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {

                    }
                });
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            BackgroundPerformAsyncTask performBackgroundTask = new BackgroundPerformAsyncTask(getApplicationContext());
                            performBackgroundTask.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        };
        timerAsync.schedule(timerTaskAsync,0,20000);
    }


    public class BackgroundPerformAsyncTask extends AsyncTask {
        private Context context;

        BackgroundPerformAsyncTask(Context context) {
            this.context = context;
        }
        @Override
        protected Object doInBackground(Object[] params) {
            if (!isNetworkAvailable()) {
                WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifi != null) {
                    wifi.setWifiEnabled(true);
                }
                while(!isNetworkAvailable()){}
            }
            return null;
        }
    }

    //Function that checks if device is connected to WiFi or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

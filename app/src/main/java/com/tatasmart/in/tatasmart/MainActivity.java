package com.tatasmart.in.tatasmart;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,SensorEventListener {

    private ImageButton b1,btnAccNotifier,btnSetPreferences,news,help,traffic,support,feedback;
    private int counter = 0,resetCounter=0;
    private TextView prevLoc, currLoc, speed;
    private LocationManager lm;
    private  String currAdd;
    private boolean upperFlag=false,lowerFlag=false;
    private Location mcurrLocation, lastLocation;
    private long prevTime, currTime;
    private double currSpeed,lowerLimit,upperLimit;
    // private String speed;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Context mycontext = this;
    private float lastX, lastY, lastZ;
    private boolean flag = false;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private String[] numbers = new String[5];
    private String user_name;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float vibrateThreshold = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;

    public Vibrator v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getBaseContext(), MyPhoneStateListener.class));
        initializeNumbers();
        flag = false;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = 30;
        } else {
            Toast.makeText(getBaseContext(),"Your device does not have accelerometer",Toast.LENGTH_LONG).show();
        }
        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        b1 = (ImageButton) findViewById(R.id.mapbutton);
        btnSetPreferences=(ImageButton)findViewById(R.id.btnSetPreferences);
        help = (ImageButton)findViewById(R.id.helpButton);
        support = (ImageButton)findViewById(R.id.supportButton);
        feedback = (ImageButton)findViewById(R.id.feedbackButton);
        traffic = (ImageButton)findViewById(R.id.trafficButton);
        news = (ImageButton)findViewById(R.id.newsButton);

        //initializeViews();
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent("com.tatasmart.in.tatasmart.newsActivity");
                startActivity(in);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent("com.tatasmart.in.tatasmart.helpActivity");
                startActivity(in);
            }
        });
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent("com.tatasmart.in.tatasmart.supprtActivity");
                startActivity(in);
            }
        });
        traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent("com.tatasmart.in.tatasmart.trafficActivity");
                startActivity(in);
            }
        });


        b1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent("com.tatasmart.in.tatasmart.MapsActivity");
                        startActivity(in);
                    }
                }
        );

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        btnSetPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in2=new Intent("com.tatasmart.in.tatasmart.AppPreferences");
                startActivity(in2);
            }
        });
    }

    public void initializeNumbers() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int i, j;
        for (i = 0; i < 5; i++) {
            j = i + 1;
            numbers[i] = SP.getString("phone_number" + j, "0");
            //Toast.makeText(getBaseContext(),"number read is:"+numbers[i],Toast.LENGTH_SHORT).show();
        }
        user_name=SP.getString("owner_name","NA");
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Log.i(TAG, "Location services connected.");

        lm = (LocationManager) mycontext.getSystemService(mycontext.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(lm.GPS_PROVIDER)) {
            //final Toast t1 = Toast.makeText(mycontext, "Location Service is Disabled", Toast.LENGTH_LONG);
            //t1.show();

            AlertDialog.Builder alert = new AlertDialog.Builder(mycontext);
            alert.setTitle("GPS is not enabled!");
            alert.setMessage("Please enable GPS");
            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1, Bundle.EMPTY);
                    }
                    try {
                        Thread.sleep(10000);
                        startLocation();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Toast t1 = Toast.makeText(mycontext,"Location Service Must be enabled. Application exiting...",Toast.LENGTH_LONG);
                    t1.show();
                    onDestroy();
                }
            });

            alert.show();
        } else {
            startLocation();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
//        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            //Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
            Toast toast = Toast.makeText(this, "Location services connection failed with code " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String lspeed,uspeed;
        mcurrLocation = location;
        updateLocation();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        uspeed = sp.getString("max_speed","0");
        lspeed = sp.getString("min_speed","0");

        //Toast t = Toast.makeText(mycontext,"Upper Limit: "+uspeed+" & Lower Limit: "+lspeed,Toast.LENGTH_SHORT);
        //t.show();

        resetCounter++;
        if(resetCounter==100){
            counter=0;
            resetCounter=0;
            upperFlag=false;
            lowerFlag=false;
        }
        double ls = Double.parseDouble(lspeed);
        double us = Double.parseDouble(uspeed);

        if(location.getSpeed()>us){
            upperFlag=true;
        }
        if(location.getSpeed()<ls){
            lowerFlag=true;
        }
        if(upperFlag && location.getSpeed()<ls){
            counter++;
        }
        if(lowerFlag && location.getSpeed()>us){
            counter++;
        }
        if(counter>10){
            //sendSmsToOwner();
        }

    }

    public void updateLocation() {
        //location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //lastLocation = mcurrLocation;
        //prevLoc.setText(lastLocation.getLatitude() + "," + lastLocation.getLongitude());
        //currLoc.setText(mcurrLocation.getLatitude() + "," + mcurrLocation.getLongitude());
        //speed.setText(mcurrLocation.getSpeed()+ "");
    }

    protected void startLocation(){
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    protected void removeLocation(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        flag = false;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if(mGoogleApiClient.isConnected()){
            startLocation();
        }
        else{
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if (((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) && flag == false) {
            flag = true;

            Geocoder geocoder;
            List<Address> addresses=null;
            geocoder = new Geocoder(mycontext, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(mcurrLocation.getLatitude(), mcurrLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0);
                // address = addresses.get(0).getAddressLine(0).toString(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();

                currAdd = address+","+city+","+state+","+country;

            }
            catch (Exception e){
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("DO YOU NEED HELP???");
            builder.setMessage("YOUR VEHICLE HAS JUST EXPERIENCED A JERK. DO YOU NEED HELP?");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            builder.setPositiveButton("YES I NEED HELP", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    initializeNumbers();
                    Toast t2 = Toast.makeText(getBaseContext(), "You have opted to ask for help, a message will be sent to all your favorite numbers.Hold tight everything will be fine..", Toast.LENGTH_LONG);
                    t2.show();
                    SmsManager.getDefault().sendTextMessage(numbers[0], null, "Your friend " + user_name + " has met an accident please help him. His location is: "+currAdd, null, null);
                    Toast.makeText(getBaseContext(), "Sent message to " + numbers[0], Toast.LENGTH_SHORT).show();
                    SmsManager.getDefault().sendTextMessage(numbers[1], null, "Your friend " + user_name + " has met an accident please help him. His location is: "+currAdd, null, null);
                    Toast.makeText(getBaseContext(), "Sent message to " + numbers[1], Toast.LENGTH_SHORT).show();
                    SmsManager.getDefault().sendTextMessage(numbers[2], null, "Your friend " + user_name + " has met an accident please help him. His location is: "+currAdd, null, null);
                    Toast.makeText(getBaseContext(), "Sent message to " + numbers[2], Toast.LENGTH_SHORT).show();
                    SmsManager.getDefault().sendTextMessage(numbers[3], null, "Your friend " + user_name + " has met an accident please help him. His location is: "+currAdd, null, null);
                    Toast.makeText(getBaseContext(), "Sent message to " + numbers[3], Toast.LENGTH_SHORT).show();
                    SmsManager.getDefault().sendTextMessage(numbers[4], null, "Your friend " + user_name + " has met an accident please help him. His location is: "+currAdd, null, null);
                    Toast.makeText(getBaseContext(), "Sent message to " + numbers[4], Toast.LENGTH_SHORT).show();
                    Toast.makeText(getBaseContext(),"Messages have been sent to all your favorites, they will be here any moment hold tight...",Toast.LENGTH_LONG).show();
                    //Toast.makeText(getBaseContext(), "Calling your first favorite tell him/her your condition, number is:" + numbers[0], Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("NO I AM FINE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    flag = false;
                }
            });

            builder.show();

            /*final AlertDialog alert = builder.create();

            v.vibrate(50);

            final Handler handler  = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (alert.isShowing()) {
                        alert.cancel();
                        //flag = false;
                        Toast t2 = Toast.makeText(getBaseContext(), "You have opted to ask for help, a message will be sent to all your favorite numbers.Hold tight everything will be fine..", Toast.LENGTH_LONG);
                        t2.show();


                       // sendSms(getBaseContext());



                    }
                }
            };

            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Toast t = Toast.makeText(mycontext, "Alert Dialog Box Dismissed", Toast.LENGTH_SHORT);
                    t.show();

                    handler.removeCallbacks(runnable);
                }
            });

            handler.postDelayed(runnable, 3000);

//            builder.show();
alert.show();*/

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        flag = false;
        removeLocation();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
}

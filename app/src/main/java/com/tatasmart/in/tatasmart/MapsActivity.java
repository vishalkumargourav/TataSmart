package com.tatasmart.in.tatasmart;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private Location currLocation;
    private MarkerOptions options;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private ImageButton hospital, restaurants, police, atm, tolltax;
    private ImageButton myLocation, directions;
    private Context mycontext = this;
    private String m_Text, curr_speed;
    private EditText input;
    private String currAdd, address, city, state, country, postalCode, knownName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        startService(new Intent(getBaseContext(), MyPhoneStateListener.class));
        hospital = (ImageButton) findViewById(R.id.hospitalButton);
        restaurants = (ImageButton) findViewById(R.id.hotelButton);
        atm = (ImageButton) findViewById(R.id.atmButton);
        police = (ImageButton) findViewById(R.id.policeButton);
        tolltax = (ImageButton) findViewById(R.id.tollButton);

        myLocation = (ImageButton) findViewById(R.id.myLocation);
        directions = (ImageButton) findViewById(R.id.directions);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                currLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                handleNewLocation(currLocation);
            }
        });

        hospital.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                double currentLatitude = currLocation.getLatitude();
                double currentLongitude = currLocation.getLongitude();
                String uriString = "geo:" + currentLatitude + "," + currentLongitude + "?q=hospitals";
                Uri gmmIntentUri = Uri.parse(uriString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(mapIntent, CONNECTION_FAILURE_RESOLUTION_REQUEST, Bundle.EMPTY);
                }
            }
        });
        restaurants.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                double currentLatitude = currLocation.getLatitude();
                double currentLongitude = currLocation.getLongitude();
                String uriString = "geo:" + currentLatitude + "," + currentLongitude + "?q=hotels";
                Uri gmmIntentUri = Uri.parse(uriString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(mapIntent, CONNECTION_FAILURE_RESOLUTION_REQUEST, Bundle.EMPTY);
                }
            }
        });
        police.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                double currentLatitude = currLocation.getLatitude();
                double currentLongitude = currLocation.getLongitude();
                String uriString = "geo:" + currentLatitude + "," + currentLongitude + "?q=police station";
                Uri gmmIntentUri = Uri.parse(uriString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(mapIntent, CONNECTION_FAILURE_RESOLUTION_REQUEST, Bundle.EMPTY);
                }
            }
        });
        atm.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                double currentLatitude = currLocation.getLatitude();
                double currentLongitude = currLocation.getLongitude();
                String uriString = "geo:" + currentLatitude + "," + currentLongitude + "?q=atm's";
                Uri gmmIntentUri = Uri.parse(uriString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(mapIntent, CONNECTION_FAILURE_RESOLUTION_REQUEST, Bundle.EMPTY);
                }
            }
        });
        tolltax.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                double currentLatitude = currLocation.getLatitude();
                double currentLongitude = currLocation.getLongitude();
                String uriString = "geo:" + currentLatitude + "," + currentLongitude + "?q=toll taxes";
                Uri gmmIntentUri = Uri.parse(uriString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(mapIntent, CONNECTION_FAILURE_RESOLUTION_REQUEST, Bundle.EMPTY);
                }
            }
        });

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double currentLatitude = currLocation.getLatitude();
                double currentLongitude = currLocation.getLongitude();

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Get Directions");
                builder.setMessage("Enter Destination");

                final EditText input = new EditText(MapsActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();

                        String uriString = "google.navigation:q=" + m_Text;
                        //Toast t2 = Toast.makeText(mycontext, uriString, Toast.LENGTH_SHORT);
                        //t2.show();

                        Uri gmmIntentUri = Uri.parse(uriString);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(mapIntent, CONNECTION_FAILURE_RESOLUTION_REQUEST, Bundle.EMPTY);
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }

                });

                builder.show();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        startLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
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
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
            //Toast toast = Toast.makeText(this, "Location services connection failed with code " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currLocation = location;
        handleNewLocation(currLocation);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        curr_speed = sp.getString("max_speed", "0");
        double speed = Double.parseDouble(curr_speed);
        if (location.getSpeed() >= speed) {
//            sendSms();
        }
    }

    protected void startLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    protected void removeLocation(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        Toast toast = Toast.makeText(this, "Location: ("+currentLatitude+","+currentLongitude+
                "). Speed: " + location.getSpeed(), Toast.LENGTH_SHORT);
        toast.show();

        mMap.clear();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        options = new MarkerOptions();
        options.position(latLng)
                .title("Current Location");
        mMap.addMarker(options);

        CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        //    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleApiClient.isConnected()){
            startLocation();
        }
        else{
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeLocation();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.d(TAG, "onStop fired ..............");
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        //Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }



}

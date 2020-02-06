package com.game.sirhowtask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    protected LocationListener locationListener;
    protected boolean gps_enabled, network_enabled;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE

    };
    HashMap<String, ArrayList<String>> hm;
    int PERMISSION_ALL = 1;
    private TextView tv, tv2;
    private GridView hotels;
    ListAdapter adapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hm = new HashMap<>();

        ArrayList al = new ArrayList();
        al.add("Hotel 1");
        al.add("Hotel 2");
        al.add("Hotel 3");
        al.add("Hotel 4");


        ArrayList al2 = new ArrayList();
        al2.add("Hotel 5");
        al2.add("Hotel 6");
        al2.add("Hotel 7");
        al2.add("Hotel 8");

        hm.put("229303", al);
        hm.put("201301", al2);

        tv = findViewById(R.id.loc);
        tv2 = findViewById(R.id.textView);
        hotels = findViewById(R.id.hotels);

        if (!hasPermissions(this, PERMISSIONS) || isNetworkAvailable()) {
            /**
             * Check if all permissions are still granted.
             * If not then do not load the map fragment and also display a message, asking user for permissions.
             * If yes then open map fragment.
             * */

            if (isNetworkAvailable()) {
                new AlertDialog
                        .Builder(this)
                        .setTitle("Internet not connected")
                        .setMessage("This app requires internet access")
                        .show();
            } else {
                new AlertDialog
                        .Builder(this)
                        .setTitle("Permissions not provided")
                        .setMessage("This app uses location permission. click okay to grant the permission.")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED) {
//                                    // Should we show an explanation?
//                                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                                            android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//                                        // Show an explanation to the user *asynchronously* -- don't block
//                                        // this thread waiting for the user's response! After the user
//                                        // sees the explanation, try again to request the permission.
//                                    } else {
//                                        // No explanation needed, we can request the permission.
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//                                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                                        // app-defined int constant. The callback method gets the
//                                        // result of the request.
//                                    }
                                };
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }

        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationChanged(Location location) {
                Log.e("Location", "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
                //tv.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());

                int pin = getAreaPin(location.getLatitude(), location.getLongitude());

                if (pin == 0) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000, 0, locationListener);
                } else {
                    tv.setText(pin+"");
                    progressDialog.cancel();
                    searchNearByPlace(pin);
                }

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.e("Latitude", provider);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.e("Latitude", provider);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e("Latitude", "status");
            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            tv2.setText("Please provide location Permission");
            return;
        }
        else {
            tv2.setText("");
            progressDialog = ProgressDialog.show(MainActivity.this, "",
                    "Finding your location", false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, locationListener);


    }

    private void searchNearByPlace(int pin) {
//        locationManager.removeUpdates(locationListener);
//        locationManager = null;

        //201301
        adapter = new HotelAdapter(MainActivity.this, hm.get(pin+""));
        hotels.setAdapter(adapter);

    }


    private int getAreaPin(double latitude, double longitude) {
        locationManager.removeUpdates(locationListener);
        locationManager = null;
        Geocoder geoCoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> matches = null;
        int x = 0;
        try {
            matches = geoCoder.getFromLocation(latitude, longitude, 1);
            if(matches == null)
                return 0;

            Log.e("Address", matches + "");

            String toCut = matches.get(0).getPostalCode();
            Log.e("toCut", toCut);

            x = Integer.parseInt(toCut.trim());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return x;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo == null || !activeNetworkInfo.isConnected();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        /**
         * Checks if the user has specified the required permissions.
         * @return boolean variable. True if all permissions already provided. False if not.
         */
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000, 0, locationListener);
                    progressDialog = ProgressDialog.show(MainActivity.this, "",
                            "Finding your location", false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    tv2.setText("");
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();

                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

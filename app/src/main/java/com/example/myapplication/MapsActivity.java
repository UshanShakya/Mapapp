package com.example.myapplication;

import android.Manifest;
import android.Manifest.permission;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION};

    private GoogleMap mMap;
    MarkerOptions markerOptions;
    Marker marker;
    LatLng trackerCordinates;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        markerOptions = new MarkerOptions().position(new LatLng(0, 0)).title("My Location");

        if (Build.VERSION.SDK_INT >= 23 && !checkPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else getLocation();
        if (!checkLocationEnabled()) {
            showAlert(1);
        }
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
        marker = mMap.addMarker(markerOptions);

//        Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng myCordinates = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(myCordinates);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCordinates));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 100000, 10, this);
     }

     private boolean checkLocationEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

     }

     private boolean checkPermissionGranted(){
        if(checkSelfPermission(permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED){
            Log.v("myLog","Permission Granted.");
            return true;
        }else {
            Log.v("myLog","Permission Denied.");
            return false;
        }

     }

     private void showAlert(final int status){
        String message, title, btnText;

        if (status == 1){
            message = "Your Locations Seettings is set to 'Off'.\n Please Enable Location to "+
                    "use this app";
            title= "Enable Location";
            btnText ="Location Settings";

        }
        else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";

        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (status == 1){
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                        else{
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                        }
                    }
                });
     }
}

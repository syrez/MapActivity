package com.exmple.android.myapplication;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.exmple.android.myapplication.Helper.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, MapsTask.Callback {

    protected static final String TAG = "MainActivity";

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }


    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double latitude;
    private double longitude;
    private LatLng latLng;
    private Marker currLocationMarker;


    private GoogleMap mMap;
    private EditText city;
    private EditText country;
    private EditText street;
    private Button submitBtn;
    private EditText description;
    private LatLng markerPosition;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        new GeoLocation(this).buildGoogleApiClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new GeoLocation(this));

        city = (EditText) findViewById(R.id.et_location_city);
        country = (EditText) findViewById(R.id.et_location_country);
        street = (EditText) findViewById(R.id.et_location_street);
        description = (EditText) findViewById(R.id.et_location_description);
        submitBtn = (Button) findViewById(R.id.btn_location_submit);

        byte[] imageByteArray = getIntent().getByteArrayExtra("image");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Artwks_" + timeStamp + ".png";

        //region Clicks Methods
        submitBtn.setOnClickListener(v -> {
            ParseObject artwork = new ParseObject("Artworks");
            ParseFile imageFile = new ParseFile(imageFileName, imageByteArray);
            ParseGeoPoint point = new ParseGeoPoint(latLng.latitude, latLng.longitude);
            artwork.put("title", "test");
            artwork.put("description", description.getText().toString());
            artwork.put("country", country.getText().toString());
            artwork.put("city", city.getText().toString());
            artwork.put("street", street.getText().toString());
            artwork.put("image", imageFile);
            artwork.put("location", point);
            artwork.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(LocationActivity.this, "Artwork added", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        });
        //endregion
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        RxPermissions rxPermissions = new RxPermissions(this);
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setExpirationDuration(20000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    //region LOCATION METHODS
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(this, " Connection failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        new GeoLocation(this).setMarker(location, mMap);
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void setAdress(Address address) {
        if (address == null) {
            Toast.makeText(this, "Oups we couldnt find your position, please try again", Toast.LENGTH_SHORT).show();
            country.setText("");
            city.setText("");
            street.setText("");
        } else {
            country.setText(address.getCountryName());
            city.setText(address.getLocality());
            street.setText(address.getAddressLine(0));
        }
    }

    //endregion

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public LatLng getMarkerPosition() {
        return markerPosition;
    }

    public void setMarkerPosition(LatLng markerPosition) {
        this.markerPosition = markerPosition;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}


package com.exmple.android.myapplication.Helper;

import android.location.Location;

import com.exmple.android.myapplication.LocationActivity;
import com.exmple.android.myapplication.MapsTask;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class GeoLocation implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private final LocationActivity activity;
    private Marker currLocationMarker;

    public GeoLocation(LocationActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        activity.setmMap(googleMap);
        GoogleMap mMap = activity.getmMap();
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setOnMarkerDragListener(this);
    }

    public void setMarker(Location location, GoogleMap mMap) {
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        activity.setLatLng(latLng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your Position");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        currLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    public synchronized void buildGoogleApiClient() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(activity)
                .addOnConnectionFailedListener(activity)
                .addApi(LocationServices.API)
                .build();
        activity.setmGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        LatLng markerPosition = marker.getPosition();
        activity.setMarkerPosition(markerPosition);
        MapsTask mapsTask = new MapsTask(activity);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(markerPosition);
        LatLngBounds bounds = builder.build();
        activity.getmMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        mapsTask.execute(activity.getMarkerPosition());
    }
}



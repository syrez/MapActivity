package com.exmple.android.myapplication;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by k3vin on 19-03-17.
 */

public class MapsTask extends AsyncTask<LatLng, Void, Address> {
    private final Context context;

    public MapsTask(Context context) {
        this.context = context;
    }

    interface Callback {
        void setAdress(Address adress);
    }

    protected Address doInBackground(LatLng... params) {

        Geocoder myLocation = new Geocoder(this.context, Locale.getDefault());
        try {
            List<Address> listAddress = myLocation.getFromLocation(params[0].latitude, params[0].longitude, 1);
            if (listAddress.size() > 0) {
                Address address = listAddress.get(0);
                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Address address) {
        super.onPostExecute(address);
        ((Callback) context).setAdress(address);
    }
}

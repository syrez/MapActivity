package com.exmple.android.myapplication;

import com.parse.ParseObject;

/**
 * Created by k3vin on 20-03-17.
 */

public class ArtworkRepository {

    private final ParseObject artwork;

    ArtworkRepository() {
        this.artwork = new ParseObject("Artwork");
    }
/*
    public void submitArtwork(Artwork artwork) {
        this.artwork.put("title", artwork.getTitle());
        this.artwork.put("description", artwork.getTitle());
        this.artwork.put("Country", artwork.getCountry);
        this.artwork.put("City", artwork.getCity());
        ParseGeoPoint point = new ParseGeoPoint(artwork.getLatitude(), artwork.getLongitude());
        this.artwork.put("location", point);
        this.artwork.saveInBackground();
    }

    public void getArtworksNearUser(){
        ParseGeoPoint userLocation = (ParseGeoPoint) userObject.get("location");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PlaceObject");
        query.whereNear("location", userLocation);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

            }
        };
    }*/
}

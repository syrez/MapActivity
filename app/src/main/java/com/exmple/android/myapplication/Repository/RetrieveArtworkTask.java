package com.exmple.android.myapplication.Repository;

import android.content.Context;
import android.os.AsyncTask;

import com.exmple.android.myapplication.Model.Artwork;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by k3vin on 23-03-17.
 */

public class RetrieveArtworkTask extends AsyncTask<Void, Void, List<Artwork>> {
    private final Context context;
    private List<Artwork> listArtworkServer = new ArrayList<Artwork>();


    public interface Callback {
        public void getArtworkList(List<Artwork> list);
    }

    public RetrieveArtworkTask(Context context) {
        this.context = context;
    }

    @Override
    protected List doInBackground(Void... params) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Artworks");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject object : objects) {

                    ParseFile postImage = object.getParseFile("image");
                    String imageUrl = postImage.getUrl();
                    String title = (String) object.get("title");
                    String id = (String) object.getObjectId();
                    listArtworkServer.add(new Artwork(title, imageUrl, id));
                }
            }
        });
        return listArtworkServer;
    }

    @Override
    protected void onPostExecute(List<Artwork> artworks) {
        super.onPostExecute(artworks);
        ((Callback) context).getArtworkList(artworks);
    }
}

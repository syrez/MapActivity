package com.exmple.android.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.gjiazhe.panoramaimageview.GyroscopeObserver;
import com.gjiazhe.panoramaimageview.PanoramaImageView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ArtworkDetailsActivity extends AppCompatActivity {

    private GyroscopeObserver gyroscopeObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artwork_details);
        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        gyroscopeObserver = new GyroscopeObserver();
        // Set the maximum radian the device should rotate to show image's bounds.
        // It should be set between 0 and π/2.
        // The default value is π/9.
        gyroscopeObserver.setMaxRotateRadian(Math.PI / 20);

        PanoramaImageView panoramaImageView = (PanoramaImageView) findViewById(R.id.iv_artworkdetail_image);
        // Set GyroscopeObserver for PanoramaImageView.
        panoramaImageView.setGyroscopeObserver(gyroscopeObserver);


        ImageView artworkImage = (ImageView) findViewById(R.id.iv_artworkdetail_image);
        //TextView artworkDesc = (TextView) findViewById(R.id.tv_artworkdetail_description);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Artworks");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Toast.makeText(ArtworkDetailsActivity.this, object.get("title").toString(), Toast.LENGTH_SHORT).show();
                    ParseFile postImage = object.getParseFile("image");
                    String imageUrl = postImage.getUrl();
                    Glide.with(ArtworkDetailsActivity.this).load(imageUrl).centerCrop().override(1500, Target.SIZE_ORIGINAL).into(artworkImage);
                    //artworkDesc.setText(object.get("description").toString());
                } else {
                    // something went wrong
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        gyroscopeObserver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister GyroscopeObserver.
        gyroscopeObserver.unregister();
    }
}

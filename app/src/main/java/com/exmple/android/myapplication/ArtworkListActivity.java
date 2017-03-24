package com.exmple.android.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.exmple.android.myapplication.Adapters.ArtworksRecyclerViewAdapter;
import com.exmple.android.myapplication.Model.Artwork;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArtworkListActivity extends AppCompatActivity implements ArtworksRecyclerViewAdapter.Callback {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private CameraPosition cameraPosition;
    private LocationManager locationManager;
    private LocationListener listener;
    private String mCurrentPhotoPath;
    private Cursor cursor;
    private Bitmap currBitmapImage;
    List<Artwork> listArtworkServer = new ArrayList<Artwork>();
    private StaggeredGridLayoutManager artworksGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView takePic = (TextView) findViewById(R.id.tv_main_takePic);


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Artworks");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        ParseFile postImage = object.getParseFile("image");
                        String imageUrl = postImage.getUrl();
                        String title = (String) object.get("title");
                        String id = (String) object.getObjectId();
                        listArtworkServer.add(new Artwork(title, imageUrl, id));
                        populateListArtwork();
                    }
                } else {
                    Toast.makeText(ArtworkListActivity.this, "we couldn't find any results :/ Try to refreshZ", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void invokeCamera() {
        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uriSavedImage = createImageFile();
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            galleryAddPic();
            getimage();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            currBitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Intent i = new Intent(this, NewArtworkActivity.class);
            i.putExtra("image", byteArray);
            startActivity(i);
        }
    }

    private Uri createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Artworks2");
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
        }
        File image = new File(imagesFolder, "QR_" + timeStamp + ".png");
        mCurrentPhotoPath = image.getAbsolutePath();
        return Uri.fromFile(image);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void populateListArtwork() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        artworksGridLayoutManager = new StaggeredGridLayoutManager(2, 1);

        recyclerView.setLayoutManager(artworksGridLayoutManager);

        List<Artwork> listArtworkLocal = this.listArtworkServer;

        ArtworksRecyclerViewAdapter rcAdapter = new ArtworksRecyclerViewAdapter(ArtworkListActivity.this, listArtworkLocal);
        recyclerView.setAdapter(rcAdapter);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth) {

        float aspectRatio = bm.getWidth() / (float) bm.getHeight();
        int width = 480;
        int height = Math.round(width / aspectRatio);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, width, height, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void getimage() {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();

        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE,
        };


        cursor = getApplicationContext().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Media.DATA + " like ? ",
                        new String[]{"%Artworks%"},
                        MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if (cursor == null) {
            cursor = getApplicationContext().getContentResolver()
                    .query(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                            projection,
                            MediaStore.Images.Media.DATA + " like ? ",
                            new String[]{"%Artworks%"},
                            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        }

        while (cursor.moveToNext()) {
            String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            File imageFile = new File(mCurrentPhotoPath);
            if (imageFile.canRead() && imageFile.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
                Log.i("Bitmap", bm.toString());
                currBitmapImage = getResizedBitmap(bm, 150);
                break;
            }
        }
    }

    @Override
    public void getIdFromArtwork(String id) {
        Intent i = new Intent(this, ArtworkDetailsActivity.class);
        i.putExtra("id", id);
        startActivity(i);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This er");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", (dialog, id) ->

                        dialog.cancel());
                builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alert11 = builder.create();
                alert11.show();
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }


}

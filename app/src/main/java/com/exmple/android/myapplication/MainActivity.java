package com.exmple.android.myapplication;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RxPermissions rxPermissions = new RxPermissions(this);

        RxView.clicks(findViewById(R.id.tv_main_takePic))
                .compose(rxPermissions.ensure(Manifest.permission.CAMERA))
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        invokeCamera();
                    } else {
                        Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show();
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
            Intent i = new Intent(this, LocationActivity.class);
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

    private String getPictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        return "TakenPhoto" + timestamp + "jpg";
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
}

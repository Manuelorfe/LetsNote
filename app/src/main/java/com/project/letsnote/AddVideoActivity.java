package com.project.letsnote;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.firebase.client.Firebase;
import com.project.letsnote.login.Login;
import com.project.letsnote.login.PrefUtils;
import com.project.letsnote.login.User;

import java.io.File;

import butterknife.ButterKnife;

public class AddVideoActivity extends MenuActivity implements LocationListener {

    AmazonS3 s3;
    TransferUtility transferUtility;
    FloatingActionButton fab;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    User user;
    Location loc = null;
    TextView titulo, descripcion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createTypeFace();

        titulo = (TextView) findViewById(R.id.ed_titulo);
        descripcion = (TextView) findViewById(R.id.ed_descripcion);
        Button button = (Button) findViewById(R.id.button);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        // callback method to call credentialsProvider method.
        credentialsProvider();
        // callback method to call the setTransferUtility method
        setTransferUtility();

        user = PrefUtils.getCurrentUser(this);

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (descripcion.getText().toString().length() > 0 && titulo.getText().toString().length() > 0) {
                    Intent takePicture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else {
                    if (titulo.getText().toString().length() <= 0) {
                        titulo.setError("Introduce titulo");
                    } else if (descripcion.getText().toString().length() <= 0) {
                        descripcion.setError("Introduce descripcion");
                    } else {
                        titulo.setError("Introduce titulo");
                        descripcion.setError("Introduce descripcion");
                    }
                }

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {

                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //file path of captured image
                    String filePath = cursor.getString(columnIndex);
                    //file path of captured image
                    File f = new File(filePath);

                    cursor.close();

                    TransferObserver transferObserver = transferUtility.upload(
                            "letsnotebucket", //* The bucket to upload to *//*
                            user.getName() + f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("/"), f.getAbsolutePath().length()),  //* The key for the uploaded object *//*
                            f,  //* The file where the data to upload exists *//*
                            CannedAccessControlList.PublicRead);
                    Log.e("DIR", user.getName() + f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("/"), f.getAbsolutePath().length()));
                    transferObserverListener(transferObserver);

                    String urlImagen = "https://s3.amazonaws.com/" + transferObserver.getBucket() + "/" + transferObserver.getKey();
                    urlImagen = urlImagen.replace(" ", "+");

                    //Le decimos a Firebase que este sera el contexto
                    Firebase.setAndroidContext(getBaseContext());

                    //Creamos una referencia a nuestra bd de Firebase y a su hijo
                    final Firebase refNota = new Firebase("https://letsnote.firebaseio.com/").child("notas").push();

                    Nota nota = new Nota();

                    nota.setDescripcion(descripcion.getText().toString());
                    nota.setUrl_media(urlImagen);
                    nota.setLatitud(loc.getLatitude());
                    nota.setLongitud(loc.getLongitude());
                    nota.setUser(Login.FIREBASEKEY);
                    nota.setTitulo(titulo.getText().toString());
                    nota.setTipo("video");
                    refNota.setValue(nota);

                    titulo.setText("");
                    descripcion.setText("");


                    final Firebase refUser = new Firebase("https://letsnote.firebaseio.com/users").child(Login.FIREBASEKEY).child("notas").child(refNota.getKey());
                    refUser.setValue(true);

                    final Firebase refVideo = new Firebase("https://letsnote.firebaseio.com/users").child(Login.FIREBASEKEY).child("videos").child(refNota.getKey());
                    refVideo.setValue(true);

                    intent = new Intent(this, MapsActivity.class);

                }
                break;
        }

    }

    public void credentialsProvider() {
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:8150ce18-4d7f-4f31-ad69-43503b8ddff0",   // Identity Pool ID
                Regions.US_EAST_1                                   // Region
        );
        setAmazonS3Client(credentialsProvider);
    }

    /**
     * Create a AmazonS3Client constructor and pass the credentialsProvider.
     *
     * @param credentialsProvider
     */
    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider) {
        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);
        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
    }

    public void setTransferUtility() {
        transferUtility = new TransferUtility(s3, getApplicationContext());
    }

    public void transferObserverListener(TransferObserver transferObserver) {

        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e("statechange", state + "");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                Log.e("percentage", percentage + "");
                Toast.makeText(AddVideoActivity.this, "Subiendo nota", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error", "error");
            }

        });
    }

    @Override
    public void onLocationChanged(Location location) {
        //Cogemos la localizacion de ese momento
        loc = location;
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
}
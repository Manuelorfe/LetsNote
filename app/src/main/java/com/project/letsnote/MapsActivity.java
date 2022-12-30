package com.project.letsnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.letsnote.login.User;

import butterknife.ButterKnife;

public class MapsActivity extends MenuActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    Intent intent;
    private FABToolbarLayout layout;
    private View one, two, three, four;
    private View fab;
    Nota notaEnviar;
    User userEnviar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Firebase.setAndroidContext(getApplicationContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createTypeFace();

        layout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        //four = findViewById(R.id.four);
        fab = findViewById(R.id.fabtoolbar_fab);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        //four.setOnClickListener(this);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        layout.hide();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.one:
                intent = new Intent(this, AddPhotoActivity.class);
                startActivity(intent);
                break;
            case R.id.two:
                intent = new Intent(this, AddVideoActivity.class);
                startActivity(intent);
                break;
            case R.id.three:
                layout.hide();
                break;
/*            case R.id.four:
                Toast.makeText(this, "Four", Toast.LENGTH_SHORT).show();
                break;*/
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

        //Para mostrar el botton de myLocation
        mMap.setMyLocationEnabled(true);
        mMap.setPadding(0, 155, 0, 0);

        //Le decimos a Firebase que este sera el contexto
        Firebase.setAndroidContext(this);

        //Hacemos una referencia para recorrer todas las notas y añadir cada una a un marcador
        final Firebase notes = new Firebase("https://letsnote.firebaseio.com/").child("notas");
        notes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Recorremos todas las notas que haya en ese momento
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    Nota note = postSnapshot.getValue(Nota.class);
                    LatLng marker = new LatLng(note.getLatitud(), note.getLongitud());
                    mMap.addMarker(new MarkerOptions().position(marker).title(postSnapshot.getKey()).snippet(note.getTitulo()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        //Listener que cuando haces click en la la burbuja haga el intent al detail de la nota
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {

                intent = new Intent(getBaseContext(), DetailActivity.class);

                Firebase notes = new Firebase("https://letsnote.firebaseio.com/").child("notas");
                notes.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //Recorremos todas las notas que haya en ese momento
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            if(postSnapshot.getKey().equals(marker.getTitle())){
                                notaEnviar = postSnapshot.getValue(Nota.class);
                                intent.putExtra("notaKey", postSnapshot.getKey());
                                intent.putExtra("nota_ref", notaEnviar);
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

                Firebase users = new Firebase("https://letsnote.firebaseio.com/").child("users");
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //Recorremos todas las notas que haya en ese momento
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            if(postSnapshot.getKey().equals(notaEnviar.getUser())){
                                userEnviar = postSnapshot.getValue(User.class);
                                intent.putExtra("user_ref", userEnviar);
                                startActivity(intent);
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

            }
        });

    }
}
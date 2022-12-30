package com.project.letsnote.follows;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.project.letsnote.DetailActivity;
import com.project.letsnote.MenuActivity;
import com.project.letsnote.Nota;
import com.project.letsnote.PerfilAjeno;
import com.project.letsnote.R;
import com.project.letsnote.frags.adapters.AdapterFotos;
import com.project.letsnote.login.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;

public class SeguidosActivity extends MenuActivity {
    User userExtra;
    User user;
    String keyUser;
    String newKeyUser;
    GridView gvSeguidos;
    Map mapSeguidos;
    AdapterFollows adapterFollows;
    TextView titSeguidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguidos);
        Firebase.setAndroidContext(getApplicationContext());
        ButterKnife.bind(this);
        keyUser = getIntent().getExtras().getString("referencia_keyUser");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createTypeFace();

        gvSeguidos = (GridView)findViewById(R.id.gridView_seguidos);
        adapterFollows = new AdapterFollows(getApplicationContext());
        titSeguidos = (TextView) findViewById(R.id.titSeguidos);

        Typeface trut = Typeface.createFromAsset(getAssets(),"fonts/quirlycues.regular.ttf");
        titSeguidos.setTypeface(trut);
        gvSeguidos.setAdapter(adapterFollows);


        final Firebase refUsers = new Firebase("https://letsnote.firebaseio.com/").child("users");
        refUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(keyUser)) {
                    user = dataSnapshot.getValue(User.class);
                    if(!user.getSeguidos().isEmpty()){

                        mapSeguidos = user.getSeguidos();
                        Iterator it = mapSeguidos.keySet().iterator();
                        while(it.hasNext()){
                            String key = (String) it.next();
                            if((boolean)mapSeguidos.get(key) == true){
                                getSeguidos(key);
                            }
                        }


                        gvSeguidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                userExtra = adapterFollows.getItem(position);
                                //i.putExtra("referencia_user", userExtra);
                                final Firebase ref = new Firebase("https://letsnote.firebaseio.com/").child("users");

                                ref.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        Intent i = new Intent(getApplicationContext(), PerfilAjeno.class);
                                        User userSnapshot = dataSnapshot.getValue(User.class);
                                        if (userSnapshot.getFacebookID().equals(userExtra.getFacebookID())) {
                                            newKeyUser = dataSnapshot.getKey();
                                            i.putExtra("referencia_keyUser", newKeyUser );
                                            startActivity(i);
                                        }
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });

                            }
                        });
                    }else{
                        Log.e("XXXX", "Seguidos vacio");
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getSeguidos(String key){
        final Firebase refUsuario = new Firebase("https://letsnote.firebaseio.com/users/" + key);
        refUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                adapterFollows.add(user);
                adapterFollows.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // ignore
            }

        });
    }


}

package com.project.letsnote.follows;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.project.letsnote.MenuActivity;
import com.project.letsnote.PerfilAjeno;
import com.project.letsnote.R;
import com.project.letsnote.login.User;

import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;

public class SeguidoresActivity extends MenuActivity {
    User userExtra;
    User user;
    String keyUser;
    String newKeyUser;
    GridView gvSeguidores;
    Map mapSeguidores;
    AdapterFollows adapterFollows;
    TextView titSeguidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguidores);
        Firebase.setAndroidContext(getApplicationContext());
        ButterKnife.bind(this);
        keyUser = getIntent().getExtras().getString("referencia_keyUser");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createTypeFace();

        gvSeguidores = (GridView)findViewById(R.id.gridView_seguidores);
        adapterFollows = new AdapterFollows(getApplicationContext());
        titSeguidores = (TextView) findViewById(R.id.titSeguidores);

        Typeface tt = Typeface.createFromAsset(getAssets(),"fonts/quirlycues.regular.ttf");
        titSeguidores.setTypeface(tt);
        gvSeguidores.setAdapter(adapterFollows);

        final Firebase refUsers = new Firebase("https://letsnote.firebaseio.com/").child("users");
        refUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(keyUser)) {
                    user = dataSnapshot.getValue(User.class);
                    if(!user.getSeguidores().isEmpty()){

                        mapSeguidores = user.getSeguidores();
                        Iterator it = mapSeguidores.keySet().iterator();
                        while(it.hasNext()){
                            String key = (String) it.next();
                            if((boolean)mapSeguidores.get(key) == true){
                                getSeguidores(key);
                            }
                        }


                        gvSeguidores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        Log.e("XXXX", "Seguidores vacio");
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

    public void getSeguidores(String key){
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

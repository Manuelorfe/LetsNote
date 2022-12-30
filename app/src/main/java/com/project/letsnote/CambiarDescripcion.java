package com.project.letsnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.project.letsnote.login.Login;

import butterknife.ButterKnife;

public class CambiarDescripcion extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_descripcion);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createTypeFace();
        Firebase.setAndroidContext(getApplicationContext());

        final EditText cambiaDesc = (EditText) findViewById(R.id.editText);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Firebase descUser = new Firebase("https://letsnote.firebaseio.com/users").child(Login.FIREBASEKEY).child("descripcion");
                descUser.setValue(cambiaDesc.getText().toString());
                Intent intent = new Intent(getBaseContext(), MiPerfil.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

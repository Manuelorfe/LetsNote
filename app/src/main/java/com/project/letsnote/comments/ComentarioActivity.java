package com.project.letsnote.comments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.project.letsnote.MenuActivity;
import com.project.letsnote.Nota;
import com.project.letsnote.R;
import com.project.letsnote.follows.AdapterFollows;
import com.project.letsnote.login.Login;
import com.project.letsnote.login.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.ButterKnife;

public class ComentarioActivity extends MenuActivity {

    TextView textComentario, tvTitComentario;
    Button btnEnviar;
    ListView lvComentarios;
    String keyNota;
    String keyUser_propio;
    Nota nota;
    Map<String, String> mapComentarios;
    ArrayList<Comentario> commentsArray;
    AdapterComentarios adapterComentarios;
    Comentario com;
    String userFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentario);
        Firebase.setAndroidContext(getApplicationContext());
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createTypeFace();

        keyNota = getIntent().getExtras().getString("nota_key");
        keyUser_propio = Login.FIREBASEKEY;
        nota = (Nota) getIntent().getSerializableExtra("nota_ref");


        textComentario = (TextView) findViewById(R.id.textComentario);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        lvComentarios = (ListView) findViewById(R.id.lvComentarios);
        tvTitComentario = (TextView) findViewById(R.id.tvTitComentario);
        Typeface s = Typeface.createFromAsset(getAssets(), "fonts/quirlycues.regular.ttf");
        tvTitComentario.setTypeface(s);

        commentsArray = new ArrayList<>();
        adapterComentarios = new AdapterComentarios(getApplicationContext());

        lvComentarios.setAdapter(adapterComentarios);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comentario com = new Comentario();
                com.setIdUserPhoto(Login.FOTOUSER);
                com.setComentario(textComentario.getText().toString());
                Firebase ref = new Firebase("https://letsnote.firebaseio.com/").child("notas").child(keyNota).child("comentarios").push();
                ref.setValue(com);
                textComentario.setText("");
            }
        });


        Firebase refComentarios = new Firebase("https://letsnote.firebaseio.com/notas/").child(keyNota).child("comentarios");
        refComentarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                GenericTypeIndicator<Map<String,Comentario>> t = new GenericTypeIndicator<Map<String,Comentario>>() {
                };
                Map<String, Comentario> comentariosMap = snapshot.getValue(t);
                if (comentariosMap == null) {
                    System.out.println("No messages");
                } else {
                    adapterComentarios.clear();
                    for (Comentario comentariosDetails: comentariosMap.values() ){
                        adapterComentarios.add(comentariosDetails);
                        Log.e("coment", comentariosDetails.getComentario());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
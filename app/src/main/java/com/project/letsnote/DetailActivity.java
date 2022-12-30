package com.project.letsnote;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.project.letsnote.comments.ComentarioActivity;
import com.project.letsnote.heartAnimation.HeartButtonView;
import com.project.letsnote.login.Login;
import com.project.letsnote.login.User;

import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;

public class DetailActivity extends MenuActivity {

    TextView nombreUsuario, descripcionNota, tituloNota;
    public static TextView likesNum;
    ImageView imagenPerfil, imagenNota;
    ImageView heartxml;
    VideoView videoNota;
    HeartButtonView heartButton;
    Nota nota;
    public static String notaKey;
    User user;
    Button btnComentar;
    RelativeLayout relativeUser;

    public static boolean meGusta = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Firebase.setAndroidContext(getApplicationContext());
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createTypeFace();

        imagenPerfil = (ImageView) findViewById(R.id.fotoPerfil);
        nombreUsuario = (TextView) findViewById(R.id.nombreUsuario);
        imagenNota = (ImageView) findViewById(R.id.muestraFoto);
        videoNota = (VideoView) findViewById(R.id.muestraVideo);
        descripcionNota = (TextView) findViewById(R.id.textReport);
        likesNum = (TextView)findViewById(R.id.likesNum);
        heartButton = (HeartButtonView) findViewById(R.id.heartButton);
        heartxml = (ImageView)findViewById(R.id.heartImg);
        btnComentar = (Button) findViewById(R.id.btnComentar);
        tituloNota = (TextView) findViewById(R.id.tituloNota);
        relativeUser = (RelativeLayout) findViewById(R.id.relativeUser);
        //Recogemos el intent con la nota
        //nota = (Nota) getIntent().getSerializableExtra("nota_ref");
        notaKey = getIntent().getExtras().getString("notaKey");
        //Recogemos el intent con el user
        user = (User) getIntent().getSerializableExtra("user_ref");

        imagenNota.setVisibility(View.INVISIBLE);
        videoNota.setVisibility(View.INVISIBLE);

        Glide.with(this).load(user.getPictureUrl()).asBitmap().fitCenter().into(new BitmapImageViewTarget(imagenPerfil) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getBaseContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imagenPerfil.setImageDrawable(circularBitmapDrawable);
            }
        });
        nombreUsuario.setText(user.getName());

        final Firebase refNotas = new Firebase("https://letsnote.firebaseio.com/").child("notas");
        refNotas.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(notaKey)) {
                    nota = dataSnapshot.getValue(Nota.class);
                    tituloNota.setText(nota.getTitulo());
                    descripcionNota.setText(nota.getDescripcion());
                    setMedia(nota);
                    checkSiHayLikes(nota);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e("userEnDetailRemoved", nota.getUser());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.e("userEnDetailMoved", nota.getUser());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        relativeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                if(nota.getUser().equals(Login.FIREBASEKEY)){
                    intent = new Intent(getBaseContext(), MiPerfil.class);
                    intent.putExtra("referencia_keyUser", nota.getUser());

                    startActivity(intent);
                }else{
                    intent = new Intent(getBaseContext(), PerfilAjeno.class);
                    intent.putExtra("referencia_keyUser", nota.getUser());
                    Log.e("userEnDetail", nota.getUser());
                    startActivity(intent);
                }
            }
        });

        btnComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), ComentarioActivity.class);
                intent.putExtra("nota_ref", nota);
                intent.putExtra("referencia_keyUser", nota.getUser());
                intent.putExtra("referencia_propio", Login.FIREBASEKEY);
                intent.putExtra("nota_key", notaKey);
                startActivity(intent);
            }
        });
    }

    public void checkSiHayLikes(Nota nota){
        if(nota.getLikes().isEmpty()){
            meGusta = false;
            likesNum.setText(String.valueOf(0));
        }else if (!nota.getLikes().isEmpty()){
            int contador = 0;
            Map likes = nota.getLikes();
            Iterator it = likes.keySet().iterator();
            while(it.hasNext()){
                Object key = it.next();
                if((boolean)likes.get(key) == true){
                    contador++;
                }
            }
            likesNum.setText(String.valueOf(contador));
            checkMeGusta(Login.FIREBASEKEY);
        }

    }

    public void checkMeGusta(String firebaseKey){
        Map likes = nota.getLikes();
        Iterator it = likes.keySet().iterator();
        while(it.hasNext()){
            String key = (String) it.next();
            if (key.equals(firebaseKey) && ((boolean)likes.get(key) == true)) {
                meGusta = true;
                break;
            }else if (key.equals(firebaseKey) && ((boolean)likes.get(key) == false)) {
                Log.e("valor1", "es; "+key);
                meGusta = false;
            }else if(!key.equals(firebaseKey)){
                Log.e("valor2", "es; "+key);
                meGusta = false;
            }
        }

        if(DetailActivity.meGusta) {
            Log.e("boolean1", "el valor es"+ meGusta);
            heartxml.setImageResource(R.drawable.heart_on);
        }else if(!DetailActivity.meGusta) {
            Log.e("boolean2", "el valor es"+ meGusta);
            heartxml.setImageResource(R.drawable.heart_off);
        }
    }

    public void setMedia(Nota nota){
        if(nota.getTipo().equals("foto")) {
            imagenNota.setVisibility(View.VISIBLE);
            Glide.with(getBaseContext()).load(nota.url_media).into(imagenNota);
        }else if (nota.getTipo().equals("video")){
            MediaController mediaController = new MediaController(this);
            videoNota.setVisibility(View.VISIBLE);
            videoNota.setVideoPath(nota.getUrl_media());
            videoNota.setMediaController(mediaController);
            mediaController.hide();
            videoNota.start();
        }
    }
}

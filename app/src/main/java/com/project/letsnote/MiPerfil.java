package com.project.letsnote;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.project.letsnote.follows.SeguidoresActivity;
import com.project.letsnote.follows.SeguidosActivity;
import com.project.letsnote.frags.FotosFragment;
import com.project.letsnote.frags.VideosFragment;
import com.project.letsnote.login.PrefUtils;
import com.project.letsnote.login.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class MiPerfil extends MenuActivity {
    TextView nombreUsuario , localidadUsuario , numeroNotas , numeroSeguidos , numeroSeguidores , descripcionMiPerfil, btnEditar;
    ImageView fotoPerfil;
    EditText nuevaDescripcion;
    TextView btnDescripcion;
    Button btnGuardar;
    User userFace;
    Firebase ref;
    ViewPager viewPager;
    TabLayout tabs;
    String keyUser;
    ArrayList<User> arraySeguidos;
    ArrayList<User> arraySeguidores;
    Bundle bundle;
    LinearLayout linearSeguidos;
    LinearLayout linearSeguidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createTypeFace();
        Firebase.setAndroidContext(getApplicationContext());

        nombreUsuario = (TextView) findViewById(R.id.nombreUsuario);
        btnEditar = (TextView) findViewById(R.id.btnEditar);
        localidadUsuario = (TextView) findViewById(R.id.localidadUsuario);
        numeroNotas = (TextView) findViewById(R.id.numeroNotas);
        numeroSeguidos = (TextView) findViewById(R.id.numeroSeguidos);
        numeroSeguidores = (TextView) findViewById(R.id.numeroSeguidores);
        descripcionMiPerfil = (TextView) findViewById(R.id.descripcionMiPerfil);
        fotoPerfil = (ImageView) findViewById(R.id.imagenPerfil);
        btnDescripcion = (TextView) findViewById(R.id.btnEditar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabs = (TabLayout) findViewById(R.id.tabs);
        linearSeguidos = (LinearLayout) findViewById(R.id.linearSeguidos);
        linearSeguidores = (LinearLayout) findViewById(R.id.linearSeguidores);


        userFace = PrefUtils.getCurrentUser(this);
        ref = new Firebase("https://letsnote.firebaseio.com/").child("users");
        Query query = ref.orderByChild("facebookID").equalTo(userFace.getFacebookID());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                keyUser = dataSnapshot.getKey();
                User userFirebase = dataSnapshot.getValue(User.class);
                userFace = userFirebase;
                PerfilAjeno.bundle = new Bundle();
                PerfilAjeno.bundle.putSerializable("user", userFirebase);
                Log.e("userfirebase", userFirebase.getName());
                nombreUsuario.setText(userFirebase.getName());
                localidadUsuario.setText(userFirebase.getLocation());
                numeroNotas.setText(String.valueOf(userFirebase.getNotas().size()));
                checkSeguidos(userFirebase);
                checkSeguidores(userFirebase);
                descripcionMiPerfil.setText(userFirebase.getDescripcion());
                recuperarUsersSeguidos(userFirebase);
                recuperarUsersSeguidores(userFirebase);
                setupViewPager(viewPager);
                tabs.setupWithViewPager(viewPager);
                for (int i = 0; i < tabs.getTabCount(); i++) {
                    if (i == 0){
                        tabs.getTabAt(i).setIcon(R.drawable.camera);
                    }else{
                        tabs.getTabAt(i).setIcon(R.drawable.video);
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

        Glide.with(this).load(userFace.getPictureUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(fotoPerfil) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getBaseContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                fotoPerfil.setImageDrawable(circularBitmapDrawable);
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getBaseContext(), CambiarDescripcion.class);
                startActivity(intent);
            }
        });

        linearSeguidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verSeguidos();
            }
        });

        linearSeguidores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verSeguidores();
            }
        });

        /*setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);
        for (int i = 0; i < tabs.getTabCount(); i++) {
            if (i == 0){
                tabs.getTabAt(i).setIcon(R.drawable.camera);
            }else{
                tabs.getTabAt(i).setIcon(R.drawable.video);
            }
        }*/
    }

    public void checkSeguidos(User user){
        if(user.getSeguidos().isEmpty()){
            numeroSeguidos.setText(String.valueOf(0));
        }else if (!user.getSeguidos().isEmpty()){
            int contador = 0;
            Map likes = user.getSeguidos();
            Iterator it = likes.keySet().iterator();
            while(it.hasNext()){
                Object key = it.next();
                if((boolean)likes.get(key) == true){
                    contador++;
                }
            }
            numeroSeguidos.setText(String.valueOf(contador));
        }
    }

    public void checkSeguidores(User user){
        if(user.getSeguidores().isEmpty()){
            numeroSeguidores.setText(String.valueOf(0));
        }else if (!user.getSeguidores().isEmpty()){
            int contador1 = 0;
            Map likes = user.getSeguidores();
            Iterator it = likes.keySet().iterator();
            while(it.hasNext()){
                Object key = it.next();
                if((boolean)likes.get(key) == true){
                    contador1++;
                }
            }
            numeroSeguidores.setText(String.valueOf(contador1));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FotosFragment());
        adapter.addFragment(new VideosFragment());
        viewPager.setAdapter(adapter);
    }

    public void verSeguidos(){
        Intent intent = new Intent(getBaseContext(), SeguidosActivity.class);
        intent.putExtra("referencia_user", userFace);
        intent.putExtra("referencia_keyUser", keyUser);
        intent.putExtra("arraySeguidos", arraySeguidos);
        startActivity(intent);
    }

    public void verSeguidores(){
        Intent intent = new Intent(getBaseContext(), SeguidoresActivity.class);
        intent.putExtra("referencia_user", userFace);
        intent.putExtra("referencia_keyUser", keyUser);
        intent.putExtra("arraySeguidores", arraySeguidores);
        startActivity(intent);
    }

    public void recuperarUsersSeguidores(User user){
        if(!user.getSeguidores().isEmpty()){
            Map seguidores = user.getSeguidores();
            arraySeguidores = new ArrayList<>();
            Iterator it = seguidores.keySet().iterator();
            while(it.hasNext()){
                Object key = it.next();
                getSeguidores(key);
            }
        }
    }

    public void getSeguidores(final Object key){
        final Firebase ref = new Firebase("https://letsnote.firebaseio.com/").child("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User userSnapshot = postSnapshot.getValue(User.class);
                    if(postSnapshot.getKey().equals(key)){
                        arraySeguidores.add(userSnapshot);
                        Log.e("array dentro for", String.valueOf(arraySeguidores.size()));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }

        });
    }

    public void recuperarUsersSeguidos(User user){
        if(!user.getSeguidos().isEmpty()){
            Map seguidos = user.getSeguidores();
            arraySeguidos = new ArrayList<>();
            Iterator it = seguidos.keySet().iterator();
            while(it.hasNext()){
                Object key = it.next();
                getSeguidos(key);
            }
        }
    }

    public void getSeguidos(final Object key){
        final Firebase ref = new Firebase("https://letsnote.firebaseio.com/").child("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User userSnapshot = postSnapshot.getValue(User.class);
                    if(postSnapshot.getKey().equals(key)){
                        arraySeguidos.add(userSnapshot);

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }

        });
    }
}
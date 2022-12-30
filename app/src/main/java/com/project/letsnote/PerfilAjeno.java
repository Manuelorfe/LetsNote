package com.project.letsnote;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.project.letsnote.follows.SeguidoresActivity;
import com.project.letsnote.follows.SeguidosActivity;
import com.project.letsnote.frags.FotosFragment;
import com.project.letsnote.frags.VideosFragment;
import com.project.letsnote.login.Login;
import com.project.letsnote.login.User;
import com.project.letsnote.heartAnimation.HeartButtonView;
import com.project.letsnote.starAnimation.LikeButtonView;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;


public class PerfilAjeno extends MenuActivity {
    ImageView imagenPerfil;
    TextView titulo;
    TextView nombreUsuario;
    TextView localidadUsuario;
    TextView numeroNotas;
    TextView seguidos;
    LinearLayout linearSeguidores;
    LinearLayout linearSeguidos;

    public static TextView seguidores;
    TextView descripcionUser;
    public static TextView tvSeguir;
    TabLayout tabs;
    ViewPager viewPager;
    LikeButtonView starButton;
    ImageView ivStar;
    static Bundle bundle;
    public static String keyUser;
    public static User user;
    public static boolean esAmigo;

    ArrayList<User> arraySeguidores;
    ArrayList<User> arraySeguidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_ajeno);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createTypeFace();
        Firebase.setAndroidContext(getApplicationContext());
        keyUser = getIntent().getExtras().getString("referencia_keyUser");
        //user = (User) getIntent().getExtras().getSerializable("referencia_user");
        bundle = new Bundle();


        imagenPerfil = (ImageView) findViewById(R.id.imagenPerfil);
        titulo = (TextView) findViewById(R.id.tvTitulo);
        nombreUsuario = (TextView) findViewById(R.id.nombreUsuario);
        localidadUsuario = (TextView) findViewById(R.id.localidadUsuario);
        numeroNotas = (TextView) findViewById(R.id.numeroNotas);
        seguidos = (TextView) findViewById(R.id.seguidos);
        seguidores = (TextView) findViewById(R.id.seguidores);
        descripcionUser = (TextView) findViewById(R.id.descripcionUser);
        tabs = (TabLayout) findViewById(R.id.tabs);
        linearSeguidores = (LinearLayout)findViewById(R.id.linearSeguidores);
        linearSeguidos = (LinearLayout)findViewById(R.id.linearSeguidos);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        starButton = (LikeButtonView) findViewById(R.id.starButton);
        ivStar = (ImageView) findViewById(R.id.ivStar);
        tvSeguir = (TextView) findViewById(R.id.tvSeguir);


        final Firebase refUsers = new Firebase("https://letsnote.firebaseio.com/").child("users");
        refUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(keyUser)) {
                    user = dataSnapshot.getValue(User.class);
                    bundle.putSerializable("user", user);
                    recuperarUsersSeguidores(user);
                    recuperarUsersSeguidos(user);
                    setInfoUser(user);
                    checkSiHayAmigos(user);
                    checkSeguidos(user);
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

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FotosFragment());
        adapter.addFragment(new VideosFragment());
        viewPager.setAdapter(adapter);
    }

    public void setInfoUser(User user){
        nombreUsuario.setText(user.getName());
        localidadUsuario.setText(user.getLocation());
        descripcionUser.setText(user.getDescripcion());
        numeroNotas.setText(String.valueOf(user.getNotas().size()));

        Glide.with(this).load(user.getPictureUrl()).asBitmap().fitCenter().into(new BitmapImageViewTarget(imagenPerfil) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getBaseContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imagenPerfil.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public void checkSeguidos(User user){
        if(user.getSeguidos().isEmpty()){
            seguidos.setText(String.valueOf(0));
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
            seguidos.setText(String.valueOf(contador));
        }
    }

    public void checkSiHayAmigos(User user){
        if(user.getSeguidores().isEmpty()){
            esAmigo = false;
            seguidores.setText(String.valueOf(0));
        }else if (!user.getSeguidores().isEmpty()){
            int contador = 0;
            Map likes = user.getSeguidores();
            Iterator it = likes.keySet().iterator();
            while(it.hasNext()){
                Object key = it.next();
                if((boolean)likes.get(key) == true){
                    contador++;
                }
            }
            seguidores.setText(String.valueOf(contador));
            checkEsAmigo(Login.FIREBASEKEY);
        }

    }

    public void checkEsAmigo(String firebaseKey){
        Map seguidores = user.getSeguidores();
        Iterator it = seguidores.keySet().iterator();
        while(it.hasNext()){
            Object key = it.next();
            if (key.equals(firebaseKey) && ((boolean)seguidores.get(key) == true)) {
                esAmigo = true;
                break;
            }else if (key.equals(firebaseKey) && ((boolean)seguidores.get(key) == false)) {
                esAmigo = false;
            }else if(!key.equals(firebaseKey)){
                esAmigo = false;
            }
        }
        if(PerfilAjeno.esAmigo) {
            PerfilAjeno.tvSeguir.setText("Siguiendo");
            ivStar.setImageResource(R.drawable.ic_star_rate_on);
        }else if(!PerfilAjeno.esAmigo){
            PerfilAjeno.tvSeguir.setText("Seguir");
            ivStar.setImageResource(R.drawable.ic_star_rate_off);
        }
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




    public void verSeguidos(){
        Intent intent = new Intent(getBaseContext(), SeguidosActivity.class);

        intent.putExtra("referencia_keyUser", keyUser);

        startActivity(intent);
    }

    public void verSeguidores(){
        Intent intent = new Intent(getBaseContext(), SeguidoresActivity.class);
        intent.putExtra("referencia_keyUser", keyUser);
        startActivity(intent);
    }
}

class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        mFragmentList.get(position).setArguments(PerfilAjeno.bundle);
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {

        mFragmentList.add(fragment);
    }
}
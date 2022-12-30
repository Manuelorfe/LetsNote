package com.project.letsnote;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;
import com.project.letsnote.login.User;

import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;

public class ListaNotasActivity extends MenuActivity {

    ListView noteList;
    Intent intent;
    TextView tituloActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tituloActivity = (TextView) findViewById(R.id.textView3);
        Typeface tfe = Typeface.createFromAsset(getAssets(),"fonts/quirlycues.regular.ttf");
        tituloActivity.setTypeface(tfe);

        ButterKnife.bind(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createTypeFace();

        noteList = (ListView) findViewById(R.id.noteList);



        //Le decimos a Firebase que este sera el contexto
        Firebase.setAndroidContext(this);

        //Creamos una referencia a nuestra bd de Firebase
        final Firebase refNotas = new Firebase("https://letsnote.firebaseio.com/").child("notas");

        Query queryRef = refNotas.orderByChild("likes");


        final FirebaseListAdapter adapter = new FirebaseListAdapter<Nota>(this, Nota.class, R.layout.lista_notas_row, queryRef) {
            @Override
            protected void populateView(View v, final Nota nota, int position) {
                super.populateView(v, nota, position);

                final ImageView foto = (ImageView) v.findViewById(R.id.imagenUserLista);
                final TextView nombre = (TextView) v.findViewById(R.id.nombreUserLista);
                TextView titulo = (TextView) v.findViewById(R.id.tituloNotaLista);
                TextView likes = (TextView) v.findViewById(R.id.likesNotaLista);

                titulo.setText(nota.getTitulo());

                if(nota.getLikes().isEmpty()){
                   likes.setText(String.valueOf(0));
                }else if (!nota.getLikes().isEmpty()) {
                    int contador = 0;
                    Map likess = nota.getLikes();
                    Iterator it = likess.keySet().iterator();
                    while (it.hasNext()) {
                        Object key = it.next();
                        if ((boolean) likess.get(key) == true) {
                            contador++;
                        }
                    }
                    likes.setText(String.valueOf(contador));
                }

                //Creamos una referencia a nuestra bd de Firebase
                final Firebase refUsers = new Firebase("https://letsnote.firebaseio.com/").child("users");
                refUsers.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                        User a = snapshot.getValue(User.class);

                        if(snapshot.getKey().equals(nota.getUser())){
                            Glide.with(getBaseContext()).load(a.getPictureUrl()).asBitmap().fitCenter().into(new BitmapImageViewTarget(foto) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getBaseContext().getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    foto.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                            nombre.setText(a.getName());
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
        };

        noteList.setAdapter(adapter);

        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                intent = new Intent(getBaseContext(), DetailActivity.class);
                final Firebase ref = adapter.getRef(position);

                Firebase refNota = new Firebase("https://letsnote.firebaseio.com/").child("notas");
                refNota.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postsnapshot: dataSnapshot.getChildren()){
                            final Nota nota = postsnapshot.getValue(Nota.class);
                            if(postsnapshot.getRef().toString().equals(ref.toString())){
                                //Toast.makeText(ListaNotasActivity.this, postsnapshot.getRef().toString(), Toast.LENGTH_SHORT).show();
                                intent.putExtra("nota_ref", nota);
                                intent.putExtra("notaKey", postsnapshot.getKey());

                                Firebase refUsers = new Firebase("https://letsnote.firebaseio.com/").child("users");
                                refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot postsnapshot: dataSnapshot.getChildren()) {
                                            User user = postsnapshot.getValue(User.class);
                                            if(nota.getUser().equals(postsnapshot.getKey())){
                                                intent.putExtra("user_ref", user);
                                                startActivity(intent);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
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
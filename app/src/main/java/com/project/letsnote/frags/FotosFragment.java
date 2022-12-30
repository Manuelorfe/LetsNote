package com.project.letsnote.frags;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;
import com.project.letsnote.DetailActivity;
import com.project.letsnote.Nota;
import com.project.letsnote.R;
import com.project.letsnote.frags.adapters.AdapterFotos;
import com.project.letsnote.login.User;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import butterknife.Bind;

public class FotosFragment extends Fragment {

    GridView gridView;
    User user;
    Map userMapFotos;
    ArrayList<Nota> arrayNotas;
    AdapterFotos adapterFotos;

    public FotosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainFragment = inflater.inflate(R.layout.fragment_fotos, container, false);

        user = (User) this.getArguments().getSerializable("user");
        Firebase.setAndroidContext(getContext());
        gridView = (GridView)mainFragment.findViewById(R.id.gridView_fotos);
        arrayNotas = new ArrayList<>();
        adapterFotos = new AdapterFotos(getContext(), arrayNotas);

        if(!user.getFotos().isEmpty()){
            userMapFotos = user.getFotos();
            Iterator it = userMapFotos.keySet().iterator();
            while(it.hasNext()){
                Object key = it.next();
                getPhotos(key);
            }

            Log.e("array", String.valueOf(arrayNotas.size()));
            adapterFotos.clear();
            adapterFotos.addAll(arrayNotas);
            gridView.setAdapter(adapterFotos);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Nota notaExtra = adapterFotos.getItem(position);
                    final Firebase refNotas = new Firebase("https://letsnote.firebaseio.com/").child("notas");
                    refNotas.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Nota nota = dataSnapshot.getValue(Nota.class);
                            if(nota.getUrl_media().equals(notaExtra.getUrl_media())){
                                Intent i = new Intent(getContext(), DetailActivity.class);
                                i.putExtra("user_ref", user);
                                i.putExtra("notaKey",dataSnapshot.getKey());
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

        }

        return mainFragment;
    }

    public void getPhotos(final Object key){
        final Firebase refNotas = new Firebase("https://letsnote.firebaseio.com/").child("notas");
        refNotas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Nota notaSnapshot = postSnapshot.getValue(Nota.class);
                    if(postSnapshot.getKey().equals(key)){
                        arrayNotas.add(notaSnapshot);
                        Log.e("array dentro for", String.valueOf(arrayNotas.size()));
                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }

        });
    }

}

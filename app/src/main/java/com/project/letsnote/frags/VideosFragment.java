package com.project.letsnote.frags;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.project.letsnote.DetailActivity;
import com.project.letsnote.Nota;
import com.project.letsnote.R;
import com.project.letsnote.frags.adapters.AdapterFotos;
import com.project.letsnote.frags.adapters.AdapterVideos;
import com.project.letsnote.login.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class VideosFragment extends Fragment {

    ListView listView;
    User user;
    Map userMapVideos;
    ArrayList<Nota> arrayNotas;
    AdapterVideos adapterVideos;

    public VideosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainFragment = inflater.inflate(R.layout.fragment_videos, container, false);
        user = (User) this.getArguments().getSerializable("user");
        Firebase.setAndroidContext(getContext());
        Log.e("nombre", user.getName());
        listView = (ListView) mainFragment.findViewById(R.id.lvVideos);
        arrayNotas = new ArrayList<>();
        adapterVideos = new AdapterVideos(getContext(), arrayNotas);
        if(user.getVideos()!=null){
            userMapVideos = user.getVideos();
            Iterator it = userMapVideos.keySet().iterator();
            while(it.hasNext()){
                Object key = it.next();
                getVideos(key);
            }
            adapterVideos.clear();
            adapterVideos.addAll(arrayNotas);

            System.out.println("despues de addAll: "+arrayNotas.size());

            listView.setAdapter(adapterVideos);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Nota notaExtra = adapterVideos.getItem(position);
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
            //do nothing
        }


        return mainFragment;
    }

    public void getVideos(final Object key){
        final Firebase refNotas = new Firebase("https://letsnote.firebaseio.com/").child("notas");
        refNotas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Nota notaSnapshot = postSnapshot.getValue(Nota.class);
                    if(postSnapshot.getKey().equals(key)){
                        arrayNotas.add(notaSnapshot);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }

        });
    }
}

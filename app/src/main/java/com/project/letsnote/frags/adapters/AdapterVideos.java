package com.project.letsnote.frags.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.project.letsnote.Nota;
import com.project.letsnote.R;

import java.util.ArrayList;


public class AdapterVideos extends ArrayAdapter<Nota> {

    TextView tituloVideo;
    TextView descripcionVideo;
    public AdapterVideos(Context context, ArrayList<Nota> notas) {
        super(context, 0, notas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Nota nota = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_item, parent, false);
        }


        tituloVideo = (TextView)convertView.findViewById(R.id.tituloVideo);
        descripcionVideo = (TextView)convertView.findViewById(R.id.descripcionVideo);

        tituloVideo.setText(nota.getTitulo());
        descripcionVideo.setText(nota.getDescripcion());

        return convertView;
    }

}

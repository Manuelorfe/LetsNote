package com.project.letsnote.frags.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.project.letsnote.Nota;
import com.project.letsnote.R;
import java.util.ArrayList;


public class AdapterFotos extends ArrayAdapter<Nota>{

    ImageView imageView;
    TextView textView;

    public AdapterFotos(Context context, ArrayList<Nota> notas) {
        super(context, 0, notas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Nota nota = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }
        imageView = (ImageView)convertView.findViewById(R.id.gridFoto);
        Glide.with(getContext()).load(nota.getUrl_media()).asBitmap().centerCrop().into(imageView);

        return convertView;
    }
}

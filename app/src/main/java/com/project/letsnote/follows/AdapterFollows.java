package com.project.letsnote.follows;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.project.letsnote.Nota;
import com.project.letsnote.R;
import com.project.letsnote.login.User;

import java.util.ArrayList;

public class AdapterFollows extends ArrayAdapter<User>{

    ImageView fotoPerfil;
    TextView nombreUsuario;

    public AdapterFollows(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User user = getItem(position);

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_follows, parent, false);

        nombreUsuario = (TextView)convertView.findViewById(R.id.nombreUsuario);
        fotoPerfil = (ImageView)convertView.findViewById(R.id.fotoPerfil);
        Glide.with(getContext()).load(user.getPictureUrl()).asBitmap().fitCenter().into(new BitmapImageViewTarget(fotoPerfil) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                fotoPerfil.setImageDrawable(circularBitmapDrawable);
            }
        });
        nombreUsuario.setText(user.getName());

        return convertView;
    }
}

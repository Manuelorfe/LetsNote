package com.project.letsnote.comments;

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
import com.project.letsnote.R;
import com.project.letsnote.login.User;

import java.util.ArrayList;

/**
 * Created by Leosss on 07/05/2016.
 */
public class AdapterComentarios extends ArrayAdapter<Comentario> {

    ImageView userPic;
    TextView tvComentario;

    public AdapterComentarios(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Comentario comentario = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
        }

        userPic = (ImageView) convertView.findViewById(R.id.userPic);
        tvComentario = (TextView)convertView.findViewById(R.id.tvComentario);


        Glide.with(getContext()).load(comentario.getIdUserPhoto()).asBitmap().fitCenter().into(new BitmapImageViewTarget(userPic) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                userPic.setImageDrawable(circularBitmapDrawable);
            }
        });
        tvComentario.setText(comentario.getComentario().toString());

        return convertView;
    }
}

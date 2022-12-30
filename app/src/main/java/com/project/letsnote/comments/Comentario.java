package com.project.letsnote.comments;

import java.io.Serializable;

/**
 * Created by Leosss on 07/05/2016.
 */
public class Comentario implements Serializable {

    String idUserPhoto;
    String comentario;


    public Comentario() {
    }

    public String getIdUserPhoto() {
        return idUserPhoto;
    }

    public void setIdUserPhoto(String idUserPhoto) {
        this.idUserPhoto = idUserPhoto;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}

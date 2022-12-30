package com.project.letsnote;

import com.project.letsnote.comments.Comentario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Nota implements Serializable {

    String descripcion;
    String url_media;
    double latitud;
    double longitud;
    String tipo;
    String titulo;
    String user;
    Map likes = new TreeMap();

    Map<String, Comentario> comentarios = new TreeMap<>();

    //falta definir como guardar los comentarios...

    public Nota(){}

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrl_media() {
        return url_media;
    }

    public void setUrl_media(String url_media) {
        this.url_media = url_media;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Map getLikes() {
        return likes;
    }

    public void setLikes(Map likes) {
        this.likes = likes;
    }

    public Map<String, Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(Map<String, Comentario> comentarios) {
        this.comentarios = comentarios;
    }
}
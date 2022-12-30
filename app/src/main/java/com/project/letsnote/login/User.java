package com.project.letsnote.login;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class User implements Serializable{
    public String name;
    public String email;
    public String facebookID;
    public String gender;
    public String pictureUrl;
    public String location;
    public String descripcion;

    Map notas = new TreeMap();
    Map fotos = new TreeMap();
    Map videos = new TreeMap();
    Map textos = new TreeMap();

    Map seguidos = new TreeMap();
    Map seguidores = new TreeMap();

    public User() {
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Map getNotas() {
        return notas;
    }

    public void setNotas(Map notas) {
        this.notas = notas;
    }

    public Map getFotos() {
        return fotos;
    }

    public void setFotos(Map fotos) {
        this.fotos = fotos;
    }

    public Map getVideos() {
        return videos;
    }

    public void setVideos(Map videos) {
        this.videos = videos;
    }

    public Map getTextos() {
        return textos;
    }

    public void setTextos(Map textos) {
        this.textos = textos;
    }

    public Map getSeguidos() {
        return seguidos;
    }

    public void setSeguidos(Map seguidos) {
        this.seguidos = seguidos;
    }

    public Map getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(Map seguidores) {
        this.seguidores = seguidores;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", facebookID='" + facebookID + '\'' +
                ", gender='" + gender + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", location='" + location + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", notas=" + notas +
                ", fotos=" + fotos +
                ", videos=" + videos +
                ", textos=" + textos +
                ", seguidos=" + seguidos +
                ", seguidores=" + seguidores +
                '}';
    }
}
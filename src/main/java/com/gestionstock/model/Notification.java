package com.gestionstock.model;

public class Notification {
    private int idNotification;
    private String message;
    private int seuil;
    private String canal;
    private int idArticle;
    private int idUtilisateur;

    // Champs de commodité pour l'UI
    private String nomArticle;
    private String nomUtilisateur;

    public Notification() {}

    public Notification(int idNotification, String message, int seuil, String canal, int idArticle, int idUtilisateur) {
        this.idNotification = idNotification;
        this.message = message;
        this.seuil = seuil;
        this.canal = canal;
        this.idArticle = idArticle;
        this.idUtilisateur = idUtilisateur;
    }

    public Notification(String message, int seuil, String canal, int idArticle, int idUtilisateur) {
        this.message = message;
        this.seuil = seuil;
        this.canal = canal;
        this.idArticle = idArticle;
        this.idUtilisateur = idUtilisateur;
    }

    public int getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(int idNotification) {
        this.idNotification = idNotification;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSeuil() {
        return seuil;
    }

    public void setSeuil(int seuil) {
        this.seuil = seuil;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public int getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNomArticle() {
        return nomArticle;
    }

    public void setNomArticle(String nomArticle) {
        this.nomArticle = nomArticle;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }
}

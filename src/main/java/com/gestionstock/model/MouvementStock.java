package com.gestionstock.model;

import java.time.LocalDate;

public class MouvementStock {
    private int idMouvement;
    private LocalDate date;
    private TypeMouvement type;
    private int quantite;
    private int idArticle;
    private int idUtilisateur;

    // Champs de commodité pour l'UI
    private String nomArticle;
    private String nomUtilisateur;

    public MouvementStock() {}

    public MouvementStock(int idMouvement, LocalDate date, TypeMouvement type, int quantite, int idArticle, int idUtilisateur) {
        this.idMouvement = idMouvement;
        this.date = date;
        this.type = type;
        this.quantite = quantite;
        this.idArticle = idArticle;
        this.idUtilisateur = idUtilisateur;
    }

    public MouvementStock(LocalDate date, TypeMouvement type, int quantite, int idArticle, int idUtilisateur) {
        this.date = date;
        this.type = type;
        this.quantite = quantite;
        this.idArticle = idArticle;
        this.idUtilisateur = idUtilisateur;
    }

    public int getIdMouvement() {
        return idMouvement;
    }

    public void setIdMouvement(int idMouvement) {
        this.idMouvement = idMouvement;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TypeMouvement getType() {
        return type;
    }

    public void setType(TypeMouvement type) {
        this.type = type;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
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

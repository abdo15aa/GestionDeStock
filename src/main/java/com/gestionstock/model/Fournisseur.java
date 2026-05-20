package com.gestionstock.model;

public class Fournisseur {
    private int idFournisseur;
    private String nom;
    private String contact;
    private String articles;

    public Fournisseur() {}

    public Fournisseur(int idFournisseur, String nom, String contact) {
        this.idFournisseur = idFournisseur;
        this.nom = nom;
        this.contact = contact;
    }

    public Fournisseur(String nom, String contact) {
        this.nom = nom;
        this.contact = contact;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getArticles() {
        return articles;
    }

    public void setArticles(String articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return nom;
    }
}

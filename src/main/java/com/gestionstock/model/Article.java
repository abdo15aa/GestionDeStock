package com.gestionstock.model;

public class Article {
    private int idArticle;
    private String nom;
    private int quantite;
    private int seuilAlerte;

    public Article() {}

    public Article(int idArticle, String nom, int quantite, int seuilAlerte) {
        this.idArticle = idArticle;
        this.nom = nom;
        this.quantite = quantite;
        this.seuilAlerte = seuilAlerte;
    }

    public Article(String nom, int quantite, int seuilAlerte) {
        this.nom = nom;
        this.quantite = quantite;
        this.seuilAlerte = seuilAlerte;
    }

    public int getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }

    @Override
    public String toString() {
        return nom + " (Stock: " + quantite + ")";
    }
}

package com.gestionstock.model;

public class Utilisateur {
    private int idUtilisateur;
    private String nom;
    private RoleUtilisateur role;

    public Utilisateur() {}

    public Utilisateur(int idUtilisateur, String nom, RoleUtilisateur role) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.role = role;
    }

    public Utilisateur(String nom, RoleUtilisateur role) {
        this.nom = nom;
        this.role = role;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public RoleUtilisateur getRole() {
        return role;
    }

    public void setRole(RoleUtilisateur role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return nom + " (" + role + ")";
    }
}

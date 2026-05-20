package com.gestionstock.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeSchema() {
        // Vérifier si la table Utilisateur existe pour savoir s'il faut initialiser le schéma
        try (Connection conn = DBConnection.getConnection()) {
            boolean tablesExist = false;
            String catalog = conn.getCatalog();
            try (ResultSet rs = conn.getMetaData().getTables(catalog, null, "Utilisateur", null)) {
                if (rs.next()) {
                    tablesExist = true;
                }
            }
            if (!tablesExist) {
                try (ResultSet rs = conn.getMetaData().getTables(catalog, null, "utilisateur", null)) {
                    if (rs.next()) {
                        tablesExist = true;
                    }
                }
            }

            if (!tablesExist) {
                System.out.println("Schéma inexistant, initialisation des tables à partir du fichier SQL...");
                executeSQLScript(conn, "c:/GestionDeStock/gestion_stock_corrige.sql");
            } else {
                System.out.println("Base de données déjà initialisée.");
            }
        } catch (SQLException e) {
            System.err.println("Impossible de se connecter à la base de données pour l'initialisation : " + e.getMessage());
        }
    }

    private static void executeSQLScript(Connection conn, String filepath) {
        StringBuilder sql = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignorer les commentaires et les lignes vides
                if (line.trim().startsWith("--") || line.trim().startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }
                sql.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier SQL : " + e.getMessage());
            return;
        }

        // Séparer les requêtes par point-virgule et les exécuter
        String[] queries = sql.toString().split(";");
        try (Statement stmt = conn.createStatement()) {
            for (String query : queries) {
                String trimmedQuery = query.trim();
                if (!trimmedQuery.isEmpty()) {
                    // Ignorer les commandes CREATE DATABASE ou USE car la connexion JDBC gère cela
                    if (trimmedQuery.toUpperCase().startsWith("CREATE DATABASE") || trimmedQuery.toUpperCase().startsWith("USE")) {
                        continue;
                    }
                    stmt.execute(trimmedQuery);
                }
            }
            System.out.println("Schéma de la base de données initialisé avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'exécution du script SQL : " + e.getMessage());
        }
    }
}

package com.gestionstock.dao;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO implements DAO<Fournisseur> {

    @Override
    public boolean create(Fournisseur f) {
        String sql = "INSERT INTO Fournisseur (nom, contact) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, f.getNom());
            stmt.setString(2, f.getContact());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        f.setIdFournisseur(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Fournisseur find(int id) {
        String sql = "SELECT * FROM Fournisseur WHERE idFournisseur = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Fournisseur(
                            rs.getInt("idFournisseur"),
                            rs.getString("nom"),
                            rs.getString("contact")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Fournisseur f) {
        String sql = "UPDATE Fournisseur SET nom = ?, contact = ? WHERE idFournisseur = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, f.getNom());
            stmt.setString(2, f.getContact());
            stmt.setInt(3, f.getIdFournisseur());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Fournisseur WHERE idFournisseur = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Fournisseur> findAll() {
        List<Fournisseur> list = new ArrayList<>();
        String sql = "SELECT f.idFournisseur, f.nom, f.contact, " +
                     "GROUP_CONCAT(DISTINCT a.nom SEPARATOR ', ') AS articles " +
                     "FROM Fournisseur f " +
                     "LEFT JOIN FournisseurArticle fa ON f.idFournisseur = fa.idFournisseur " +
                     "LEFT JOIN Article a ON fa.idArticle = a.idArticle " +
                     "GROUP BY f.idFournisseur, f.nom, f.contact";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Fournisseur fournisseur = new Fournisseur(
                        rs.getInt("idFournisseur"),
                        rs.getString("nom"),
                        rs.getString("contact")
                );
                String articles = rs.getString("articles");
                fournisseur.setArticles(articles != null ? articles : "");
                list.add(fournisseur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Gestion de l'association N:N dans FournisseurArticle
    public boolean associateArticle(int idFournisseur, int idArticle) {
        String sql = "INSERT IGNORE INTO FournisseurArticle (idFournisseur, idArticle, dateAssociation) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFournisseur);
            stmt.setInt(2, idArticle);
            stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean dissociateArticle(int idFournisseur, int idArticle) {
        String sql = "DELETE FROM FournisseurArticle WHERE idFournisseur = ? AND idArticle = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFournisseur);
            stmt.setInt(2, idArticle);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

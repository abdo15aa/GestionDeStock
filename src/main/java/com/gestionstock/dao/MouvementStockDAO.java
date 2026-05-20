package com.gestionstock.dao;

import com.gestionstock.model.MouvementStock;
import com.gestionstock.model.TypeMouvement;
import com.gestionstock.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MouvementStockDAO implements DAO<MouvementStock> {

    @Override
    public boolean create(MouvementStock m) {
        String sql = "INSERT INTO MouvementStock (date, type, quantite, idArticle, idUtilisateur) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(m.getDate()));
            stmt.setString(2, m.getType().name());
            stmt.setInt(3, m.getQuantite());
            stmt.setInt(4, m.getIdArticle());
            stmt.setInt(5, m.getIdUtilisateur());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        m.setIdMouvement(generatedKeys.getInt(1));
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
    public MouvementStock find(int id) {
        String sql = "SELECT * FROM MouvementStock WHERE idMouvement = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MouvementStock(
                            rs.getInt("idMouvement"),
                            rs.getDate("date").toLocalDate(),
                            TypeMouvement.valueOf(rs.getString("type")),
                            rs.getInt("quantite"),
                            rs.getInt("idArticle"),
                            rs.getInt("idUtilisateur")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(MouvementStock m) {
        String sql = "UPDATE MouvementStock SET date = ?, type = ?, quantite = ?, idArticle = ?, idUtilisateur = ? WHERE idMouvement = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(m.getDate()));
            stmt.setString(2, m.getType().name());
            stmt.setInt(3, m.getQuantite());
            stmt.setInt(4, m.getIdArticle());
            stmt.setInt(5, m.getIdUtilisateur());
            stmt.setInt(6, m.getIdMouvement());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM MouvementStock WHERE idMouvement = ?";
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
    public List<MouvementStock> findAll() {
        List<MouvementStock> list = new ArrayList<>();
        String sql = "SELECT * FROM MouvementStock";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new MouvementStock(
                        rs.getInt("idMouvement"),
                        rs.getDate("date").toLocalDate(),
                        TypeMouvement.valueOf(rs.getString("type")),
                        rs.getInt("quantite"),
                        rs.getInt("idArticle"),
                        rs.getInt("idUtilisateur")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Récupérer les mouvements avec les noms d'articles et d'utilisateurs
    public List<MouvementStock> findAllWithDetails() {
        List<MouvementStock> list = new ArrayList<>();
        String sql = "SELECT m.*, a.nom AS nomArticle, u.nom AS nomUtilisateur " +
                     "FROM MouvementStock m " +
                     "JOIN Article a ON m.idArticle = a.idArticle " +
                     "LEFT JOIN Utilisateur u ON m.idUtilisateur = u.idUtilisateur " +
                     "ORDER BY m.date DESC, m.idMouvement DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MouvementStock m = new MouvementStock(
                        rs.getInt("idMouvement"),
                        rs.getDate("date").toLocalDate(),
                        TypeMouvement.valueOf(rs.getString("type")),
                        rs.getInt("quantite"),
                        rs.getInt("idArticle"),
                        rs.getInt("idUtilisateur")
                );
                m.setNomArticle(rs.getString("nomArticle"));
                m.setNomUtilisateur(rs.getString("nomUtilisateur"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

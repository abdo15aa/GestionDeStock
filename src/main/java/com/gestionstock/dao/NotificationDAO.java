package com.gestionstock.dao;

import com.gestionstock.model.Notification;
import com.gestionstock.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO implements DAO<Notification> {

    @Override
    public boolean create(Notification n) {
        String sql = "INSERT INTO Notification (message, seuil, canal, idArticle, idUtilisateur) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, n.getMessage());
            stmt.setInt(2, n.getSeuil());
            stmt.setString(3, n.getCanal());
            stmt.setInt(4, n.getIdArticle());
            // idUtilisateur peut être null
            if (n.getIdUtilisateur() > 0) {
                stmt.setInt(5, n.getIdUtilisateur());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        n.setIdNotification(generatedKeys.getInt(1));
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
    public Notification find(int id) {
        String sql = "SELECT * FROM Notification WHERE idNotification = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Notification(
                            rs.getInt("idNotification"),
                            rs.getString("message"),
                            rs.getInt("seuil"),
                            rs.getString("canal"),
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
    public boolean update(Notification n) {
        String sql = "UPDATE Notification SET message = ?, seuil = ?, canal = ?, idArticle = ?, idUtilisateur = ? WHERE idNotification = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, n.getMessage());
            stmt.setInt(2, n.getSeuil());
            stmt.setString(3, n.getCanal());
            stmt.setInt(4, n.getIdArticle());
            if (n.getIdUtilisateur() > 0) {
                stmt.setInt(5, n.getIdUtilisateur());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setInt(6, n.getIdNotification());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Notification WHERE idNotification = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Supprime toutes les notifications liées à un article.
     */
    public boolean deleteByArticleId(int idArticle) {
        String sql = "DELETE FROM Notification WHERE idArticle = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idArticle);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Indique s'il existe déjà une notification pour l'article.
     */
    public boolean existsForArticleId(int idArticle) {
        String sql = "SELECT idNotification FROM Notification WHERE idArticle = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idArticle);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Notification> findAll() {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM Notification ORDER BY idNotification DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Notification(
                        rs.getInt("idNotification"),
                        rs.getString("message"),
                        rs.getInt("seuil"),
                        rs.getString("canal"),
                        rs.getInt("idArticle"),
                        rs.getInt("idUtilisateur")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Notification> findAllWithDetails() {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.*, a.nom AS nomArticle, u.nom AS nomUtilisateur " +
                     "FROM Notification n " +
                     "JOIN Article a ON n.idArticle = a.idArticle " +
                     "LEFT JOIN Utilisateur u ON n.idUtilisateur = u.idUtilisateur " +
                     "ORDER BY n.idNotification DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Notification n = new Notification(
                        rs.getInt("idNotification"),
                        rs.getString("message"),
                        rs.getInt("seuil"),
                        rs.getString("canal"),
                        rs.getInt("idArticle"),
                        rs.getInt("idUtilisateur")
                );
                n.setNomArticle(rs.getString("nomArticle"));
                n.setNomUtilisateur(rs.getString("nomUtilisateur"));
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

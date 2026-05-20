package com.gestionstock.dao;

import com.gestionstock.model.Article;
import com.gestionstock.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO implements DAO<Article> {

    @Override
    public boolean create(Article a) {
        String sql = "INSERT INTO Article (nom, quantite, seuilAlerte) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, a.getNom());
            stmt.setInt(2, a.getQuantite());
            stmt.setInt(3, a.getSeuilAlerte());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        a.setIdArticle(generatedKeys.getInt(1));
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
    public Article find(int id) {
        String sql = "SELECT a.idArticle, a.nom, a.quantite, a.seuilAlerte, " +
                     "GROUP_CONCAT(DISTINCT f.nom SEPARATOR ', ') AS fournisseurs " +
                     "FROM Article a " +
                     "LEFT JOIN FournisseurArticle fa ON a.idArticle = fa.idArticle " +
                     "LEFT JOIN Fournisseur f ON fa.idFournisseur = f.idFournisseur " +
                     "WHERE a.idArticle = ? " +
                     "GROUP BY a.idArticle, a.nom, a.quantite, a.seuilAlerte";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Article article = new Article(
                            rs.getInt("idArticle"),
                            rs.getString("nom"),
                            rs.getInt("quantite"),
                            rs.getInt("seuilAlerte")
                    );
                    String fournisseurs = rs.getString("fournisseurs");
                    article.setFournisseurs(fournisseurs != null ? fournisseurs : "");
                    return article;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Article findByName(String nom) {
        String sql = "SELECT * FROM Article WHERE LOWER(nom) = LOWER(?) LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Article(
                            rs.getInt("idArticle"),
                            rs.getString("nom"),
                            rs.getInt("quantite"),
                            rs.getInt("seuilAlerte")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Article a) {
        String sql = "UPDATE Article SET nom = ?, quantite = ?, seuilAlerte = ? WHERE idArticle = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, a.getNom());
            stmt.setInt(2, a.getQuantite());
            stmt.setInt(3, a.getSeuilAlerte());
            stmt.setInt(4, a.getIdArticle());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String deleteNotifications = "DELETE FROM Notification WHERE idArticle = ?";
        String deleteMouvements = "DELETE FROM MouvementStock WHERE idArticle = ?";
        String deleteRelations = "DELETE FROM FournisseurArticle WHERE idArticle = ?";
        String deleteArticle = "DELETE FROM Article WHERE idArticle = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtNotif = conn.prepareStatement(deleteNotifications);
                 PreparedStatement stmtMouv = conn.prepareStatement(deleteMouvements);
                 PreparedStatement stmtRel = conn.prepareStatement(deleteRelations);
                 PreparedStatement stmtArticle = conn.prepareStatement(deleteArticle)) {

                stmtNotif.setInt(1, id);
                stmtNotif.executeUpdate();

                stmtMouv.setInt(1, id);
                stmtMouv.executeUpdate();

                stmtRel.setInt(1, id);
                stmtRel.executeUpdate();

                stmtArticle.setInt(1, id);
                int deleted = stmtArticle.executeUpdate();

                conn.commit();
                return deleted > 0;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Article> findAll() {
        List<Article> list = new ArrayList<>();
        String sql = "SELECT a.idArticle, a.nom, a.quantite, a.seuilAlerte, " +
                     "GROUP_CONCAT(DISTINCT f.nom SEPARATOR ', ') AS fournisseurs " +
                     "FROM Article a " +
                     "LEFT JOIN FournisseurArticle fa ON a.idArticle = fa.idArticle " +
                     "LEFT JOIN Fournisseur f ON fa.idFournisseur = f.idFournisseur " +
                     "GROUP BY a.idArticle, a.nom, a.quantite, a.seuilAlerte";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Article article = new Article(
                        rs.getInt("idArticle"),
                        rs.getString("nom"),
                        rs.getInt("quantite"),
                        rs.getInt("seuilAlerte")
                );
                String fournisseurs = rs.getString("fournisseurs");
                article.setFournisseurs(fournisseurs != null ? fournisseurs : "");
                list.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

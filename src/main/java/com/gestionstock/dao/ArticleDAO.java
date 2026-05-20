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
        String sql = "SELECT * FROM Article WHERE idArticle = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
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
        String sql = "DELETE FROM Article WHERE idArticle = ?";
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
    public List<Article> findAll() {
        List<Article> list = new ArrayList<>();
        String sql = "SELECT * FROM Article";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Article(
                        rs.getInt("idArticle"),
                        rs.getString("nom"),
                        rs.getInt("quantite"),
                        rs.getInt("seuilAlerte")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

package com.gestionstock.service;

import com.gestionstock.dao.ArticleDAO;
import com.gestionstock.dao.UtilisateurDAO;
import com.gestionstock.model.Article;
import com.gestionstock.model.MouvementStock;
import com.gestionstock.model.RoleUtilisateur;
import com.gestionstock.model.TypeMouvement;
import com.gestionstock.model.Utilisateur;
import com.gestionstock.util.DBConnection;
import com.gestionstock.util.DatabaseInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class StockServiceTest {

    private static boolean dbReachable = false;
    private final StockService stockService = new StockService();
    private final ArticleDAO articleDAO = new ArticleDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @BeforeAll
    public static void checkDatabase() {
        try (Connection conn = DBConnection.getConnection()) {
            dbReachable = true;
            DatabaseInitializer.initializeSchema();
        } catch (Exception e) {
            System.out.println("Base de données MySQL non disponible pour les tests unitaires. Passage des tests.");
        }
    }

    @Test
    public void testEnregistrerMouvementEntree() {
        // Exécuter le test uniquement si la base de données est accessible
        assumeTrue(dbReachable, "La base de données doit être accessible");

        // Créer un utilisateur et un article de test
        Utilisateur user = new Utilisateur("TestUser", RoleUtilisateur.EmployeLogistique);
        assertTrue(utilisateurDAO.create(user));

        Article article = new Article("ArticleTestEntree", 10, 5);
        assertTrue(articleDAO.create(article));

        // Enregistrer une entrée de 5 articles
        MouvementStock mouvement = new MouvementStock(
                LocalDate.now(),
                TypeMouvement.ENTREE,
                5,
                article.getIdArticle(),
                user.getIdUtilisateur()
        );

        assertTrue(stockService.enregistrerMouvement(mouvement));

        // Vérifier la mise à jour de la quantité
        Article updated = articleDAO.find(article.getIdArticle());
        assertNotNull(updated);
        assertEquals(15, updated.getQuantite());

        // Nettoyage
        articleDAO.delete(article.getIdArticle());
        utilisateurDAO.delete(user.getIdUtilisateur());
    }

    @Test
    public void testEnregistrerMouvementSortie() {
        assumeTrue(dbReachable, "La base de données doit être accessible");

        Utilisateur user = new Utilisateur("TestUser2", RoleUtilisateur.EmployeLogistique);
        assertTrue(utilisateurDAO.create(user));

        Article article = new Article("ArticleTestSortie", 10, 5);
        assertTrue(articleDAO.create(article));

        // Enregistrer une sortie de 3 articles
        MouvementStock mouvement = new MouvementStock(
                LocalDate.now(),
                TypeMouvement.SORTIE,
                3,
                article.getIdArticle(),
                user.getIdUtilisateur()
        );

        assertTrue(stockService.enregistrerMouvement(mouvement));

        // Vérifier la mise à jour de la quantité
        Article updated = articleDAO.find(article.getIdArticle());
        assertNotNull(updated);
        assertEquals(7, updated.getQuantite());

        // Nettoyage
        articleDAO.delete(article.getIdArticle());
        utilisateurDAO.delete(user.getIdUtilisateur());
    }

    @Test
    public void testEnregistrerMouvementSortieInsuffisante() {
        assumeTrue(dbReachable, "La base de données doit être accessible");

        Utilisateur user = new Utilisateur("TestUser3", RoleUtilisateur.EmployeLogistique);
        assertTrue(utilisateurDAO.create(user));

        Article article = new Article("ArticleTestInsuffisant", 2, 1);
        assertTrue(articleDAO.create(article));

        // Tenter de sortir 5 articles (alors qu'on n'en a que 2)
        MouvementStock mouvement = new MouvementStock(
                LocalDate.now(),
                TypeMouvement.SORTIE,
                5,
                article.getIdArticle(),
                user.getIdUtilisateur()
        );

        assertFalse(stockService.enregistrerMouvement(mouvement));

        // Vérifier que la quantité n'a pas changé
        Article updated = articleDAO.find(article.getIdArticle());
        assertNotNull(updated);
        assertEquals(2, updated.getQuantite());

        // Nettoyage
        articleDAO.delete(article.getIdArticle());
        utilisateurDAO.delete(user.getIdUtilisateur());
    }
}

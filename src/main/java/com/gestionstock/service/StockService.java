package com.gestionstock.service;

import com.gestionstock.dao.*;
import com.gestionstock.model.*;

import java.time.LocalDate;
import java.util.List;

public class StockService {
    private final ArticleDAO articleDAO = new ArticleDAO();
    private final MouvementStockDAO mouvementDAO = new MouvementStockDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    /**
     * Enregistre un mouvement de stock, met à jour la quantité de l'article, et lève une alerte si nécessaire.
     * @return true si le mouvement a été effectué avec succès, false sinon (ex: stock insuffisant).
     */
    public synchronized boolean enregistrerMouvement(MouvementStock mouvement) {
        Article article = articleDAO.find(mouvement.getIdArticle());
        if (article == null) {
            System.err.println("Article introuvable.");
            return false;
        }

        int nouvelleQuantite = article.getQuantite();
        if (mouvement.getType() == TypeMouvement.ENTREE) {
            nouvelleQuantite += mouvement.getQuantite();
        } else if (mouvement.getType() == TypeMouvement.SORTIE) {
            if (article.getQuantite() < mouvement.getQuantite()) {
                System.err.println("Erreur: Stock insuffisant pour la sortie.");
                return false;
            }
            nouvelleQuantite -= mouvement.getQuantite();
        }

        // 1. Mettre à jour l'article en BDD
        article.setQuantite(nouvelleQuantite);
        if (!articleDAO.update(article)) {
            System.err.println("Échec de la mise à jour de la quantité de l'article.");
            return false;
        }

        // 2. Enregistrer le mouvement de stock
        mouvement.setDate(LocalDate.now());
        if (!mouvementDAO.create(mouvement)) {
            System.err.println("Échec de l'enregistrement du mouvement.");
            return false;
        }

        // 3. Vérifier le seuil d'alerte et mettre à jour les notifications
        if (nouvelleQuantite <= article.getSeuilAlerte()) {
            // Supprimer d'éventuelles anciennes notifications puis en créer une nouvelle
            notificationDAO.deleteByArticleId(article.getIdArticle());
            String message = String.format("Stock critique pour l'article '%s' : %d restants (Seuil d'alerte : %d)",
                article.getNom(), nouvelleQuantite, article.getSeuilAlerte());
            Notification notif = new Notification(
                message,
                article.getSeuilAlerte(),
                "Interface/Email",
                article.getIdArticle(),
                mouvement.getIdUtilisateur()
            );
            notificationDAO.create(notif);
            System.out.println("Notification d'alerte créée : " + message);
        } else {
            // Si le stock est redevenu supérieur au seuil, supprimer automatiquement l'alerte
            notificationDAO.deleteByArticleId(article.getIdArticle());
        }

        return true;
    }

    public List<Article> getArticles() {
        return articleDAO.findAll();
    }

    public List<MouvementStock> getMouvements() {
        return mouvementDAO.findAllWithDetails();
    }

    public List<Notification> getNotifications() {
        return notificationDAO.findAllWithDetails();
    }

    /**
     * Génère des notifications pour les articles déjà en-dessous du seuil au démarrage.
     * N'ajoute une notification que s'il n'en existe pas encore pour l'article.
     */
    public void generateNotificationsForExistingLowStock() {
        List<Article> articles = articleDAO.findAll();
        for (Article article : articles) {
            if (article.getQuantite() <= article.getSeuilAlerte()) {
                if (!notificationDAO.existsForArticleId(article.getIdArticle())) {
                    String message = String.format("Stock critique pour l'article '%s' : %d restants (Seuil d'alerte : %d)",
                            article.getNom(), article.getQuantite(), article.getSeuilAlerte());
                    Notification notif = new Notification(
                            message,
                            article.getSeuilAlerte(),
                            "Interface/Email",
                            article.getIdArticle(),
                            0
                    );
                    notificationDAO.create(notif);
                    System.out.println("Notification d'alerte (démarrage) créée : " + message);
                }
            }
        }
    }
}

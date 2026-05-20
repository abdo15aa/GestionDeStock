package com.gestionstock.controller;

import com.gestionstock.dao.*;
import com.gestionstock.model.*;
import com.gestionstock.service.StockService;
import com.gestionstock.util.DatabaseInitializer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ListChangeListener;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

public class MainController {

    // SIDEBAR BUTTONS
    @FXML private Button btnDashboard;
    @FXML private Button btnArticles;
    @FXML private Button btnFournisseurs;
    @FXML private Button btnMouvements;
    @FXML private Button btnNotifications;

    // VIEWS
    @FXML private VBox viewDashboard;
    @FXML private HBox viewArticles;
    @FXML private HBox viewFournisseurs;
    @FXML private VBox viewMouvements;
    @FXML private VBox viewNotifications;

    // DASHBOARD CARDS
    @FXML private Label lblTotalArticles;
    @FXML private Label lblTotalFournisseurs;
    @FXML private Label lblTotalAlertes;

    // TABLES & COLUMNS
    @FXML private TableView<MouvementStock> tblDashboardMouvements;
    @FXML private TableColumn<MouvementStock, Integer> colDashMouvId;
    @FXML private TableColumn<MouvementStock, LocalDate> colDashMouvDate;
    @FXML private TableColumn<MouvementStock, String> colDashMouvArticle;
    @FXML private TableColumn<MouvementStock, String> colDashMouvType;
    @FXML private TableColumn<MouvementStock, Integer> colDashMouvQte;
    @FXML private TableColumn<MouvementStock, String> colDashMouvUser;

    @FXML private TableView<Article> tblArticles;
    @FXML private TableColumn<Article, Integer> colArtId;
    @FXML private TableColumn<Article, String> colArtNom;
    @FXML private TableColumn<Article, Integer> colArtQte;
    @FXML private TableColumn<Article, Integer> colArtSeuil;

    @FXML private TableView<Fournisseur> tblFournisseurs;
    @FXML private TableColumn<Fournisseur, Integer> colFournId;
    @FXML private TableColumn<Fournisseur, String> colFournNom;
    @FXML private TableColumn<Fournisseur, String> colFournContact;

    @FXML private TableView<MouvementStock> tblMouvements;
    @FXML private TableColumn<MouvementStock, Integer> colMouvId;
    @FXML private TableColumn<MouvementStock, LocalDate> colMouvDate;
    @FXML private TableColumn<MouvementStock, String> colMouvArticle;
    @FXML private TableColumn<MouvementStock, String> colMouvType;
    @FXML private TableColumn<MouvementStock, Integer> colMouvQte;
    @FXML private TableColumn<MouvementStock, String> colMouvUser;

    @FXML private TableView<Notification> tblNotifications;
    @FXML private TableColumn<Notification, Integer> colNotifId;
    @FXML private TableColumn<Notification, String> colNotifMessage;
    @FXML private TableColumn<Notification, Integer> colNotifSeuil;
    @FXML private TableColumn<Notification, String> colNotifCanal;
    @FXML private TableColumn<Notification, String> colNotifArticle;
    @FXML private Label notifBadge;

    // FORM FIELDS: ARTICLES
    @FXML private TextField txtArtNom;
    @FXML private TextField txtArtQte;
    @FXML private TextField txtArtSeuil;

    // FORM FIELDS: FOURNISSEURS
    @FXML private TextField txtFournNom;
    @FXML private TextField txtFournContact;

    // FORM FIELDS: MOUVEMENTS
    @FXML private ComboBox<Article> cmbMouvArticle;
    @FXML private ComboBox<Utilisateur> cmbMouvUtilisateur;
    @FXML private ToggleGroup toggleTypeMouvement;
    @FXML private RadioButton radEntree;
    @FXML private RadioButton radSortie;
    @FXML private TextField txtMouvQte;

    // SERVICES & DAOs
    private StockService stockService;
    private ArticleDAO articleDAO;
    private FournisseurDAO fournisseurDAO;
    private UtilisateurDAO utilisateurDAO;
    private NotificationDAO notificationDAO;

    // DATA LISTS
    private ObservableList<Article> articleList = FXCollections.observableArrayList();
    private ObservableList<Fournisseur> fournisseurList = FXCollections.observableArrayList();
    private ObservableList<MouvementStock> mouvementList = FXCollections.observableArrayList();
    private ObservableList<Notification> notificationList = FXCollections.observableArrayList();
    private ObservableList<Utilisateur> utilisateurList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialiser le schéma de base de données si nécessaire
        DatabaseInitializer.initializeSchema();

        // Initialiser les services
        stockService = new StockService();
        articleDAO = new ArticleDAO();
        fournisseurDAO = new FournisseurDAO();
        utilisateurDAO = new UtilisateurDAO();
        notificationDAO = new NotificationDAO();

        // Associer les colonnes des tables aux modèles
        setupTableColumns();

        // Générer les notifications pour les articles déjà en-dessous du seuil
        stockService.generateNotificationsForExistingLowStock();

        // Charger les données de la base de données
        loadData();

        // Initialiser le badge de notifications
        setupNotificationBadge();

        // Définir le bouton actif et la vue active (Dashboard par défaut)
        setActiveButton(btnDashboard);
        showView(viewDashboard);
    }

    private void setupTableColumns() {
        // Table Dashboard Mouvements
        colDashMouvId.setCellValueFactory(new PropertyValueFactory<>("idMouvement"));
        colDashMouvDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDashMouvArticle.setCellValueFactory(new PropertyValueFactory<>("nomArticle"));
        colDashMouvType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDashMouvQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colDashMouvUser.setCellValueFactory(new PropertyValueFactory<>("nomUtilisateur"));

        // Table Articles
        colArtId.setCellValueFactory(new PropertyValueFactory<>("idArticle"));
        colArtNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colArtQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colArtSeuil.setCellValueFactory(new PropertyValueFactory<>("seuilAlerte"));

        // Table Fournisseurs
        colFournId.setCellValueFactory(new PropertyValueFactory<>("idFournisseur"));
        colFournNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colFournContact.setCellValueFactory(new PropertyValueFactory<>("contact"));

        // Table Mouvements
        colMouvId.setCellValueFactory(new PropertyValueFactory<>("idMouvement"));
        colMouvDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colMouvArticle.setCellValueFactory(new PropertyValueFactory<>("nomArticle"));
        colMouvType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colMouvQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colMouvUser.setCellValueFactory(new PropertyValueFactory<>("nomUtilisateur"));

        // Table Notifications
        colNotifId.setCellValueFactory(new PropertyValueFactory<>("idNotification"));
        colNotifMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colNotifSeuil.setCellValueFactory(new PropertyValueFactory<>("seuil"));
        colNotifCanal.setCellValueFactory(new PropertyValueFactory<>("canal"));
        colNotifArticle.setCellValueFactory(new PropertyValueFactory<>("nomArticle"));
    }

    private void loadData() {
        try {
            // Recharger les listes depuis les DAOs / Services
            articleList.setAll(articleDAO.findAll());
            fournisseurList.setAll(fournisseurDAO.findAll());
            mouvementList.setAll(stockService.getMouvements());
            notificationList.setAll(stockService.getNotifications());
            utilisateurList.setAll(utilisateurDAO.findAll());

            // Affecter les listes aux tables
            tblArticles.setItems(articleList);
            tblFournisseurs.setItems(fournisseurList);
            tblMouvements.setItems(mouvementList);
            tblNotifications.setItems(notificationList);
            
            // Dashboard
            tblDashboardMouvements.setItems(FXCollections.observableArrayList(
                    mouvementList.subList(0, Math.min(5, mouvementList.size()))
            ));

            // Comboboxes du formulaire de mouvement
            cmbMouvArticle.setItems(articleList);
            cmbMouvUtilisateur.setItems(utilisateurList);

            // Mettre à jour les statistiques sur le dashboard
            updateDashboardStats();

        } catch (Exception e) {
            showError("Erreur de chargement", "Impossible de récupérer les données depuis la base de données. Vérifiez votre connexion MySQL.");
            e.printStackTrace();
        }
    }

    private void updateDashboardStats() {
        lblTotalArticles.setText(String.valueOf(articleList.size()));
        lblTotalFournisseurs.setText(String.valueOf(fournisseurList.size()));
        
        // Compter les alertes
        lblTotalAlertes.setText(String.valueOf(notificationList.size()));
    }

    private void setupNotificationBadge() {
        updateNotificationBadge();
        notificationList.addListener((ListChangeListener<Notification>) change -> updateNotificationBadge());
    }

    private void updateNotificationBadge() {
        int n = notificationList.size();
        if (notifBadge == null) return;
        if (n > 0) {
            notifBadge.setText(String.valueOf(n));
            notifBadge.setVisible(true);
        } else {
            notifBadge.setVisible(false);
        }
    }

    // ACTIONS SIDEBAR (COMMUTATION DE VUES)
    @FXML
    void showDashboardView(ActionEvent event) {
        setActiveButton(btnDashboard);
        showView(viewDashboard);
        loadData(); // Recharger pour avoir les stats à jour
    }

    @FXML
    void showArticlesView(ActionEvent event) {
        setActiveButton(btnArticles);
        showView(viewArticles);
        clearArticleForm();
    }

    @FXML
    void showFournisseursView(ActionEvent event) {
        setActiveButton(btnFournisseurs);
        showView(viewFournisseurs);
        clearFournisseurForm();
    }

    @FXML
    void showMouvementsView(ActionEvent event) {
        setActiveButton(btnMouvements);
        showView(viewMouvements);
    }

    @FXML
    void showNotificationsView(ActionEvent event) {
        setActiveButton(btnNotifications);
        showView(viewNotifications);
    }

    @FXML
    void deleteNotification(ActionEvent event) {
        Notification selected = tblNotifications.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner une alerte à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer cette alerte ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Supprimer l'alerte");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (notificationDAO.delete(selected.getIdNotification())) {
                    notificationList.remove(selected);
                    updateDashboardStats();
                    showInfo("Alerte supprimée", "L'alerte a bien été supprimée.");
                } else {
                    showError("Erreur", "Impossible de supprimer l'alerte. Réessayez.");
                }
            }
        });
    }

    private void showView(javafx.scene.Node viewToShow) {
        viewDashboard.setVisible(false);
        viewArticles.setVisible(false);
        viewFournisseurs.setVisible(false);
        viewMouvements.setVisible(false);
        viewNotifications.setVisible(false);

        viewToShow.setVisible(true);
    }

    private void setActiveButton(Button activeBtn) {
        btnDashboard.getStyleClass().remove("sidebar-btn-active");
        btnArticles.getStyleClass().remove("sidebar-btn-active");
        btnFournisseurs.getStyleClass().remove("sidebar-btn-active");
        btnMouvements.getStyleClass().remove("sidebar-btn-active");
        btnNotifications.getStyleClass().remove("sidebar-btn-active");

        activeBtn.getStyleClass().add("sidebar-btn-active");
    }

    // CRUD ARTICLES
    @FXML
    void addArticle(ActionEvent event) {
        if (validateArticleFields()) {
            String nom = txtArtNom.getText();
            int qte = Integer.parseInt(txtArtQte.getText());
            int seuil = Integer.parseInt(txtArtSeuil.getText());

            Article a = new Article(nom, qte, seuil);
            if (articleDAO.create(a)) {
                showInfo("Succès", "L'article a été ajouté avec succès.");
                loadData();
                clearArticleForm();
            } else {
                showError("Erreur", "L'ajout de l'article a échoué.");
            }
        }
    }

    @FXML
    void updateArticle(ActionEvent event) {
        Article selected = tblArticles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un article dans la table.");
            return;
        }

        if (validateArticleFields()) {
            selected.setNom(txtArtNom.getText());
            selected.setQuantite(Integer.parseInt(txtArtQte.getText()));
            selected.setSeuilAlerte(Integer.parseInt(txtArtSeuil.getText()));

            if (articleDAO.update(selected)) {
                showInfo("Succès", "L'article a été modifié avec succès.");
                loadData();
                clearArticleForm();
            } else {
                showError("Erreur", "La modification de l'article a échoué.");
            }
        }
    }

    @FXML
    void deleteArticle(ActionEvent event) {
        Article selected = tblArticles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un article à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cet article ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (articleDAO.delete(selected.getIdArticle())) {
                    showInfo("Succès", "L'article a été supprimé.");
                    loadData();
                    clearArticleForm();
                } else {
                    showError("Erreur", "Impossible de supprimer l'article (il est probablement lié à un mouvement ou un fournisseur).");
                }
            }
        });
    }

    @FXML
    void onArticleSelected(MouseEvent event) {
        Article selected = tblArticles.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtArtNom.setText(selected.getNom());
            txtArtQte.setText(String.valueOf(selected.getQuantite()));
            txtArtSeuil.setText(String.valueOf(selected.getSeuilAlerte()));
            txtArtQte.setDisable(true); // Désactivé en modification pour passer par les mouvements de stock
        }
    }

    @FXML
    void clearArticleForm() {
        txtArtNom.clear();
        txtArtQte.clear();
        txtArtSeuil.clear();
        txtArtQte.setDisable(false);
        tblArticles.getSelectionModel().clearSelection();
    }

    private boolean validateArticleFields() {
        if (txtArtNom.getText().trim().isEmpty() || txtArtQte.getText().trim().isEmpty() || txtArtSeuil.getText().trim().isEmpty()) {
            showWarning("Champs vides", "Veuillez remplir tous les champs de l'article.");
            return false;
        }
        try {
            int qte = Integer.parseInt(txtArtQte.getText().trim());
            int seuil = Integer.parseInt(txtArtSeuil.getText().trim());
            if (qte < 0 || seuil < 0) {
                showWarning("Valeurs incorrectes", "La quantité et le seuil doivent être positifs.");
                return false;
            }
        } catch (NumberFormatException e) {
            showWarning("Valeurs incorrectes", "La quantité et le seuil doivent être des nombres entiers.");
            return false;
        }
        return true;
    }

    // CRUD FOURNISSEURS
    @FXML
    void addFournisseur(ActionEvent event) {
        String nom = txtFournNom.getText().trim();
        String contact = txtFournContact.getText().trim();

        if (nom.isEmpty()) {
            showWarning("Champ vide", "Veuillez renseigner le nom du fournisseur.");
            return;
        }

        Fournisseur f = new Fournisseur(nom, contact);
        if (fournisseurDAO.create(f)) {
            showInfo("Succès", "Fournisseur ajouté.");
            loadData();
            clearFournisseurForm();
        } else {
            showError("Erreur", "L'ajout a échoué.");
        }
    }

    @FXML
    void updateFournisseur(ActionEvent event) {
        Fournisseur selected = tblFournisseurs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un fournisseur.");
            return;
        }

        String nom = txtFournNom.getText().trim();
        String contact = txtFournContact.getText().trim();

        if (nom.isEmpty()) {
            showWarning("Champ vide", "Le nom ne peut pas être vide.");
            return;
        }

        selected.setNom(nom);
        selected.setContact(contact);

        if (fournisseurDAO.update(selected)) {
            showInfo("Succès", "Fournisseur modifié.");
            loadData();
            clearFournisseurForm();
        } else {
            showError("Erreur", "La modification a échoué.");
        }
    }

    @FXML
    void deleteFournisseur(ActionEvent event) {
        Fournisseur selected = tblFournisseurs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un fournisseur.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce fournisseur ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (fournisseurDAO.delete(selected.getIdFournisseur())) {
                    showInfo("Succès", "Fournisseur supprimé.");
                    loadData();
                    clearFournisseurForm();
                } else {
                    showError("Erreur", "La suppression a échoué.");
                }
            }
        });
    }

    @FXML
    void onFournisseurSelected(MouseEvent event) {
        Fournisseur selected = tblFournisseurs.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtFournNom.setText(selected.getNom());
            txtFournContact.setText(selected.getContact());
        }
    }

    @FXML
    void clearFournisseurForm() {
        txtFournNom.clear();
        txtFournContact.clear();
        tblFournisseurs.getSelectionModel().clearSelection();
    }

    // MOUVEMENTS DE STOCK
    @FXML
    void saveMouvement(ActionEvent event) {
        Article article = cmbMouvArticle.getValue();
        Utilisateur utilisateur = cmbMouvUtilisateur.getValue();
        String qteStr = txtMouvQte.getText().trim();

        if (article == null || utilisateur == null || qteStr.isEmpty()) {
            showWarning("Champs incomplets", "Veuillez sélectionner un article, un opérateur et saisir une quantité.");
            return;
        }

        int qte;
        try {
            qte = Integer.parseInt(qteStr);
            if (qte <= 0) {
                showWarning("Quantité incorrecte", "La quantité doit être supérieure à zéro.");
                return;
            }
        } catch (NumberFormatException e) {
            showWarning("Quantité incorrecte", "La quantité doit être un nombre entier.");
            return;
        }

        TypeMouvement type = radEntree.isSelected() ? TypeMouvement.ENTREE : TypeMouvement.SORTIE;

        // Créer l'objet mouvement
        MouvementStock m = new MouvementStock(LocalDate.now(), type, qte, article.getIdArticle(), utilisateur.getIdUtilisateur());

        // Enregistrer via le service
        if (stockService.enregistrerMouvement(m)) {
            showInfo("Succès", "Le mouvement de stock a été enregistré avec succès.");
            loadData();
            txtMouvQte.clear();
        } else {
            showError("Échec", "Stock insuffisant pour effectuer cette sortie.");
        }
    }

    // ALERTS UTILITAIRES
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

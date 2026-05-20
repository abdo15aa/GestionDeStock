
CREATE DATABASE IF NOT EXISTS gestion_stock;
USE gestion_stock;

-- TABLE Utilisateur
CREATE TABLE Utilisateur(
    idUtilisateur INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    role ENUM('EmployeLogistique','ResponsableLogistique') NOT NULL
);

-- TABLE Fournisseur
CREATE TABLE Fournisseur(
    idFournisseur INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    contact VARCHAR(100)
);

-- TABLE Article
CREATE TABLE Article(
    idArticle INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    quantite INT DEFAULT 0,
    seuilAlerte INT
);

-- TABLE FournisseurArticle (relation N:N)
CREATE TABLE FournisseurArticle(
    idFournisseur INT,
    idArticle INT,
    dateAssociation DATE,

    PRIMARY KEY(idFournisseur,idArticle),

    FOREIGN KEY(idFournisseur)
    REFERENCES Fournisseur(idFournisseur)
    ON DELETE CASCADE,

    FOREIGN KEY(idArticle)
    REFERENCES Article(idArticle)
    ON DELETE CASCADE
);

-- TABLE MouvementStock
CREATE TABLE MouvementStock(
    idMouvement INT AUTO_INCREMENT PRIMARY KEY,

    date DATE,

    type ENUM('ENTREE','SORTIE'),

    quantite INT,

    idArticle INT,
    idUtilisateur INT,

    FOREIGN KEY(idArticle)
    REFERENCES Article(idArticle),

    FOREIGN KEY(idUtilisateur)
    REFERENCES Utilisateur(idUtilisateur)
);

-- TABLE Notification
CREATE TABLE Notification(
    idNotification INT AUTO_INCREMENT PRIMARY KEY,

    message TEXT,

    seuil INT,

    canal VARCHAR(50),

    idArticle INT,
    idUtilisateur INT,

    FOREIGN KEY(idArticle)
    REFERENCES Article(idArticle),

    FOREIGN KEY(idUtilisateur)
    REFERENCES Utilisateur(idUtilisateur)
);

-- Donnees de test

INSERT INTO Utilisateur(nom,role)
VALUES
('Ahmed','ResponsableLogistique'),
('Ali','EmployeLogistique');

INSERT INTO Fournisseur(nom,contact)
VALUES
('Dell','dell@gmail.com'),
('HP','hp@gmail.com');

INSERT INTO Article(nom,quantite,seuilAlerte)
VALUES
('Clavier',50,10),
('Souris',30,5);

INSERT INTO FournisseurArticle
VALUES
(1,1,'2026-05-18'),
(2,2,'2026-05-18');

INSERT INTO MouvementStock
(date,type,quantite,idArticle,idUtilisateur)
VALUES
('2026-05-18','ENTREE',20,1,2);

INSERT INTO Notification
(message,seuil,canal,idArticle,idUtilisateur)
VALUES
('Stock critique clavier',10,'Email',1,1);

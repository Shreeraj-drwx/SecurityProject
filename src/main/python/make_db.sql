CREATE DATABASE IF NOT EXISTS StockManager;

CREATE TABLE IF NOT EXISTS `asset_prices` (
  `symbol` varchar(6) NOT NULL COMMENT 'Stock symbol',
  `market_price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS`assets` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `volume` int DEFAULT NULL,
  `date` int DEFAULT NULL,
  `price` double DEFAULT NULL,
  `userID` int DEFAULT NULL,
  `broker` varchar(45) DEFAULT NULL,
  `symbol` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `assets_users_fk` (`userID`),
  CONSTRAINT `assets_users_fk` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS`users` (
  `userID` int NOT NULL AUTO_INCREMENT,
  `email` varchar(45) NOT NULL,
  `password` varchar(100) NOT NULL,
  `firstName` varchar(45) NOT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `upload_history` (
     `date` date DEFAULT NULL,
      `filename` varchar(50) DEFAULT NULL,
    `userID` int NOT NULL,
    KEY `fk_user` (`userID`),
    CONSTRAINT `fk_user` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;





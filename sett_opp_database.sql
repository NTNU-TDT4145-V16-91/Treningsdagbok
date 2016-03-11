-- Gruppe 91 - Sondre Rodahl, Thorben Dahl og Vilde Mariussen

-- MySQL dump 10.13  Distrib 5.5.47, for debian-linux-gnu (x86_64)
--
-- Host: mysql.stud.ntnu.no    Database: twdahl_tdt4145prj
-- ------------------------------------------------------
-- Server version	5.5.47-0ubuntu0.12.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `øvelse`
--

DROP TABLE IF EXISTS `øvelse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `øvelse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `navn` varchar(30) NOT NULL,
  `beskrivelse` text,
  `repetisjoner` smallint(6) DEFAULT NULL,
  `sett` smallint(6) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `utholdenhet_default_distanse` decimal(5,2) DEFAULT NULL,
  `utholdenhet_default_varighet` int(11) DEFAULT NULL,
  `belastning` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `øvelse`
--

LOCK TABLES `øvelse` WRITE;
/*!40000 ALTER TABLE `øvelse` DISABLE KEYS */;
/*!40000 ALTER TABLE `øvelse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `øvelse_i_gruppe`
--

DROP TABLE IF EXISTS `øvelse_i_gruppe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `øvelse_i_gruppe` (
  `øvelse_id` int(11) NOT NULL,
  `gruppe_id` int(11) NOT NULL,
  PRIMARY KEY (`øvelse_id`,`gruppe_id`),
  KEY `gruppe_id` (`gruppe_id`),
  CONSTRAINT `øvelse_i_gruppe_ibfk_1` FOREIGN KEY (`øvelse_id`) REFERENCES `øvelse` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `øvelse_i_gruppe_ibfk_2` FOREIGN KEY (`gruppe_id`) REFERENCES `gruppe` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `øvelse_i_gruppe`
--

LOCK TABLES `øvelse_i_gruppe` WRITE;
/*!40000 ALTER TABLE `øvelse_i_gruppe` DISABLE KEYS */;
/*!40000 ALTER TABLE `øvelse_i_gruppe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `øvelse_i_mal`
--

DROP TABLE IF EXISTS `øvelse_i_mal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `øvelse_i_mal` (
  `mal_navn` varchar(30) NOT NULL,
  `øvelse_id` int(11) NOT NULL,
  `plassering` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`mal_navn`,`øvelse_id`),
  KEY `øvelse_id` (`øvelse_id`),
  CONSTRAINT `øvelse_i_mal_ibfk_1` FOREIGN KEY (`mal_navn`) REFERENCES `mal` (`navn`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `øvelse_i_mal_ibfk_2` FOREIGN KEY (`øvelse_id`) REFERENCES `øvelse` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `øvelse_i_mal`
--

LOCK TABLES `øvelse_i_mal` WRITE;
/*!40000 ALTER TABLE `øvelse_i_mal` DISABLE KEYS */;
/*!40000 ALTER TABLE `øvelse_i_mal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `øvelse_i_trening`
--

DROP TABLE IF EXISTS `øvelse_i_trening`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `øvelse_i_trening` (
  `trening_id` int(11) NOT NULL,
  `øvelse_id` int(11) NOT NULL,
  `plassering` int(11) DEFAULT NULL,
  PRIMARY KEY (`trening_id`,`øvelse_id`),
  KEY `øvelse_id` (`øvelse_id`),
  CONSTRAINT `øvelse_i_trening_ibfk_1` FOREIGN KEY (`trening_id`) REFERENCES `treningsøkt` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `øvelse_i_trening_ibfk_2` FOREIGN KEY (`øvelse_id`) REFERENCES `øvelse` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `øvelse_i_trening`
--

LOCK TABLES `øvelse_i_trening` WRITE;
/*!40000 ALTER TABLE `øvelse_i_trening` DISABLE KEYS */;
/*!40000 ALTER TABLE `øvelse_i_trening` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `erstatt_med`
--

DROP TABLE IF EXISTS `erstatt_med`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `erstatt_med` (
  `øvelse_id` int(11) NOT NULL,
  `erstatt_med_id` int(11) NOT NULL,
  PRIMARY KEY (`øvelse_id`,`erstatt_med_id`),
  KEY `erstatt_med_id` (`erstatt_med_id`),
  CONSTRAINT `erstatt_med_ibfk_1` FOREIGN KEY (`øvelse_id`) REFERENCES `øvelse` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `erstatt_med_ibfk_2` FOREIGN KEY (`erstatt_med_id`) REFERENCES `øvelse` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `erstatt_med`
--

LOCK TABLES `erstatt_med` WRITE;
/*!40000 ALTER TABLE `erstatt_med` DISABLE KEYS */;
/*!40000 ALTER TABLE `erstatt_med` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gruppe`
--

DROP TABLE IF EXISTS `gruppe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gruppe` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `navn` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gruppe`
--

LOCK TABLES `gruppe` WRITE;
/*!40000 ALTER TABLE `gruppe` DISABLE KEYS */;
/*!40000 ALTER TABLE `gruppe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mål`
--

DROP TABLE IF EXISTS `mål`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mål` (
  `øvelseID` int(11) NOT NULL,
  `opprettet_tid` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `belastning` int(11) DEFAULT NULL,
  `repetisjoner` smallint(6) DEFAULT NULL,
  `sett` smallint(6) DEFAULT NULL,
  `utholdenhet_distanse` decimal(5,2) DEFAULT NULL,
  `utholdenhet_varighet` int(11) DEFAULT NULL,
  `oppnådd_tid` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`øvelseID`,`opprettet_tid`),
  CONSTRAINT `mål_ibfk_1` FOREIGN KEY (`øvelseID`) REFERENCES `øvelse` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mål`
--

LOCK TABLES `mål` WRITE;
/*!40000 ALTER TABLE `mål` DISABLE KEYS */;
/*!40000 ALTER TABLE `mål` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mal`
--

DROP TABLE IF EXISTS `mal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mal` (
  `navn` varchar(30) NOT NULL DEFAULT '',
  PRIMARY KEY (`navn`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mal`
--

LOCK TABLES `mal` WRITE;
/*!40000 ALTER TABLE `mal` DISABLE KEYS */;
/*!40000 ALTER TABLE `mal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resultat`
--

DROP TABLE IF EXISTS `resultat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resultat` (
  `trening_id` int(11) NOT NULL,
  `øvelse_id` int(11) NOT NULL,
  `belastning` int(11) DEFAULT NULL,
  `repetisjoner` smallint(6) DEFAULT NULL,
  `sett` smallint(6) DEFAULT NULL,
  `utholdenhet_distanse` decimal(5,2) DEFAULT NULL,
  `utholdenhet_varighet` int(11) DEFAULT NULL,
  PRIMARY KEY (`trening_id`,`øvelse_id`),
  KEY `resultat_ibfk_2` (`øvelse_id`),
  CONSTRAINT `resultat_ibfk_2` FOREIGN KEY (`øvelse_id`) REFERENCES `øvelse` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `resultat_ibfk_1` FOREIGN KEY (`trening_id`) REFERENCES `treningsøkt` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resultat`
--

LOCK TABLES `resultat` WRITE;
/*!40000 ALTER TABLE `resultat` DISABLE KEYS */;
/*!40000 ALTER TABLE `resultat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `treningsøkt`
--

DROP TABLE IF EXISTS `treningsøkt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treningsøkt` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tidspunkt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `varighet` smallint(5) unsigned DEFAULT NULL,
  `personlig_form` tinyint(4) DEFAULT NULL,
  `prestasjon` tinyint(4) DEFAULT NULL,
  `notat` text,
  `innendørs` tinyint(1) DEFAULT '0',
  `luftscore` tinyint(4) DEFAULT NULL,
  `antall_tilskuere` mediumint(8) unsigned DEFAULT '0',
  `ute_værtype` varchar(30) DEFAULT NULL,
  `ute_temperatur` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `treningsøkt`
--

LOCK TABLES `treningsøkt` WRITE;
/*!40000 ALTER TABLE `treningsøkt` DISABLE KEYS */;
/*!40000 ALTER TABLE `treningsøkt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `undergruppe`
--

DROP TABLE IF EXISTS `undergruppe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `undergruppe` (
  `subgruppe_id` int(11) NOT NULL,
  `supergruppe_id` int(11) NOT NULL,
  PRIMARY KEY (`subgruppe_id`,`supergruppe_id`),
  KEY `supergruppe_id` (`supergruppe_id`),
  CONSTRAINT `undergruppe_ibfk_1` FOREIGN KEY (`subgruppe_id`) REFERENCES `gruppe` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `undergruppe_ibfk_2` FOREIGN KEY (`supergruppe_id`) REFERENCES `gruppe` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `undergruppe`
--

LOCK TABLES `undergruppe` WRITE;
/*!40000 ALTER TABLE `undergruppe` DISABLE KEYS */;
/*!40000 ALTER TABLE `undergruppe` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-03-01 13:58:49

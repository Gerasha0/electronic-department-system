-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: localhost    Database: electronic_department
-- ------------------------------------------------------
-- Server version	8.0.43-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `archived_grades`
--

DROP TABLE IF EXISTS `archived_grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `archived_grades` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `archive_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `archived_at` datetime(6) NOT NULL,
  `archived_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `grade_value` int DEFAULT NULL,
  `group_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `original_created_at` datetime(6) DEFAULT NULL,
  `original_grade_id` bigint NOT NULL,
  `original_group_id` bigint DEFAULT NULL,
  `original_student_id` bigint NOT NULL,
  `original_subject_id` bigint DEFAULT NULL,
  `original_updated_at` datetime(6) DEFAULT NULL,
  `student_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subject_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archived_grades`
--

LOCK TABLES `archived_grades` WRITE;
/*!40000 ALTER TABLE `archived_grades` DISABLE KEYS */;
INSERT INTO `archived_grades` VALUES (1,'Group deleted by user (group deletion)','2025-09-02 17:05:14.638106','SYSTEM',65,'GR0066','2025-09-01 08:34:07.994168',12,2,1,4,'2025-09-01 08:34:07.994174','БЗ121001','Веб-технології'),(2,'Group deleted by user (group deletion)','2025-09-02 17:05:14.656968','SYSTEM',65,'GR0066','2025-08-29 12:27:09.894675',2,2,1,1,'2025-09-01 08:47:04.929745','БЗ121001','Програмування'),(3,'Group deleted by user (group deletion)','2025-09-02 17:05:14.658451','SYSTEM',45,'GR0066','2025-08-29 12:27:09.892049',1,2,1,1,'2025-09-02 09:09:58.617583','БЗ121001','Програмування'),(4,'Group deleted by user (group deletion)','2025-09-02 17:05:14.669967','SYSTEM',87,'GR0066','2025-09-01 08:41:43.452343',13,2,4,2,'2025-09-01 08:41:43.452351','БЗ250004','Бази даних'),(5,'Group deleted by user (group deletion)','2025-09-02 17:05:14.671402','SYSTEM',85,'GR0066','2025-09-01 08:26:06.450640',9,2,4,4,'2025-09-01 08:26:06.450647','БЗ250004','Веб-технології'),(6,'Group deleted by user (group deletion)','2025-09-02 17:05:14.677786','SYSTEM',42,'GR0066','2025-09-01 08:32:55.304471',11,2,6,3,'2025-09-01 08:32:55.304501','БЗ25006','Алгоритми та структури даних');
/*!40000 ALTER TABLE `archived_grades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archived_student_groups`
--

DROP TABLE IF EXISTS `archived_student_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `archived_student_groups` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `archive_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `archived_at` datetime(6) NOT NULL,
  `archived_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `course_year` int DEFAULT NULL,
  `enrollment_year` int DEFAULT NULL,
  `group_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `original_created_at` datetime(6) DEFAULT NULL,
  `original_group_id` bigint NOT NULL,
  `original_updated_at` datetime(6) DEFAULT NULL,
  `study_form` enum('FULL_TIME','PART_TIME','EVENING','DISTANCE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archived_student_groups`
--

LOCK TABLES `archived_student_groups` WRITE;
/*!40000 ALTER TABLE `archived_student_groups` DISABLE KEYS */;
INSERT INTO `archived_student_groups` VALUES (1,'Group deleted by user','2025-09-02 10:24:02.226681','SYSTEM',3,2021,'GR0001','БЗ-121','2025-08-29 12:27:09.777911',1,'2025-09-02 10:09:09.738136','FULL_TIME'),(2,'Group deleted by user','2025-09-02 17:05:14.680529','SYSTEM',2,2020,'GR0066','ПІ-121-1','2025-08-29 12:27:09.785114',2,'2025-09-02 12:26:40.359951','PART_TIME');
/*!40000 ALTER TABLE `archived_student_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archived_students`
--

DROP TABLE IF EXISTS `archived_students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `archived_students` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `archive_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `archived_at` datetime(6) NOT NULL,
  `archived_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `enrollment_year` int DEFAULT NULL,
  `group_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `original_created_at` datetime(6) DEFAULT NULL,
  `original_group_id` bigint DEFAULT NULL,
  `original_student_id` bigint NOT NULL,
  `original_updated_at` datetime(6) DEFAULT NULL,
  `phone_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `student_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `study_form` enum('FULL_TIME','PART_TIME','EVENING','DISTANCE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archived_students`
--

LOCK TABLES `archived_students` WRITE;
/*!40000 ALTER TABLE `archived_students` DISABLE KEYS */;
INSERT INTO `archived_students` VALUES (1,NULL,'Group deleted by user (group deletion)','2025-09-02 10:24:02.207576','SYSTEM',2025,'GR0001','БЗ-121','2025-09-01 08:14:51.000000',1,5,'2025-09-02 09:47:03.078824',NULL,'БЗ250005','FULL_TIME'),(2,'м. Київ, вул. Студентська, 15','Group deleted by user (group deletion)','2025-09-02 17:05:14.659975','SYSTEM',2021,'GR0066','ПІ-121-1','2025-08-29 12:27:09.859964',2,1,'2025-09-02 12:26:40.786572','+380631234567','БЗ121001','FULL_TIME'),(3,NULL,'Group deleted by user (group deletion)','2025-09-02 17:05:14.672855','SYSTEM',2025,'GR0066','ПІ-121-1','2025-09-01 08:14:47.000000',2,4,'2025-09-02 12:26:40.793820',NULL,'БЗ250004','FULL_TIME'),(4,NULL,'Group deleted by user (group deletion)','2025-09-02 17:05:14.679307','SYSTEM',2025,'GR0066','ПІ-121-1','2025-09-01 08:30:37.276394',2,6,'2025-09-02 12:26:40.800698',NULL,'БЗ25006','FULL_TIME');
/*!40000 ALTER TABLE `archived_students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grades`
--

DROP TABLE IF EXISTS `grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grades` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comments` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `grade_date` datetime(6) NOT NULL,
  `grade_type` enum('CURRENT','MODULE','MIDTERM','FINAL','RETAKE','MAKEUP') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `grade_value` int NOT NULL,
  `is_final` bit(1) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `student_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  `teacher_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKuslnqqrs3b4clqwos43j6ljd` (`student_id`,`subject_id`,`grade_type`),
  KEY `FKrc0s5tgvm9r4ccxitaqtu88k5` (`subject_id`),
  KEY `FKjkankww1vg2lw4ysxo90qp51h` (`teacher_id`),
  CONSTRAINT `FK13a16545m7vvrcspc999r15s9` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
  CONSTRAINT `FKjkankww1vg2lw4ysxo90qp51h` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`id`),
  CONSTRAINT `FKrc0s5tgvm9r4ccxitaqtu88k5` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grades`
--

LOCK TABLES `grades` WRITE;
/*!40000 ALTER TABLE `grades` DISABLE KEYS */;
/*!40000 ALTER TABLE `grades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_groups`
--

DROP TABLE IF EXISTS `student_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_groups` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_year` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `group_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `max_students` int DEFAULT NULL,
  `specialization` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_year` int DEFAULT NULL,
  `enrollment_year` int DEFAULT NULL,
  `study_form` enum('FULL_TIME','PART_TIME','EVENING','DISTANCE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `education_level` enum('BACHELOR','MASTER','PHD') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jc3sgr86kaisj55xdprb2dtlr` (`group_name`),
  UNIQUE KEY `group_code` (`group_code`),
  KEY `idx_student_groups_group_code` (`group_code`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_groups`
--

LOCK TABLES `student_groups` WRITE;
/*!40000 ALTER TABLE `student_groups` DISABLE KEYS */;
INSERT INTO `student_groups` VALUES (3,2,'2025-08-29 12:27:09.786637','КН-221','GR0003',_binary '',28,'Комп\'ютерні науки',NULL,2022,'FULL_TIME','2025-09-03 09:37:26.827540','MASTER'),(4,1,'2025-09-03 06:17:05.217902','ПР-123-24-1','GR0444',_binary '',NULL,'Шото_тест1',NULL,2025,'DISTANCE','2025-09-03 06:28:13.630624','PHD'),(5,5,'2025-09-03 06:27:47.311718','ПІ-123-23-2','GR0009',_binary '',NULL,NULL,NULL,2025,'FULL_TIME','2025-09-03 12:42:58.969430','BACHELOR'),(6,1,'2025-09-03 06:33:12.462048','ПР-121-1-1','ПР1',_binary '',NULL,'Шото_тест2',NULL,2020,'PART_TIME','2025-09-03 06:33:12.462067','BACHELOR'),(7,2,'2025-09-03 06:33:48.536403','ПР-121-1-2','ПР2',_binary '',NULL,NULL,NULL,2020,'DISTANCE','2025-09-03 06:33:48.536409','PHD'),(8,2,'2025-09-03 06:34:24.577535','ПР-121-1-3','ПР3',_binary '',25,'Шото_тест3',NULL,2021,'EVENING','2025-09-03 06:34:24.577542','MASTER');
/*!40000 ALTER TABLE `student_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `enrollment_year` int DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `phone_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `student_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `study_form` enum('FULL_TIME','PART_TIME','EVENING','DISTANCE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `group_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `course_year` int DEFAULT NULL,
  `education_level` enum('BACHELOR','MASTER','PHD') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_h7gboo6v79gig1eo7lt1fubew` (`student_number`),
  UNIQUE KEY `UK_g4fwvutq09fjdlb4bb0byp7t` (`user_id`),
  KEY `FKk44ecnoi1xpn5d3ofspe7ciss` (`group_id`),
  CONSTRAINT `FKdt1cjx5ve5bdabmuuf3ibrwaq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKk44ecnoi1xpn5d3ofspe7ciss` FOREIGN KEY (`group_id`) REFERENCES `student_groups` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (7,NULL,'2025-09-03 06:46:47.417437',2025,_binary '',NULL,'БЗ25001','FULL_TIME','2025-09-03 12:42:59.092277',5,16,NULL,NULL),(8,NULL,'2025-09-03 06:56:32.792614',2025,_binary '',NULL,'БЗ25002','FULL_TIME','2025-09-03 09:37:26.916725',3,17,NULL,NULL),(9,NULL,'2025-09-03 06:56:52.832838',2025,_binary '',NULL,'БЗ25003','FULL_TIME','2025-09-03 09:37:37.229240',3,18,NULL,NULL),(10,NULL,'2025-09-03 06:57:15.308449',2025,_binary '',NULL,'БЗ25004','FULL_TIME','2025-09-03 09:39:09.959453',3,19,NULL,NULL),(11,NULL,'2025-09-03 06:57:32.775441',2025,_binary '',NULL,'БЗ25005','FULL_TIME','2025-09-03 12:42:59.075594',5,20,NULL,NULL),(12,NULL,'2025-09-03 06:57:57.943963',2025,_binary '',NULL,'БЗ25006','FULL_TIME','2025-09-03 12:42:59.083667',5,21,NULL,NULL);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject_group`
--

DROP TABLE IF EXISTS `subject_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subject_group` (
  `subject_id` bigint NOT NULL,
  `group_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`subject_id`,`group_id`),
  KEY `fk_sg_group` (`group_id`),
  CONSTRAINT `fk_sg_group` FOREIGN KEY (`group_id`) REFERENCES `student_groups` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_sg_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject_group`
--

LOCK TABLES `subject_group` WRITE;
/*!40000 ALTER TABLE `subject_group` DISABLE KEYS */;
INSERT INTO `subject_group` VALUES (1,3,'2025-09-03 11:49:03'),(1,4,'2025-09-03 11:49:03'),(1,5,'2025-09-03 12:53:09'),(2,3,'2025-09-03 11:49:03'),(3,3,'2025-09-03 12:53:57'),(3,4,'2025-09-03 12:54:00'),(3,5,'2025-09-03 11:49:03'),(3,8,'2025-09-03 12:54:06'),(5,3,'2025-09-03 12:53:36'),(5,4,'2025-09-03 12:53:38'),(5,5,'2025-09-03 12:53:40'),(5,8,'2025-09-03 12:53:46');
/*!40000 ALTER TABLE `subject_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subjects` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assessment_type` enum('EXAM','TEST','DIFFERENTIATED_CREDIT','COURSE_WORK','QUALIFICATION_WORK','ATTESTATION','STATE_EXAM') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `credits` int DEFAULT NULL,
  `description` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `hours_laboratory` int DEFAULT NULL,
  `hours_lectures` int DEFAULT NULL,
  `hours_practical` int DEFAULT NULL,
  `hours_total` int DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `semester` int DEFAULT NULL,
  `subject_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subject_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_qt734ivq9gq4yo4p1j1lhhk8l` (`subject_code`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subjects`
--

LOCK TABLES `subjects` WRITE;
/*!40000 ALTER TABLE `subjects` DISABLE KEYS */;
INSERT INTO `subjects` VALUES (1,'EXAM','2025-08-29 12:27:09.788546',6,'Основи програмування на мові Java',30,60,90,180,_binary '',5,'PROG-301','Програмування','2025-08-29 12:27:09.788556'),(2,'EXAM','2025-08-29 12:27:09.791866',5,'Проектування та розробка баз даних',30,45,75,150,_binary '',5,'DB-301','Бази даних','2025-08-29 12:27:09.791873'),(3,'DIFFERENTIATED_CREDIT','2025-08-29 12:27:09.793820',4,'Вивчення основних алгоритмів та структур даних',30,40,50,120,_binary '',3,'ASD-201','Алгоритми та структури даних','2025-08-29 12:27:09.793827'),(4,'COURSE_WORK','2025-08-29 12:27:09.795734',5,'Розробка веб-додатків з використанням сучасних технологій',30,30,90,150,_binary '',7,'WEB-401','Веб-технології','2025-08-29 12:27:09.795744'),(5,'EXAM','2025-09-01 10:46:34.731639',4,'',50,30,40,120,_binary '',1,'123','Нихеранеделание','2025-09-01 10:46:34.731669'),(6,'EXAM','2025-09-03 06:59:59.629893',6,'',40,140,20,200,_binary '',8,'GZ12312','Что-то важное','2025-09-03 06:59:59.629898'),(7,'QUALIFICATION_WORK','2025-09-03 07:31:45.266626',25,'',10,5,5,25,_binary '',10,'RRRR123','Что-то важное','2025-09-03 07:31:45.266641'),(8,'STATE_EXAM','2025-09-03 08:39:02.279601',25,'',28,37,37,100,_binary '',10,'GG123','Что-то ну очень важное','2025-09-03 08:39:02.279615');
/*!40000 ALTER TABLE `subjects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_subjects`
--

DROP TABLE IF EXISTS `teacher_subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_subjects` (
  `subject_id` bigint NOT NULL,
  `teacher_id` bigint NOT NULL,
  PRIMARY KEY (`subject_id`,`teacher_id`),
  KEY `FK6dcl3ihufp4v0j1fuxlw4ksoj` (`teacher_id`),
  CONSTRAINT `FK6dcl3ihufp4v0j1fuxlw4ksoj` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`id`),
  CONSTRAINT `FKdweqkwxroox2u7pbmksehx04i` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_subjects`
--

LOCK TABLES `teacher_subjects` WRITE;
/*!40000 ALTER TABLE `teacher_subjects` DISABLE KEYS */;
INSERT INTO `teacher_subjects` VALUES (1,1),(3,1),(5,1),(6,1),(2,2),(4,2),(5,2),(6,3),(7,3),(8,3);
/*!40000 ALTER TABLE `teacher_subjects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teachers`
--

DROP TABLE IF EXISTS `teachers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teachers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `academic_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `biography` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `department_position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hire_date` datetime(6) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `office_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `scientific_degree` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_cd1k6xwg9jqtiwx9ybnxpmoh9` (`user_id`),
  CONSTRAINT `FKb8dct7w2j1vl1r2bpstw5isc0` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachers`
--

LOCK TABLES `teachers` WRITE;
/*!40000 ALTER TABLE `teachers` DISABLE KEYS */;
INSERT INTO `teachers` VALUES (1,'Доцент','Досвідчений викладач з 15-річним стажем роботи у сфері IT','2025-08-29 12:27:09.829188','Завідувач кафедри','2025-08-29 12:27:09.828501',_binary '','201','+380501234567','Кандидат технічних наук','2025-08-29 12:27:09.829196',3),(2,'Старший викладач','Молодий перспективний викладач, спеціаліст з баз даних','2025-08-29 12:27:09.844727','Викладач','2025-08-29 12:27:09.844588',_binary '','203','+380507654321','Магістр комп\'ютерних наук','2025-08-29 12:27:09.844734',4),(3,NULL,NULL,'2025-09-01 09:45:42.936336','Викладач','2025-09-01 09:45:42.935818',_binary '',NULL,NULL,NULL,'2025-09-01 09:45:42.936344',14);
/*!40000 ALTER TABLE `teachers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `first_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `last_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('ADMIN','MANAGER','TEACHER','STUDENT','GUEST') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2025-08-29 12:27:09.165077','admin@university.ua','Адміністратор',_binary '','Системи','$2a$10$OK2TZatAUFXKNwjsHZBWUOkVPvLYkajETscrJ6rFWf3JYv5UHf2t2','ADMIN','2025-09-01 07:16:22.267028','admin'),(3,'2025-08-29 12:27:09.351371','ivanov@university.ua','Іван',_binary '','Іванов','$2a$10$gPKlsMS/BWl5odPiWiwNyer7ePeWJdHIoaWAphdjKRdpzluZGVdI.','TEACHER','2025-08-29 12:27:09.351379','teacher1'),(4,'2025-08-29 12:27:09.435814','petrov@university.ua','Петро',_binary '','Петров','$2a$10$ebPIoFaPurPbH3Fd6Nqwn.pypIoDvcNvlUhh/6PcRPb9BzBm5AWCG','TEACHER','2025-08-29 12:27:09.435821','teacher2'),(14,'2025-09-01 09:45:42.915094','test@teacher.com','Тест',_binary '','Вчитель','$2a$10$bLJhEBnxrFvvnhKDfKEJ0umLQcfuccCU3VhUR6X..LfrhXcOcwKY6','TEACHER','2025-09-02 14:41:34.574126','testteacher'),(15,'2025-09-02 06:30:31.594094','manager@test.com','manager',_binary '','manager','$2a$10$UGYkK7dgeITzkIpuVE5XzuByIHgoIfq3YpAzTV9JZCcUwMnVrdesa','MANAGER','2025-09-02 06:30:39.411410','manager1'),(16,'2025-09-03 06:46:47.404339','aleha@test.com','Альоша',_binary '','Альошович','$2a$10$K85vYw/pGBoov2TVxQ2ynOXcaJgDtV4gE8TboGRJ9QDaOQ450z79u','STUDENT','2025-09-03 06:46:52.631579','альоша'),(17,'2025-09-03 06:56:32.788109','student1@test.com','student1',_binary '','student1','$2a$10$Z53ip33jlAuTD/NMOJ4gs.eh.VZpJakKn6mCQFSUaH8gGthfGYBO.','STUDENT','2025-09-03 06:58:02.117867','student1'),(18,'2025-09-03 06:56:52.829613','student2@test.com','student2',_binary '','student2','$2a$10$YWKqlilLdLGJs0VDHHZKNezdVHFJUBM0dCchsczQSdLfT2dUF/JKu','STUDENT','2025-09-03 06:58:04.986191','student2'),(19,'2025-09-03 06:57:15.303837','student3@test.com','student3',_binary '','student3','$2a$10$8NHi.kYpPGHLuk5egiqJd.1XpSXLpTU5DgjsGdy62525OK7XzjyYW','STUDENT','2025-09-03 06:58:07.476990','student3'),(20,'2025-09-03 06:57:32.772277','student4@test.com','student4',_binary '','student4','$2a$10$1mZWwERzZf26pLbA/kpeueyf1LLypAs1N6ThA4N8mii/XiCql7Tvm','STUDENT','2025-09-03 06:58:08.979739','student4'),(21,'2025-09-03 06:57:57.939278','student5@test.com','student5',_binary '','student5','$2a$10$q2O9upBp8V1P7iK4FY.lXO5xleH.ltLskYFBwHhcGhRi9El13ymym','STUDENT','2025-09-03 06:58:10.107407','student5');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-03 14:46:01

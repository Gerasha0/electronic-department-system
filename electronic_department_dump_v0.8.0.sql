-- MySQL dump 10.13  Distrib 8.4.6, for Linux (x86_64)
--
-- Host: localhost    Database: electronic_department
-- ------------------------------------------------------
-- Server version	8.4.6-0ubuntu0.25.04.2

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
  `comments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `grade_type` enum('LABORATORY_WORK','PRACTICAL_WORK','SEMINAR','CONTROL_WORK','MODULE_WORK','HOMEWORK','INDIVIDUAL_WORK','CURRENT_MAKEUP','EXAM','CREDIT','DIFFERENTIATED_CREDIT','COURSE_WORK','QUALIFICATION_WORK','STATE_EXAM','ATTESTATION','RETAKE_EXAM','RETAKE_CREDIT','RETAKE_WORK','MAKEUP_LESSON','MAKEUP_WORK','ADDITIONAL_TASK') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `grade_category_enum` enum('CURRENT_CONTROL','FINAL_CONTROL','RETAKE','MAKEUP') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CURRENT_CONTROL',
  `student_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archived_grades`
--

LOCK TABLES `archived_grades` WRITE;
/*!40000 ALTER TABLE `archived_grades` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archived_student_groups`
--

LOCK TABLES `archived_student_groups` WRITE;
/*!40000 ALTER TABLE `archived_student_groups` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archived_students`
--

LOCK TABLES `archived_students` WRITE;
/*!40000 ALTER TABLE `archived_students` DISABLE KEYS */;
/*!40000 ALTER TABLE `archived_students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'0','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'dept_user','2025-09-09 19:25:26',0,1),(2,'1','init schema','SQL','V1__init_schema.sql',472685385,'dept_user','2025-09-09 19:28:16',0,1),(3,'2','update student groups','SQL','V2__update_student_groups.sql',1203417122,'dept_user','2025-09-09 19:28:16',0,1),(4,'3','add student education level','SQL','V3__add_student_education_level.sql',1933642925,'dept_user','2025-09-09 19:28:16',0,1),(5,'4','add subject groups relationship','SQL','V4__add_subject_groups_relationship.sql',-1155082464,'dept_user','2025-09-09 19:28:16',0,1),(6,'5','add grade type comments to archived grades','SQL','V5__add_grade_type_comments_to_archived_grades.sql',-1860752386,'dept_user','2025-09-09 19:28:16',0,1),(7,'6','Add archive grade fields','SQL','V6__Add_archive_grade_fields.sql',-103184047,'dept_user','2025-09-09 19:28:16',0,1),(8,'7','Add grade category to grades','SQL','V7__Add_grade_category_to_grades.sql',2069571087,'dept_user','2025-09-09 19:28:16',0,1),(9,'8','Add specialist education level','SQL','V8__Add_specialist_education_level.sql',-1829453943,'dept_user','2025-09-09 19:28:16',0,1),(10,'9','create archived tables','SQL','V9__create_archived_tables.sql',870671196,'dept_user','2025-09-09 19:28:16',0,1),(11,'10','update grade category system','SQL','V10__update_grade_category_system.sql',123456789,'dept_user','2025-09-09 19:36:26',1000,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
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
  `grade_type` enum('LABORATORY_WORK','PRACTICAL_WORK','SEMINAR','CONTROL_WORK','MODULE_WORK','HOMEWORK','INDIVIDUAL_WORK','CURRENT_MAKEUP','EXAM','CREDIT','DIFFERENTIATED_CREDIT','COURSE_WORK','QUALIFICATION_WORK','STATE_EXAM','ATTESTATION','RETAKE_EXAM','RETAKE_CREDIT','RETAKE_WORK','MAKEUP_LESSON','MAKEUP_WORK','ADDITIONAL_TASK') COLLATE utf8mb4_unicode_ci NOT NULL,
  `grade_category_enum` enum('CURRENT_CONTROL','FINAL_CONTROL','RETAKE','MAKEUP') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CURRENT_CONTROL',
  `grade_value` int NOT NULL,
  `is_final` bit(1) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `student_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  `teacher_id` bigint NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKuslnqqrs3b4clqwos43j6ljd` (`student_id`,`subject_id`,`grade_type`),
  KEY `FKrc0s5tgvm9r4ccxitaqtu88k5` (`subject_id`),
  KEY `FKjkankww1vg2lw4ysxo90qp51h` (`teacher_id`),
  CONSTRAINT `FK13a16545m7vvrcspc999r15s9` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
  CONSTRAINT `FKjkankww1vg2lw4ysxo90qp51h` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`id`),
  CONSTRAINT `FKrc0s5tgvm9r4ccxitaqtu88k5` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_groups`
--

LOCK TABLES `student_groups` WRITE;
/*!40000 ALTER TABLE `student_groups` DISABLE KEYS */;
INSERT INTO `student_groups` VALUES (9,1,'2025-09-10 06:44:14.024099','TST-121-21-1','TST21',_binary '',5,'Test1',NULL,2025,'FULL_TIME','2025-09-10 06:44:14.024111','BACHELOR'),(10,1,'2025-09-10 06:45:00.645602','TST-121-21-2','TST22',_binary '',10,'Test2',NULL,2020,'PART_TIME','2025-09-10 06:45:00.645623','MASTER'),(11,2,'2025-09-10 06:45:30.085805','TST-121-21-3','TST23',_binary '',20,'Test3',NULL,2021,'EVENING','2025-09-10 06:45:30.085821','PHD'),(12,3,'2025-09-10 06:45:57.309476','TST-121-21-4','TST24',_binary '',15,'Test4',NULL,2021,'DISTANCE','2025-09-10 06:45:57.309484','BACHELOR'),(13,5,'2025-09-10 06:46:29.326519','TST-121-21-5','TST25',_binary '',14,'Test5',NULL,2020,'FULL_TIME','2025-09-10 06:46:29.326525','BACHELOR');
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
  `education_level` enum('BACHELOR','SPECIALIST','MASTER','PHD') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_h7gboo6v79gig1eo7lt1fubew` (`student_number`),
  UNIQUE KEY `UK_g4fwvutq09fjdlb4bb0byp7t` (`user_id`),
  KEY `FKk44ecnoi1xpn5d3ofspe7ciss` (`group_id`),
  CONSTRAINT `FKdt1cjx5ve5bdabmuuf3ibrwaq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKk44ecnoi1xpn5d3ofspe7ciss` FOREIGN KEY (`group_id`) REFERENCES `student_groups` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (19,NULL,'2025-09-10 06:40:26.551427',2025,_binary '',NULL,'БЗ25001','FULL_TIME','2025-09-10 06:40:26.551439',NULL,33,NULL,NULL),(20,NULL,'2025-09-10 06:40:58.254207',2025,_binary '',NULL,'БЗ25002','FULL_TIME','2025-09-10 06:40:58.254220',NULL,34,NULL,NULL),(21,NULL,'2025-09-10 06:41:29.497010',2025,_binary '',NULL,'БЗ25003','FULL_TIME','2025-09-10 06:41:29.497019',NULL,35,NULL,NULL),(22,NULL,'2025-09-10 06:42:04.042387',2025,_binary '',NULL,'БЗ25004','FULL_TIME','2025-09-10 06:42:04.042394',NULL,36,NULL,NULL),(23,NULL,'2025-09-10 06:42:53.474155',2025,_binary '',NULL,'БЗ25005','FULL_TIME','2025-09-10 06:42:53.474161',NULL,37,NULL,NULL),(24,NULL,'2025-09-10 06:47:17.163817',2025,_binary '',NULL,'БЗ25006','FULL_TIME','2025-09-10 06:47:17.163823',NULL,38,NULL,NULL),(25,NULL,'2025-09-10 06:47:53.397577',2025,_binary '',NULL,'БЗ25007','FULL_TIME','2025-09-10 06:47:53.397599',NULL,39,NULL,NULL);
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
  `assessment_type` enum('EXAM','TEST','DIFFERENTIATED_CREDIT','COURSE_WORK','QUALIFICATION_WORK','ATTESTATION','STATE_EXAM') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subjects`
--

LOCK TABLES `subjects` WRITE;
/*!40000 ALTER TABLE `subjects` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachers`
--

LOCK TABLES `teachers` WRITE;
/*!40000 ALTER TABLE `teachers` DISABLE KEYS */;
INSERT INTO `teachers` VALUES (5,NULL,NULL,'2025-09-10 06:30:58.366797','Викладач','2025-09-10 06:30:58.366041',_binary '',NULL,NULL,NULL,'2025-09-10 06:30:58.366809',29),(6,NULL,NULL,'2025-09-10 06:31:29.503933','Викладач','2025-09-10 06:31:29.503616',_binary '',NULL,NULL,NULL,'2025-09-10 06:31:29.503943',30),(7,NULL,NULL,'2025-09-10 06:32:06.377586','Викладач','2025-09-10 06:32:06.377390',_binary '',NULL,NULL,NULL,'2025-09-10 06:32:06.377596',31);
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
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2025-08-29 12:27:09.165077','admin@university.ua','Адміністратор',_binary '','Системи','$2a$10$OK2TZatAUFXKNwjsHZBWUOkVPvLYkajETscrJ6rFWf3JYv5UHf2t2','ADMIN','2025-09-09 14:14:19.118933','admin'),(29,'2025-09-10 06:30:58.331692','teacher1@test.com','Учитель1',_binary '','Фамилия1','$2a$10$fOxJBuudmwIhDKRJEWXmPuX6RTfScky9dWeupPQjmWvpA2mbnkiw.','TEACHER','2025-09-10 06:32:48.484160','teacher1'),(30,'2025-09-10 06:31:29.500339','teacher2@test.com','Учитель2',_binary '','Фамилия2','$2a$10$U0fvlSI8NVdZUd9CI5WwLudrtOI7r4TlRdkkfFnPEXAA16h7xYU.S','TEACHER','2025-09-10 06:32:50.833304','teacher2'),(31,'2025-09-10 06:32:06.375227','teacher3@test.com','Учитель3',_binary '','Фамилия3','$2a$10$emJfRMZ3cjQauF6r18ij.Ou/3RRkNQ0HfnlToLnFSx//AIxvkTtWe','TEACHER','2025-09-10 06:32:52.838105','Teacher3'),(32,'2025-09-10 06:32:44.342818','manager@test.com','Менеджер1',_binary '','Фамилия1','$2a$10$Yzno2lmieyu.FIQ9M/HzpO33dBDO0ah0Qvc49JetvA6Xogf7us9nG','MANAGER','2025-09-10 06:32:54.681808','manager1'),(33,'2025-09-10 06:40:26.544890','student1@test.com','Студент1',_binary '','Фамилия1','$2a$10$YWo1uqt5QkwxbymMpyyGne60ddKgoM0CmRaILmkA4qX.dkBj2TjLO','STUDENT','2025-09-10 06:40:26.544917','student1'),(34,'2025-09-10 06:40:58.243677','student2@test.com','Студент2',_binary '','Фамилия2','$2a$10$mgg3CIii2UiBI6wM9mFGwOqryhoTdZ8kgWOTTWBAIb0wXdIKQw8o2','STUDENT','2025-09-10 06:40:58.243705','student2'),(35,'2025-09-10 06:41:29.491245','student3@test.com','Студент3',_binary '','Фамилия3','$2a$10$Lzvy9kFa5ZoluI8Lpve9v.y2/zhEsmdckl6affXw1ZpE5Gv99EtWm','STUDENT','2025-09-10 06:41:29.491268','student3'),(36,'2025-09-10 06:42:04.038361','student4@test.com','Студент4',_binary '','Фамилия4','$2a$10$1rkwiqMw8fk5jHwAgJEj5.cIEauSnM/5gYgIrWGPqKQSNaBMr02N6','STUDENT','2025-09-10 06:42:04.038376','student4'),(37,'2025-09-10 06:42:53.466429','student5@test.com','Студент5',_binary '','Фамилия5','$2a$10$LnZYTGcbJZwuoA69ci2pBebcqX9gJXr2R4Gl1ir8sSmdID0bDyr8K','STUDENT','2025-09-10 06:42:53.466456','student5'),(38,'2025-09-10 06:47:17.158203','student6@test.com','Студент6',_binary '','Фамилия6','$2a$10$UWBBjyZpM7D1OWVffYWp4Ohc4bXQzzOWjrFpVevAo9LEUKva/b2K6','STUDENT','2025-09-10 06:47:17.158224','student6'),(39,'2025-09-10 06:47:53.382243','student7@test.com','Студент7',_binary '','Фамилия7','$2a$10$ENAQqg.kpsxfX52JJarauOaO.hJVmIHNMjH8GhIFIBsq/EExncO.u','STUDENT','2025-09-10 06:47:53.382279','student7');
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

-- Dump completed on 2025-09-10  6:49:28

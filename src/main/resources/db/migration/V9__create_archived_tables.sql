-- Create archived tables for data archiving

CREATE TABLE archived_grades (
  id BIGINT NOT NULL AUTO_INCREMENT,
  archive_reason VARCHAR(255) DEFAULT NULL,
  archived_at DATETIME(6) NOT NULL,
  archived_by VARCHAR(255) DEFAULT NULL,
  grade_value INT DEFAULT NULL,
  group_code VARCHAR(255) DEFAULT NULL,
  original_created_at DATETIME(6) DEFAULT NULL,
  original_grade_id BIGINT NOT NULL,
  original_group_id BIGINT DEFAULT NULL,
  original_student_id BIGINT NOT NULL,
  original_subject_id BIGINT DEFAULT NULL,
  original_updated_at DATETIME(6) DEFAULT NULL,
  student_number VARCHAR(255) DEFAULT NULL,
  subject_name VARCHAR(255) DEFAULT NULL,
  comments TEXT,
  grade_type ENUM('CURRENT','MODULE','MIDTERM','FINAL','RETAKE','MAKEUP','LABORATORY','PRACTICAL','SEMINAR','CONTROL_WORK','MODULE_WORK','HOMEWORK','INDIVIDUAL_WORK','MAKEUP_WORK','EXAM','CREDIT','DIFF_CREDIT','COURSEWORK','QUALIFICATION_WORK','STATE_EXAM','ATTESTATION','RETAKE_EXAM','RETAKE_CREDIT','RETAKE_WORK','MAKEUP_LESSON','ADDITIONAL_TASK') DEFAULT NULL,
  grade_category VARCHAR(255) DEFAULT NULL,
  student_name VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE archived_student_groups (
  id BIGINT NOT NULL AUTO_INCREMENT,
  archive_reason VARCHAR(255) DEFAULT NULL,
  archived_at DATETIME(6) NOT NULL,
  archived_by VARCHAR(255) DEFAULT NULL,
  course_year INT DEFAULT NULL,
  enrollment_year INT DEFAULT NULL,
  group_code VARCHAR(255) NOT NULL,
  group_name VARCHAR(255) NOT NULL,
  original_created_at DATETIME(6) DEFAULT NULL,
  original_group_id BIGINT NOT NULL,
  original_updated_at DATETIME(6) DEFAULT NULL,
  study_form ENUM('FULL_TIME','PART_TIME','EVENING','DISTANCE') DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE archived_students (
  id BIGINT NOT NULL AUTO_INCREMENT,
  address VARCHAR(255) DEFAULT NULL,
  archive_reason VARCHAR(255) DEFAULT NULL,
  archived_at DATETIME(6) NOT NULL,
  archived_by VARCHAR(255) DEFAULT NULL,
  enrollment_year INT DEFAULT NULL,
  group_code VARCHAR(255) DEFAULT NULL,
  group_name VARCHAR(255) DEFAULT NULL,
  original_created_at DATETIME(6) DEFAULT NULL,
  original_group_id BIGINT DEFAULT NULL,
  original_student_id BIGINT NOT NULL,
  original_updated_at DATETIME(6) DEFAULT NULL,
  phone_number VARCHAR(255) DEFAULT NULL,
  student_number VARCHAR(255) NOT NULL,
  study_form ENUM('FULL_TIME','PART_TIME','EVENING','DISTANCE') DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Initial schema for MySQL adapted from H2 in-memory model
-- Note: adjust types/constraints if you customized entities

CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  role ENUM('ADMIN','MANAGER','TEACHER','STUDENT','GUEST') NOT NULL,
  is_active BIT(1) DEFAULT NULL,
  created_at DATETIME(6) DEFAULT NULL,
  updated_at DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (username),
  UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE student_groups (
  id BIGINT NOT NULL AUTO_INCREMENT,
  group_name VARCHAR(255) NOT NULL,
  course_year INT NOT NULL,
  study_form ENUM('FULL_TIME','PART_TIME','EVENING','DISTANCE') DEFAULT NULL,
  specialization VARCHAR(255) DEFAULT NULL,
  max_students INT DEFAULT NULL,
  group_code VARCHAR(10) DEFAULT NULL,
  is_active BIT(1) DEFAULT NULL,
  start_year INT DEFAULT NULL,
  enrollment_year INT DEFAULT NULL,
  created_at DATETIME(6) DEFAULT NULL,
  updated_at DATETIME(6) DEFAULT NULL,
  education_level ENUM('BACHELOR','MASTER','PHD') DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (group_name),
  UNIQUE (group_code),
  INDEX idx_student_groups_group_code (group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE subjects (
  id BIGINT NOT NULL AUTO_INCREMENT,
  subject_name VARCHAR(255) NOT NULL,
  subject_code VARCHAR(255) DEFAULT NULL,
  credits INT DEFAULT NULL,
  assessment_type ENUM('EXAM','TEST','DIFFERENTIATED_CREDIT','COURSE_WORK','QUALIFICATION_WORK','ATTESTATION','STATE_EXAM') DEFAULT NULL,
  description TINYTEXT DEFAULT NULL,
  hours_total INT DEFAULT NULL,
  hours_lectures INT DEFAULT NULL,
  hours_practical INT DEFAULT NULL,
  hours_laboratory INT DEFAULT NULL,
  semester INT DEFAULT NULL,
  is_active BIT(1) DEFAULT NULL,
  created_at DATETIME(6) DEFAULT NULL,
  updated_at DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (subject_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE teachers (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  academic_title VARCHAR(255) DEFAULT NULL,
  scientific_degree VARCHAR(255) DEFAULT NULL,
  department_position VARCHAR(255) DEFAULT NULL,
  phone_number VARCHAR(255) DEFAULT NULL,
  office_number VARCHAR(255) DEFAULT NULL,
  hire_date DATETIME(6) DEFAULT NULL,
  biography TINYTEXT DEFAULT NULL,
  is_active BIT(1) DEFAULT NULL,
  created_at DATETIME(6) DEFAULT NULL,
  updated_at DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (user_id),
  CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE students (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  student_number VARCHAR(255) NOT NULL,
  enrollment_year INT DEFAULT NULL,
  study_form ENUM('FULL_TIME','PART_TIME','EVENING','DISTANCE') DEFAULT NULL,
  group_id BIGINT DEFAULT NULL,
  phone_number VARCHAR(255) DEFAULT NULL,
  address VARCHAR(255) DEFAULT NULL,
  is_active BIT(1) DEFAULT NULL,
  course_year INT DEFAULT NULL,
  education_level ENUM('BACHELOR','SPECIALIST','MASTER','PHD') DEFAULT NULL,
  created_at DATETIME(6) DEFAULT NULL,
  updated_at DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (user_id),
  UNIQUE (student_number),
  CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_student_group FOREIGN KEY (group_id) REFERENCES student_groups(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE grades (
  id BIGINT NOT NULL AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  teacher_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL,
  grade_value INT NOT NULL,
  grade_type ENUM('CURRENT','MODULE','MIDTERM','FINAL','RETAKE','MAKEUP','LABORATORY','PRACTICAL','SEMINAR','CONTROL_WORK','MODULE_WORK','HOMEWORK','INDIVIDUAL_WORK','MAKEUP_WORK','EXAM','CREDIT','DIFF_CREDIT','COURSEWORK','QUALIFICATION_WORK','STATE_EXAM','ATTESTATION','RETAKE_EXAM','RETAKE_CREDIT','RETAKE_WORK','MAKEUP_LESSON','ADDITIONAL_TASK') NOT NULL,
  comments TINYTEXT DEFAULT NULL,
  is_final BIT(1) DEFAULT NULL,
  grade_date DATETIME(6) NOT NULL,
  grade_category VARCHAR(255) DEFAULT NULL,
  is_active BIT(1) DEFAULT NULL,
  created_at DATETIME(6) DEFAULT NULL,
  updated_at DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (student_id, subject_id, grade_type),
  CONSTRAINT fk_grade_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  CONSTRAINT fk_grade_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
  CONSTRAINT fk_grade_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Join table for subject-teacher many-to-many
CREATE TABLE teacher_subjects (
  subject_id BIGINT NOT NULL,
  teacher_id BIGINT NOT NULL,
  PRIMARY KEY (subject_id, teacher_id),
  CONSTRAINT fk_ts_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
  CONSTRAINT fk_ts_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Join table for subject-group many-to-many
CREATE TABLE subject_group (
  subject_id BIGINT NOT NULL,
  group_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (subject_id, group_id),
  CONSTRAINT fk_sg_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
  CONSTRAINT fk_sg_group FOREIGN KEY (group_id) REFERENCES student_groups(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

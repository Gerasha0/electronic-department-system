-- Add many-to-many relationship between subjects and student groups
CREATE TABLE subject_group (
  subject_id BIGINT NOT NULL,
  group_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (subject_id, group_id),
  CONSTRAINT fk_sg_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
  CONSTRAINT fk_sg_group FOREIGN KEY (group_id) REFERENCES student_groups(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Add new columns to archived_grades table
-- Version 0.6.8

ALTER TABLE archived_grades 
ADD COLUMN student_name VARCHAR(255) AFTER student_number,
ADD COLUMN grade_category VARCHAR(100) AFTER subject_name;

-- Update comment
UPDATE archived_grades SET grade_category = 'N/A' WHERE grade_category IS NULL;

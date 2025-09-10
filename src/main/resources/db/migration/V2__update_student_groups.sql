-- Add education_level, group_code, enrollment_year to student_groups table
ALTER TABLE student_groups ADD COLUMN education_level ENUM('BACHELOR','MASTER','PHD') DEFAULT NULL;

-- Update existing groups with default education level based on course_year
UPDATE student_groups SET education_level = 'BACHELOR' WHERE course_year >= 1 AND course_year <= 5;
UPDATE student_groups SET education_level = 'MASTER' WHERE course_year > 5 AND course_year <= 7;
UPDATE student_groups SET education_level = 'PHD' WHERE course_year > 7;

-- Set default for groups that might have edge cases
UPDATE student_groups SET education_level = 'BACHELOR' WHERE education_level IS NULL;

-- Add group_code and enrollment_year
ALTER TABLE student_groups 
ADD COLUMN group_code VARCHAR(10) NOT NULL UNIQUE AFTER group_name,
ADD COLUMN enrollment_year INT AFTER start_year;

-- Add index for group_code
CREATE INDEX idx_student_groups_group_code ON student_groups(group_code);

-- Update existing records with sample group codes (if any exist)
-- This is a one-time operation to populate group_code for existing records
UPDATE student_groups 
SET group_code = CONCAT('GR', LPAD(id, 4, '0')) 
WHERE group_code IS NULL OR group_code = '';

-- Set enrollment_year same as start_year for existing records
UPDATE student_groups 
SET enrollment_year = start_year 
WHERE enrollment_year IS NULL;

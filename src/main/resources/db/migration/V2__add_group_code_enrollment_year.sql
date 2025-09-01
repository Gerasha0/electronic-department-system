-- Add new columns to student_groups table
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

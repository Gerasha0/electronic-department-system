-- Add grade_type and comments columns to archived_grades table
-- Version 0.6.8

ALTER TABLE archived_grades 
ADD COLUMN grade_type VARCHAR(50),
ADD COLUMN comments TEXT;

-- Update existing records with default values if needed
UPDATE archived_grades 
SET grade_type = 'CURRENT' 
WHERE grade_type IS NULL;

-- Add index for better performance on grade_type searches
CREATE INDEX idx_archived_grades_grade_type ON archived_grades(grade_type);
CREATE INDEX idx_archived_grades_archived_at ON archived_grades(archived_at);

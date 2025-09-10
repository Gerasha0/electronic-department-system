-- Add course_year and education_level to students table (already added in V1, but for compatibility)
-- If not exists, add them
-- Note: In updated V1, these are already present, so this migration is redundant but kept for sequence

ALTER TABLE students 
ADD COLUMN IF NOT EXISTS course_year INT,
ADD COLUMN IF NOT EXISTS education_level VARCHAR(20);

-- Update existing students with default values based on enrollment year
-- For now, we'll set all existing students to Bachelor level and calculate course year
UPDATE students 
SET education_level = 'BACHELOR',
    course_year = IF(enrollment_year IS NOT NULL, LEAST(GREATEST(YEAR(NOW()) - enrollment_year + 1, 1), 5), 1);

-- You can manually update specific students to different education levels if needed
-- Example:
-- UPDATE students SET education_level = 'MASTER', course_year = 1 WHERE student_number IN ('M001', 'M002');
-- UPDATE students SET education_level = 'PHD', course_year = 1 WHERE student_number IN ('P001', 'P002');

-- Add education level and course year fields to students table
ALTER TABLE students 
ADD COLUMN course_year INT,
ADD COLUMN education_level VARCHAR(20);

-- Update existing students with default values based on enrollment year
-- For now, we'll set all existing students to Bachelor level and calculate course year
UPDATE students 
SET education_level = 'BACHELOR',
    course_year = CASE 
        WHEN enrollment_year IS NOT NULL THEN 
            LEAST(GREATEST(YEAR(NOW()) - enrollment_year + 1, 1), 5)
        ELSE 1 
    END;

-- You can manually update specific students to different education levels if needed
-- Example:
-- UPDATE students SET education_level = 'MASTER', course_year = 1 WHERE student_number IN ('M001', 'M002');
-- UPDATE students SET education_level = 'PHD', course_year = 1 WHERE student_number IN ('P001', 'P002');

-- Add education_level column to student_groups table
ALTER TABLE student_groups ADD COLUMN education_level VARCHAR(20);

-- Update existing groups with default education level based on course year
UPDATE student_groups SET education_level = 'BACHELOR' WHERE course_year >= 1 AND course_year <= 5;
UPDATE student_groups SET education_level = 'MASTER' WHERE course_year > 5 AND course_year <= 7;
UPDATE student_groups SET education_level = 'PHD' WHERE course_year > 7;

-- Set default for groups that might have edge cases
UPDATE student_groups SET education_level = 'BACHELOR' WHERE education_level IS NULL;

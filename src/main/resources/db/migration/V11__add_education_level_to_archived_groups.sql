-- Add education_level column to archived_student_groups table
ALTER TABLE archived_student_groups 
ADD COLUMN education_level VARCHAR(20);

-- Update existing records with default value (or NULL for now, can be updated manually if needed)
-- Since we can't retroactively determine the education level of already archived groups,
-- we'll leave them as NULL for now

-- Add grade_category column to grades table
-- Version 0.6.9

ALTER TABLE grades 
ADD COLUMN grade_category VARCHAR(100) AFTER grade_type;

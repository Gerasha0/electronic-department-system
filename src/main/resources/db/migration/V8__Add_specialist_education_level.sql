-- Add SPECIALIST to education_level enum
ALTER TABLE students MODIFY education_level ENUM('BACHELOR', 'SPECIALIST', 'MASTER', 'PHD');

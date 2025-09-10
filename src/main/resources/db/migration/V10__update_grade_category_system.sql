-- Update grade category system to use new enum structure
-- Version 0.7.0

-- First, let's add the new grade_category_enum column
ALTER TABLE grades ADD COLUMN grade_category_enum ENUM(
    'CURRENT_CONTROL', 
    'FINAL_CONTROL', 
    'RETAKE', 
    'MAKEUP'
) NOT NULL DEFAULT 'CURRENT_CONTROL' AFTER grade_type;

-- Update existing data to use new category system BEFORE changing the enum
-- Based on actual existing data: LABORATORY, SEMINAR, EXAM, CREDIT, RETAKE_EXAM, RETAKE_CREDIT, ADDITIONAL_TASK

-- Current control types
UPDATE grades SET grade_category_enum = 'CURRENT_CONTROL' 
WHERE grade_type IN ('LABORATORY', 'SEMINAR');

-- Final control types  
UPDATE grades SET grade_category_enum = 'FINAL_CONTROL'
WHERE grade_type IN ('EXAM', 'CREDIT');

-- Retake types
UPDATE grades SET grade_category_enum = 'RETAKE'
WHERE grade_type IN ('RETAKE_EXAM', 'RETAKE_CREDIT');

-- Makeup types
UPDATE grades SET grade_category_enum = 'MAKEUP'
WHERE grade_type IN ('ADDITIONAL_TASK');

-- Now migrate old grade_type values to new ones BEFORE changing the enum
-- Map existing values to new enum values
UPDATE grades SET grade_type = 'LABORATORY_WORK' WHERE grade_type = 'LABORATORY';
UPDATE grades SET grade_type = 'SEMINAR' WHERE grade_type = 'SEMINAR';  -- SEMINAR already matches
UPDATE grades SET grade_type = 'EXAM' WHERE grade_type = 'EXAM';        -- EXAM already matches  
UPDATE grades SET grade_type = 'CREDIT' WHERE grade_type = 'CREDIT';    -- CREDIT already matches
UPDATE grades SET grade_type = 'RETAKE_EXAM' WHERE grade_type = 'RETAKE_EXAM';       -- Already matches
UPDATE grades SET grade_type = 'RETAKE_CREDIT' WHERE grade_type = 'RETAKE_CREDIT';   -- Already matches
UPDATE grades SET grade_type = 'ADDITIONAL_TASK' WHERE grade_type = 'ADDITIONAL_TASK'; -- Already matches

-- Now we can safely update the enum since all data matches the new values
ALTER TABLE grades MODIFY grade_type ENUM(
    'LABORATORY_WORK',
    'PRACTICAL_WORK', 
    'SEMINAR',
    'CONTROL_WORK',
    'MODULE_WORK',
    'HOMEWORK',
    'INDIVIDUAL_WORK',
    'CURRENT_MAKEUP',
    'EXAM',
    'CREDIT',
    'DIFFERENTIATED_CREDIT',
    'COURSE_WORK',
    'QUALIFICATION_WORK',
    'STATE_EXAM',
    'ATTESTATION',
    'RETAKE_EXAM',
    'RETAKE_CREDIT',
    'RETAKE_WORK',
    'MAKEUP_LESSON',
    'MAKEUP_WORK',
    'ADDITIONAL_TASK'
) NOT NULL;

-- Drop the old grade_category column as it's no longer needed
ALTER TABLE grades DROP COLUMN grade_category;

-- Update archived_grades table as well
ALTER TABLE archived_grades ADD COLUMN grade_category_enum ENUM(
    'CURRENT_CONTROL', 
    'FINAL_CONTROL', 
    'RETAKE', 
    'MAKEUP'
) NOT NULL DEFAULT 'CURRENT_CONTROL' AFTER grade_type;

-- Update archived_grades data BEFORE changing the enum
-- Based on actual existing data: MODULE, PRACTICAL, CONTROL_WORK, EXAM, STATE_EXAM, RETAKE_WORK

-- Current control types
UPDATE archived_grades SET grade_category_enum = 'CURRENT_CONTROL' 
WHERE grade_type IN ('MODULE', 'PRACTICAL', 'CONTROL_WORK');

-- Final control types
UPDATE archived_grades SET grade_category_enum = 'FINAL_CONTROL'
WHERE grade_type IN ('EXAM', 'STATE_EXAM');

-- Retake types
UPDATE archived_grades SET grade_category_enum = 'RETAKE'
WHERE grade_type IN ('RETAKE_WORK');

-- Migrate old grade_type values in archived_grades BEFORE changing the enum
UPDATE archived_grades SET grade_type = 'MODULE_WORK' WHERE grade_type = 'MODULE';
UPDATE archived_grades SET grade_type = 'PRACTICAL_WORK' WHERE grade_type = 'PRACTICAL';
UPDATE archived_grades SET grade_type = 'CONTROL_WORK' WHERE grade_type = 'CONTROL_WORK';  -- Already matches
UPDATE archived_grades SET grade_type = 'EXAM' WHERE grade_type = 'EXAM';                -- Already matches
UPDATE archived_grades SET grade_type = 'STATE_EXAM' WHERE grade_type = 'STATE_EXAM';    -- Already matches
UPDATE archived_grades SET grade_type = 'RETAKE_WORK' WHERE grade_type = 'RETAKE_WORK';  -- Already matches

-- Now we can safely update archived_grades grade_type enum
ALTER TABLE archived_grades MODIFY grade_type ENUM(
    'LABORATORY_WORK',
    'PRACTICAL_WORK', 
    'SEMINAR',
    'CONTROL_WORK',
    'MODULE_WORK',
    'HOMEWORK',
    'INDIVIDUAL_WORK',
    'CURRENT_MAKEUP',
    'EXAM',
    'CREDIT',
    'DIFFERENTIATED_CREDIT',
    'COURSE_WORK',
    'QUALIFICATION_WORK',
    'STATE_EXAM',
    'ATTESTATION',
    'RETAKE_EXAM',
    'RETAKE_CREDIT',
    'RETAKE_WORK',
    'MAKEUP_LESSON',
    'MAKEUP_WORK',
    'ADDITIONAL_TASK'
) NOT NULL;

-- Drop the old grade_category column from archived_grades
ALTER TABLE archived_grades DROP COLUMN grade_category;

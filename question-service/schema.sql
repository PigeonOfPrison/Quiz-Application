-- ============================================
-- Question Service Database Schema
-- Database: questiondb
-- ============================================

-- Drop existing types and tables if they exist (optional, uncomment if needed)
-- DROP TABLE IF EXISTS question CASCADE;
-- DROP TYPE IF EXISTS difficulty CASCADE;

-- ============================================
-- Custom Type: difficulty
-- Description: Enum for question difficulty levels
-- ============================================
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'difficulty') THEN
        CREATE TYPE difficulty AS ENUM ('easy', 'medium', 'hard', 'insane');
    END IF;
END $$;

-- ============================================
-- Create CAST for varchar to difficulty conversion
-- This allows implicit casting from varchar to difficulty enum
-- Required for JPA/Hibernate compatibility
-- ============================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_cast 
        WHERE castsource = 'varchar'::regtype 
        AND casttarget = 'difficulty'::regtype
    ) THEN
        CREATE CAST (varchar AS difficulty) WITH INOUT AS IMPLICIT;
    END IF;
END $$;

-- ============================================
-- Table: question
-- Description: Stores questions with multiple choice options
-- ============================================
CREATE TABLE IF NOT EXISTS question (
    id SERIAL PRIMARY KEY,
    category VARCHAR(45),
    difficulty_level difficulty NOT NULL,
    question_title TEXT NOT NULL,
    option1 VARCHAR(255) NOT NULL,
    option2 VARCHAR(255) NOT NULL,
    option3 VARCHAR(255) NOT NULL,
    option4 VARCHAR(255) NOT NULL,
    answer VARCHAR(255) NOT NULL,
    hint TEXT,
    CONSTRAINT question_title_not_empty CHECK (question_title <> ''),
    CONSTRAINT question_option1_not_empty CHECK (option1 <> ''),
    CONSTRAINT question_option2_not_empty CHECK (option2 <> ''),
    CONSTRAINT question_option3_not_empty CHECK (option3 <> ''),
    CONSTRAINT question_option4_not_empty CHECK (option4 <> ''),
    CONSTRAINT question_answer_not_empty CHECK (answer <> '')
);

-- ============================================
-- Indexes for better query performance
-- ============================================
CREATE INDEX IF NOT EXISTS idx_question_category ON question(category);
CREATE INDEX IF NOT EXISTS idx_question_difficulty ON question(difficulty_level);
CREATE INDEX IF NOT EXISTS idx_question_category_difficulty ON question(category, difficulty_level);

-- ============================================
-- Sample Data (Optional - uncomment to insert)
-- ============================================
/*
INSERT INTO question (category, difficulty_level, question_title, option1, option2, option3, option4, answer, hint) VALUES
    ('Java', 'easy', 'What is the default value of a boolean variable in Java?', 'true', 'false', 'null', '0', 'false', 'Think about primitive types'),
    ('Java', 'medium', 'Which keyword is used to prevent inheritance in Java?', 'static', 'final', 'private', 'protected', 'final', 'This keyword has multiple uses'),
    ('Java', 'hard', 'What is the time complexity of HashMap get() operation?', 'O(1)', 'O(n)', 'O(log n)', 'O(n²)', 'O(1)', 'Average case scenario'),
    ('Spring Boot', 'easy', 'Which annotation is used to mark a class as a REST controller?', '@Controller', '@RestController', '@Service', '@Component', '@RestController', 'Combines @Controller and @ResponseBody'),
    ('Spring Boot', 'medium', 'What is the default port for Spring Boot applications?', '8080', '8000', '3000', '9090', '8080', 'Most common development port'),
    ('Database', 'easy', 'What does SQL stand for?', 'Structured Query Language', 'Simple Query Language', 'Standard Query Language', 'Structured Question Language', 'Structured Query Language', 'Standard database language'),
    ('Database', 'hard', 'Which normal form eliminates transitive dependencies?', '1NF', '2NF', '3NF', 'BCNF', '3NF', 'Think about indirect dependencies'),
    ('General', 'easy', 'What does HTTP stand for?', 'HyperText Transfer Protocol', 'High Transfer Text Protocol', 'HyperText Transmission Protocol', 'High Text Transfer Protocol', 'HyperText Transfer Protocol', 'Web protocol'),
    ('General', 'medium', 'Which design pattern ensures only one instance of a class exists?', 'Factory', 'Singleton', 'Builder', 'Prototype', 'Singleton', 'One instance globally'),
    ('General', 'insane', 'What is the maximum value of a signed 32-bit integer?', '2147483647', '4294967295', '2147483648', '4294967296', '2147483647', '2^31 - 1');
*/

-- ============================================
-- Comments on Tables and Columns
-- ============================================
COMMENT ON TABLE question IS 'Stores quiz questions with multiple choice options and answers';
COMMENT ON TYPE difficulty IS 'Enumeration for question difficulty levels: easy, medium, hard, insane';

COMMENT ON COLUMN question.id IS 'Auto-generated primary key';
COMMENT ON COLUMN question.category IS 'Category of the question (e.g., Java, Spring Boot, Database)';
COMMENT ON COLUMN question.difficulty_level IS 'Difficulty level of the question';
COMMENT ON COLUMN question.question_title IS 'The actual question text';
COMMENT ON COLUMN question.option1 IS 'First multiple choice option';
COMMENT ON COLUMN question.option2 IS 'Second multiple choice option';
COMMENT ON COLUMN question.option3 IS 'Third multiple choice option';
COMMENT ON COLUMN question.option4 IS 'Fourth multiple choice option';
COMMENT ON COLUMN question.answer IS 'Correct answer to the question';
COMMENT ON COLUMN question.hint IS 'Optional hint to help answer the question';

-- ============================================
-- Useful Queries
-- ============================================

-- Get questions by category and difficulty
-- SELECT * FROM question WHERE category = 'Java' AND difficulty_level = 'medium';

-- Get random questions for a quiz
-- SELECT * FROM question WHERE category = 'Java' ORDER BY RANDOM() LIMIT 10;

-- Count questions by difficulty
-- SELECT difficulty_level, COUNT(*) as count FROM question GROUP BY difficulty_level;

-- Count questions by category
-- SELECT category, COUNT(*) as count FROM question GROUP BY category;

-- ============================================
-- Quiz Service Database Schema
-- Database: quizdb
-- ============================================

-- Drop existing tables if they exist (optional, uncomment if needed)
-- DROP TABLE IF EXISTS quiz_question_ids CASCADE;
-- DROP TABLE IF EXISTS quiz CASCADE;

-- ============================================
-- Table: quiz
-- Description: Stores quiz metadata
-- ============================================
CREATE TABLE IF NOT EXISTS quiz (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    number_of_questions INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT quiz_title_not_empty CHECK (title <> ''),
    CONSTRAINT quiz_number_positive CHECK (number_of_questions >= 0)
);

-- ============================================
-- Table: quiz_question_ids
-- Description: Stores the list of question IDs associated with each quiz
-- This is an ElementCollection mapping from the Quiz entity
-- ============================================
CREATE TABLE IF NOT EXISTS quiz_question_ids (
    quiz_id INTEGER NOT NULL,
    question_ids INTEGER NOT NULL,
    CONSTRAINT fk_quiz_question_ids 
        FOREIGN KEY (quiz_id) 
        REFERENCES quiz(id) 
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- ============================================
-- Indexes for better query performance
-- ============================================
CREATE INDEX IF NOT EXISTS idx_quiz_category ON quiz(category);
CREATE INDEX IF NOT EXISTS idx_quiz_title ON quiz(title);
CREATE INDEX IF NOT EXISTS idx_quiz_question_ids_quiz_id ON quiz_question_ids(quiz_id);
CREATE INDEX IF NOT EXISTS idx_quiz_question_ids_question_id ON quiz_question_ids(question_ids);

-- ============================================
-- Sample Data (Optional - uncomment to insert)
-- ============================================
/*
INSERT INTO quiz (title, category, number_of_questions) VALUES
    ('Java Basics', 'Programming', 10),
    ('Spring Boot Fundamentals', 'Programming', 15),
    ('General Knowledge', 'General', 20),
    ('Mathematics', 'Math', 12);

-- Sample question IDs for the quizzes
INSERT INTO quiz_question_ids (quiz_id, question_ids) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
    (2, 6), (2, 7), (2, 8), (2, 9), (2, 10);
*/

-- ============================================
-- Comments on Tables
-- ============================================
COMMENT ON TABLE quiz IS 'Stores quiz metadata including title, category, and number of questions';
COMMENT ON TABLE quiz_question_ids IS 'Junction table storing question IDs associated with each quiz';

COMMENT ON COLUMN quiz.id IS 'Auto-generated primary key';
COMMENT ON COLUMN quiz.title IS 'Title of the quiz';
COMMENT ON COLUMN quiz.category IS 'Category of the quiz (e.g., Programming, Math, General)';
COMMENT ON COLUMN quiz.number_of_questions IS 'Total number of questions in the quiz';

COMMENT ON COLUMN quiz_question_ids.quiz_id IS 'Foreign key referencing quiz.id';
COMMENT ON COLUMN quiz_question_ids.question_ids IS 'ID of a question from the question-service';

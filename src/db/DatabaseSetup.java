package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void setupDatabase() {
        String createCategoriesTable = """
            CREATE TABLE IF NOT EXISTS categories (
                category_id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL UNIQUE,
                description TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String createQuizzesTable = """
            CREATE TABLE IF NOT EXISTS quizzes (
                quiz_id SERIAL PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                description TEXT,
                category_id INT REFERENCES categories(category_id),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String createQuestionsTable = """
            CREATE TABLE IF NOT EXISTS questions (
                question_id SERIAL PRIMARY KEY,
                quiz_id INT NOT NULL REFERENCES quizzes(quiz_id) ON DELETE CASCADE,
                question_text TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String createAnswersTable = """
            CREATE TABLE IF NOT EXISTS answers (
                answer_id SERIAL PRIMARY KEY,
                question_id INT NOT NULL REFERENCES questions(question_id) ON DELETE CASCADE,
                answer_text TEXT NOT NULL,
                is_correct BOOLEAN NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                user_id SERIAL PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                email VARCHAR(100) NOT NULL UNIQUE,
                role VARCHAR(50) DEFAULT 'user',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String createUserAttemptsTable = """
            CREATE TABLE IF NOT EXISTS user_attempts (
                attempt_id SERIAL PRIMARY KEY,
                user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                quiz_id INT NOT NULL REFERENCES quizzes(quiz_id) ON DELETE CASCADE,
                score INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(createCategoriesTable);
            statement.executeUpdate(createQuizzesTable);
            statement.executeUpdate(createQuestionsTable);
            statement.executeUpdate(createAnswersTable);
            statement.executeUpdate(createUsersTable);
            statement.executeUpdate(createUserAttemptsTable);

            System.out.println("All tables created successfully (or already exist).");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertSampleData() {
        String insertCategories = """
            INSERT INTO categories (name, description)
            VALUES
                ('Programming', 'Quizzes related to programming topics'),
                ('General Knowledge', 'Various general knowledge quizzes')
            ON CONFLICT (name) DO NOTHING;
        """;

        String insertQuizzes = """
            INSERT INTO quizzes (title, description, category_id)
            VALUES
                ('Java Basics', 'A quiz about basic Java concepts', 1),
                ('SQL Fundamentals', 'Test your knowledge of SQL concepts', 1);
        """;
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(insertCategories);
            statement.executeUpdate(insertQuizzes);

            System.out.println("Sample categories and quizzes inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package dao;

import db.DatabaseConnection;
import models.FullQuizDescription;
import models.Question;
import models.Quiz;
import models.Answer;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizDAO {
    public static int addQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes (title, description, category_id) VALUES (?, ?, ?) RETURNING quiz_id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getDescription());
            // If a category is assigned (nonzero), set it; otherwise, set NULL.
            if (quiz.getCategoryId() != 0) {
                stmt.setInt(3, quiz.getCategoryId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int generatedId = rs.getInt("quiz_id");
                    quiz.setQuizId(generatedId);
                    System.out.println("Quiz inserted with ID: " + generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Quiz getQuizById(int quizId) {
        String sql = "SELECT quiz_id, title, description, category_id FROM quizzes WHERE quiz_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Quiz quiz = new Quiz(
                            rs.getInt("quiz_id"),
                            rs.getString("title"),
                            rs.getString("description")
                    );
                    quiz.setCategoryId(rs.getInt("category_id"));
                    return quiz;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT quiz_id, title, description, category_id FROM quizzes";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Quiz quiz = new Quiz(
                        rs.getInt("quiz_id"),
                        rs.getString("title"),
                        rs.getString("description")
                );
                quiz.setCategoryId(rs.getInt("category_id"));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    public static boolean updateQuiz(Quiz quiz) {
        String sql = "UPDATE quizzes SET title = ?, description = ?, category_id = ? WHERE quiz_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getDescription());
            if (quiz.getCategoryId() != 0) {
                stmt.setInt(3, quiz.getCategoryId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setInt(4, quiz.getQuizId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteQuiz(int quizId) {
        String sql = "DELETE FROM quizzes WHERE quiz_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, quizId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Demonstrates JOIN-like aggregation: retrieves a full quiz description (quiz info, questions, and answers)
    public static FullQuizDescription getFullQuizDescription(int quizId) {
        Quiz quiz = getQuizById(quizId);
        if (quiz == null) return null;

        List<Question> questions = QuestionDAO.getQuestionsByQuiz(quizId);
        Map<Integer, List<Answer>> questionAnswers = new HashMap<>();

        // Lambda expression to process each question and retrieve its answers
        questions.forEach(question -> {
            List<Answer> answers = AnswerDAO.getAnswersByQuestion(question.getQuestionId());
            questionAnswers.put(question.getQuestionId(), answers);
        });

        return new FullQuizDescription(quiz, questions, questionAnswers);
    }
}


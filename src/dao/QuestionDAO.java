package dao;

import db.DatabaseConnection;
import models.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {
    public static int addQuestion(Question question) {
        String sql = "INSERT INTO questions (quiz_id, question_text) VALUES (?, ?) RETURNING question_id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, question.getQuizId());
            stmt.setString(2, question.getQuestionText());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int generatedId = rs.getInt("question_id");
                    question.setQuestionId(generatedId);
                    System.out.println("Question inserted with ID: " + generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<Question> getQuestionsByQuiz(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT question_id, quiz_id, question_text FROM questions WHERE quiz_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(new Question(
                            rs.getInt("question_id"),
                            rs.getInt("quiz_id"),
                            rs.getString("question_text")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
}

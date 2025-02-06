package dao;

import db.DatabaseConnection;
import models.Answer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class AnswerDAO {
    public static int addAnswer(Answer answer) {
        String sql = "INSERT INTO answers (question_id, answer_text, is_correct) VALUES (?, ?, ?) RETURNING answer_id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, answer.getQuestionId());
            stmt.setString(2, answer.getAnswerText());
            stmt.setBoolean(3, answer.isCorrect());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int generatedId = rs.getInt("answer_id");
                    answer.setAnswerId(generatedId);
                    System.out.println("Answer inserted with ID: " + generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static List<Answer> getAnswersByQuestion(int questionId) {
        List<Answer> answers = new ArrayList<>();
        String sql = "SELECT answer_id, question_id, answer_text, is_correct FROM answers WHERE question_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    answers.add(new Answer(
                            rs.getInt("answer_id"),
                            rs.getInt("question_id"),
                            rs.getString("answer_text"),
                            rs.getBoolean("is_correct")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }
    public static boolean updateAnswer(Answer answer) {
        String sql = "UPDATE answers SET answer_text = ?, is_correct = ? WHERE answer_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, answer.getAnswerText());
            stmt.setBoolean(2, answer.isCorrect());
            stmt.setInt(3, answer.getAnswerId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean deleteAnswer(int answerId) {
        String sql = "DELETE FROM answers WHERE answer_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, answerId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

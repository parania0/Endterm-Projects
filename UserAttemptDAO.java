package dao;

import db.DatabaseConnection;
import models.UserAttempt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class UserAttemptDAO {
    public static int addAttempt(UserAttempt attempt) {
        String sql = "INSERT INTO user_attempts (user_id, quiz_id, score) VALUES (?, ?, ?) RETURNING attempt_id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, attempt.getUserId());
            stmt.setInt(2, attempt.getQuizId());
            stmt.setInt(3, attempt.getScore());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int generatedId = rs.getInt("attempt_id");
                    attempt.setAttemptId(generatedId);
                    System.out.println("User attempt inserted with ID: " + generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static List<UserAttempt> getAttemptsByUser(int userId) {
        List<UserAttempt> attempts = new ArrayList<>();
        String sql = "SELECT attempt_id, user_id, quiz_id, score FROM user_attempts WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(new UserAttempt(
                            rs.getInt("attempt_id"),
                            rs.getInt("user_id"),
                            rs.getInt("quiz_id"),
                            rs.getInt("score")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attempts;
    }
    public static List<UserAttempt> getAttemptsByQuiz(int quizId) {
        List<UserAttempt> attempts = new ArrayList<>();
        String sql = "SELECT attempt_id, user_id, quiz_id, score FROM user_attempts WHERE quiz_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(new UserAttempt(
                            rs.getInt("attempt_id"),
                            rs.getInt("user_id"),
                            rs.getInt("quiz_id"),
                            rs.getInt("score")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attempts;
    }
    public static boolean updateAttemptScore(int attemptId, int newScore) {
        String sql = "UPDATE user_attempts SET score = ? WHERE attempt_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, newScore);
            stmt.setInt(2, attemptId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean deleteAttempt(int attemptId) {
        String sql = "DELETE FROM user_attempts WHERE attempt_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, attemptId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

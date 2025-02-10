package dao;

import db.DatabaseConnection;
import models.Quiz;
import models.Question;
import models.Answer;
import models.FullQuizDescription;

import java.sql.*;
import java.util.*;

public class QuizDAO {
    public static List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT quiz_id, title, description FROM quizzes";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                quizzes.add(new Quiz(
                        rs.getInt("quiz_id"),
                        rs.getString("title"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    public static FullQuizDescription getFullQuizDescription(int quizId) {
        Quiz quiz = getQuizById(quizId);
        if (quiz == null) return null;

        List<Question> questions = QuestionDAO.getQuestionsByQuiz(quizId);
        Map<Integer, List<Answer>> questionAnswers = new HashMap<>();

        questions.forEach(question ->
                questionAnswers.put(question.getQuestionId(),
                        AnswerDAO.getAnswersByQuestion(question.getQuestionId()))
        );

        return new FullQuizDescription(quiz, questions, questionAnswers);
    }

    public static Quiz getQuizById(int quizId) {
        String sql = "SELECT quiz_id, title, description FROM quizzes WHERE quiz_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Quiz(rs.getInt("quiz_id"), rs.getString("title"), rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

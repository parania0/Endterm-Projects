package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Quiz {
    private String question;
    private List<String> options;
    private int correctAnswer;

    public Quiz(String question) {
        this.question = question;
        this.options = new ArrayList<>();
    }

    public void addOption(String option) {
        this.options.add(option);
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void saveToDatabase(Connection connection) throws SQLException {
        String sql = "INSERT INTO quizzes (question, options, correct_answer) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, question);
            statement.setString(2, String.join(",", options));
            statement.setInt(3, correctAnswer);
            statement.executeUpdate();
        }
    }

}

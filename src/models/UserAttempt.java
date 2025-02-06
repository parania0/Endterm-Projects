package models;

public class UserAttempt {
    private int attemptId;
    private int userId;
    private int quizId;
    private int score;

    public UserAttempt(int attemptId, int userId, int quizId, int score) {
        this.attemptId = attemptId;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
    }

    public UserAttempt(int userId, int quizId, int score) {
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
    }
    public int getAttemptId() {
        return attemptId;
    }
    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuizId() {
        return quizId;
    }
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
}

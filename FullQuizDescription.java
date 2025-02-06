package models;

import java.util.List;
import java.util.Map;

public class FullQuizDescription {
    private Quiz quiz;
    private List<Question> questions;
    private Map<Integer, List<Answer>> questionAnswers;

    public FullQuizDescription(Quiz quiz, List<Question> questions, Map<Integer, List<Answer>> questionAnswers) {
        this.quiz = quiz;
        this.questions = questions;
        this.questionAnswers = questionAnswers;
    }

    public Quiz getQuiz() {
        return quiz;
    }
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
    public List<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    public Map<Integer, List<Answer>> getQuestionAnswers() {
        return questionAnswers;
    }
    public void setQuestionAnswers(Map<Integer, List<Answer>> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }
}

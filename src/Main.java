import models.DataBaseConnection;
import models.Quiz;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Создание теста. Введите вопросы и варианты ответов (Что бы завершить создание вопросов, напишите exit):");

        try (Connection connection = DataBaseConnection.getConnection()) {
            while (true) {
                System.out.print("Вопрос: ");
                String question = scanner.nextLine();
                if (question.equals("exit")) {
                    break;
                }

                Quiz quiz = new Quiz(question);
                for (int i = 1; i <= 4; i++) {
                    System.out.print("Вариант " + i + ": ");
                    quiz.addOption(scanner.nextLine());
                }

                System.out.print("Номер правильного ответа: ");
                int correctAnswer = scanner.nextInt();
                quiz.setCorrectAnswer(correctAnswer);
                scanner.nextLine();

                quiz.saveToDatabase(connection);
                System.out.println("Вопрос сохранен");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

import dao.*;
import db.DatabaseSetup;
import models.*;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("Setting up the database...");
        DatabaseSetup.setupDatabase();
        DatabaseSetup.insertSampleData();

        while (true) {
            printMenu();
            String input = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
                continue;
            }
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    listQuizzes();
                    break;
                case 4:
                    takeQuiz();
                    break;
                case 5:
                    addQuestionAndAnswers();
                    break;
                case 6:
                    viewFullQuizDescription();
                    break;
                case 7:
                    addCategory();
                    break;
                case 8:
                    exit();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Quiz Application Menu ---");
        System.out.println("1. Register User");
        System.out.println("2. Login");
        System.out.println("3. List Quizzes");
        System.out.println("4. Take Quiz");
        System.out.println("5. Add Question and Answers (Admin/Manager only)");
        System.out.println("6. View Full Quiz Description (JOIN query demonstration)");
        System.out.println("7. Add Category");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }

    private static void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        System.out.print("Enter role (user/admin/manager/editor) [default: user]: ");
        String role = scanner.nextLine().trim();
        if (role.isEmpty()) {
            role = "user";
        }

        User user = new User(username, email);
        user.setRole(role);

        int userId = UserDAO.addUser(user);
        if (userId != -1) {
            System.out.println("User registered successfully with ID: " + userId);
            currentUser = user;
        } else {
            System.out.println("User registration failed.");
        }
    }


    private static void loginUser() {
        List<User> users = UserDAO.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users registered yet. Please register first.");
            return;
        }
        System.out.println("Registered Users:");
        users.forEach(user -> System.out.println(user.getUserId() + ": " + user.getUsername() +
                " (" + user.getEmail() + ") Role: " + user.getRole()));
        System.out.print("Enter the user ID to login: ");
        try {
            int userId = Integer.parseInt(scanner.nextLine().trim());
            User user = UserDAO.getUserById(userId);
            if (user != null) {
                currentUser = user;
                System.out.println("Logged in as " + user.getUsername());
            } else {
                System.out.println("Invalid user ID.");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void listQuizzes() {
        List<Quiz> quizzes = QuizDAO.getAllQuizzes();
        if (quizzes.isEmpty()) {
            System.out.println("No quizzes available.");
            return;
        }
        System.out.println("Available Quizzes:");
        quizzes.forEach(quiz -> System.out.println(quiz.getQuizId() + ": " + quiz.getTitle() +
                " - " + quiz.getDescription() + " (Category ID: " + quiz.getCategoryId() + ")"));
    }

    private static void takeQuiz() {
        if (currentUser == null) {
            System.out.println("Please login or register before taking a quiz.");
            return;
        }
        listQuizzes();
        System.out.print("Enter quiz ID to take: ");
        int quizId;
        try {
            quizId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ex) {
            System.out.println("Invalid quiz ID.");
            return;
        }

        List<Question> questions = QuestionDAO.getQuestionsByQuiz(quizId);
        if (questions.isEmpty()) {
            System.out.println("No questions found for this quiz.");
            return;
        }

        int score = 0;
        for (Question question : questions) {
            System.out.println("\nQuestion: " + question.getQuestionText());
            List<Answer> answers = AnswerDAO.getAnswersByQuestion(question.getQuestionId());
            if (answers.isEmpty()) {
                System.out.println("No answers available for this question.");
                continue;
            }
            for (int i = 0; i < answers.size(); i++) {
                System.out.println((i + 1) + ": " + answers.get(i).getAnswerText());
            }
            System.out.print("Enter your answer (number): ");
            int answerChoice;
            try {
                answerChoice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Skipping this question.");
                continue;
            }
            if (answerChoice < 1 || answerChoice > answers.size()) {
                System.out.println("Choice out of range. Skipping this question.");
                continue;
            }
            Answer selectedAnswer = answers.get(answerChoice - 1);
            if (selectedAnswer.isCorrect()) {
                System.out.println("Correct!");
                score++;
            } else {
                System.out.println("Incorrect.");
            }
        }
        System.out.println("\nQuiz finished. Your score: " + score + " out of " + questions.size());
        UserAttempt attempt = new UserAttempt(currentUser.getUserId(), quizId, score);
        int attemptId = UserAttemptDAO.addAttempt(attempt);
        if (attemptId != -1) {
            System.out.println("Your attempt has been recorded with attempt ID: " + attemptId);
        } else {
            System.out.println("Failed to record your attempt.");
        }
    }

    private static void addQuestionAndAnswers() {
        if (currentUser == null) {
            System.out.println("Please login first.");
            return;
        }
        if (!(currentUser.getRole().equalsIgnoreCase("admin") || currentUser.getRole().equalsIgnoreCase("manager"))) {
            System.out.println("You do not have permission to add questions.");
            return;
        }
        listQuizzes();
        System.out.print("Enter quiz ID to add a question to: ");
        int quizId;
        try {
            quizId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ex) {
            System.out.println("Invalid quiz ID.");
            return;
        }
        System.out.print("Enter the question text: ");
        String questionText = scanner.nextLine().trim();

        Question question = new Question(quizId, questionText);
        int questionId = QuestionDAO.addQuestion(question);
        if (questionId == -1) {
            System.out.println("Failed to add the question.");
            return;
        }
        System.out.print("How many answers does this question have? ");
        int numAnswers;
        try {
            numAnswers = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number.");
            return;
        }
        for (int i = 0; i < numAnswers; i++) {
            System.out.print("Enter text for answer " + (i + 1) + ": ");
            String answerText = scanner.nextLine().trim();
            System.out.print("Is this answer correct? (true/false): ");
            boolean isCorrect = Boolean.parseBoolean(scanner.nextLine().trim());
            Answer answer = new Answer(questionId, answerText, isCorrect);
            int answerId = AnswerDAO.addAnswer(answer);
            if (answerId == -1) {
                System.out.println("Failed to add answer " + (i + 1) + ".");
            }
        }
        System.out.println("Question and its answers added successfully.");
    }

    private static void viewFullQuizDescription() {
        System.out.print("Enter quiz ID to view full description: ");
        int quizId;
        try {
            quizId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ex) {
            System.out.println("Invalid quiz ID.");
            return;
        }
        FullQuizDescription fullQuiz = QuizDAO.getFullQuizDescription(quizId);
        if (fullQuiz == null) {
            System.out.println("Quiz not found or no details available.");
            return;
        }
        System.out.println("\n--- Full Quiz Description ---");
        System.out.println("Quiz: " + fullQuiz.getQuiz().getTitle());
        System.out.println("Description: " + fullQuiz.getQuiz().getDescription());
        System.out.println("Questions and Answers:");
        fullQuiz.getQuestions().forEach(question -> {
            System.out.println("Q: " + question.getQuestionText());
            List<Answer> answers = fullQuiz.getQuestionAnswers().get(question.getQuestionId());
            if (answers != null) {
                answers.forEach(answer -> System.out.println("   - " + answer.getAnswerText() +
                        (answer.isCorrect() ? " (Correct)" : "")));
            }
        });
    }

    private static void addCategory() {
        System.out.print("Enter category name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter category description: ");
        String description = scanner.nextLine().trim();
        Category category = new Category(name, description);
        int categoryId = CategoryDAO.addCategory(category);
        if (categoryId != -1) {
            System.out.println("Category added with ID: " + categoryId);
        } else {
            System.out.println("Failed to add category.");
        }
    }

    private static void exit() {
        System.out.println("Exiting application. Goodbye!");
        System.exit(0);
    }
}

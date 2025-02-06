package models;

public class User {
    private int userId;
    private String username;
    private String email;
    private String role;

    public User(int userId, String username, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public User(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public boolean isAdmin() { return role.equalsIgnoreCase("admin"); }
    public boolean isManager() { return role.equalsIgnoreCase("manager"); }
}


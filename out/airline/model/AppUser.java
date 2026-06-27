package airline.model;

public class AppUser {
    private final int id;
    private final String fullName;
    private final String username;
    private final String role;

    public AppUser(int id, String fullName, String username, String role) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}

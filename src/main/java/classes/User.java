package classes;

import java.util.UUID;

public class User {
    private final String id;
    private String name;
    private String email;
    private boolean admin;
    private String password;

    public User() {
        id = UUID.randomUUID().toString();
    }

    public User(String name, String email, boolean admin, String password) {
        this();
        this.name = name;
        this.email = email;
        this.admin = admin;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}

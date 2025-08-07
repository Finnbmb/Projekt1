package de.swtp1.terminkalender.dto;

import de.swtp1.terminkalender.entity.User;

/**
 * DTO für Login-Antworten
 */
public class AuthResponseDto {

    private String token;
    private String type = "Bearer";
    private UserDto user;
    private String message;

    // Konstruktoren
    public AuthResponseDto() {
    }

    public AuthResponseDto(String token, UserDto user) {
        this.token = token;
        this.user = user;
        this.message = "Login erfolgreich";
    }

    public AuthResponseDto(String token, UserDto user, String message) {
        this.token = token;
        this.user = user;
        this.message = message;
    }

    // Getter und Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Vereinfachter User DTO ohne sensible Daten
     */
    public static class UserDto {
        private Long id;
        private String username;
        private String email;
        private String name;
        private User.UserRole role;
        private String federalState;

        public UserDto() {
        }

        public UserDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.name = user.getName();
            this.role = user.getRole();
            this.federalState = user.getFederalState();
        }

        // Getter und Setter
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public User.UserRole getRole() { return role; }
        public void setRole(User.UserRole role) { this.role = role; }

        public String getFederalState() { return federalState; }
        public void setFederalState(String federalState) { this.federalState = federalState; }
    }
}

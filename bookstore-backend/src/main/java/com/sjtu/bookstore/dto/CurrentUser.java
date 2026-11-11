package com.sjtu.bookstore.dto;

public class CurrentUser {

    private final Long id;
    private final String username;
    private final String role;
    private final Boolean enabled;

    public CurrentUser(Long id, String username, String role, Boolean enabled) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public Boolean getEnabled() {
        return enabled;
    }
}

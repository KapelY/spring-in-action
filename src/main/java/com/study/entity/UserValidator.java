package com.study.entity;

import java.util.Objects;

public record UserValidator(User user) {
    public boolean isValid() {
        return !Objects.equals(user, null)
                && user.getEmail() != null
                && user.id != null
                && user.passwordHash != null;
    }
}

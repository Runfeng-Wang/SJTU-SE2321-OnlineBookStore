package com.sjtu.bookstore.dto;

import jakarta.validation.constraints.NotNull;

public class UserStatusRequest {

    @NotNull(message = "用户状态不能为空")
    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}

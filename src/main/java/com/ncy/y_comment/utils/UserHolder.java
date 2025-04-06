package com.ncy.y_comment.utils;

import com.ncy.y_comment.dto.UserDTO;

public class UserHolder {
    private static final ThreadLocal<UserDTO> t1 = new ThreadLocal<>();

    public static UserDTO getUser() {
        return t1.get();
    }
    public static void setUser(UserDTO user) {
        t1.set(user);
    }
    public static void removeUser() {
        t1.remove();
    }

}

package com.zorvyn.finance.util;

public class Constants {
    
    public static final String JWT_SECRET = "ZorvynFinanceSecretKeyThatisLongEnoughToBeSecure";
    public static final long JWT_EXPIRATION = 86400000; // 1 day
    public static final String BEARER = "Bearer ";
    public static final String AUTH_HEADER = "Authorization";

    // Roles
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_ANALYST = "ROLE_ANALYST";
    public static final String ROLE_VIEWER = "ROLE_VIEWER";

}

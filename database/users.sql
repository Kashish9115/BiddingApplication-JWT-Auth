CREATE DATABASE IF NOT EXISTS bid;
USE bid;

CREATE TABLE IF NOT EXISTS users (
    user_id INT NOT NULL AUTO_INCREMENT,
    full_name LONGTEXT NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash LONGTEXT NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_first_time_login BIT(1) NOT NULL DEFAULT 1,
    secret_key LONGTEXT NULL,
    is_two_factor_enabled BIT(1) NOT NULL DEFAULT 0,

    access_token_hash VARCHAR(2000) NULL,
    access_token_expiry DATETIME(6) NULL,

    refresh_token_hash VARCHAR(2000) NULL,
    refresh_token_expiry DATETIME(6) NULL,

    email_otp_hash VARCHAR(255) NULL,
    email_otp_expiry DATETIME(6) NULL,

    is_deleted BIT(1) NOT NULL DEFAULT 0,
    deleted_at DATETIME(6) NULL,
    is_token_revoked BIT(1) NOT NULL DEFAULT 0,

    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE users
    MODIFY access_token_hash VARCHAR(2000) NULL,
    MODIFY refresh_token_hash VARCHAR(2000) NULL;

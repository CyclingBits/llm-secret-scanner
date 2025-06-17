-- SQL Dump for demonstration purposes.
-- This file contains various examples of how secrets can be stored in .sql files.
-- A secret token found in the header comment: sql-header-token-1a2b3c

-- 1. Hardcoded password in a CREATE TABLE statement (very bad practice).
CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       temp_password VARCHAR(100) DEFAULT 'default_temporary_password123'
);

-- 2. Inserting plaintext credentials directly.
INSERT INTO users (username, password_hash, temp_password) VALUES
                                                               ('admin', 'plain_text_not_a_hash_password!', 'should_not_be_here'),
                                                               ('service_account', '5f4dcc3b5aa765d61d8327deb882cf99', 'some_other_secret'); -- api_key=api-key-in-inline-comment

/*
 3. Multi-line comment block with credentials for a different system.
 Connection details for reporting DB:
 Host: reporting.db.internal
 User: report_user
 Pass: ReportingDB_P@ssw0rd
*/

-- 4. An update statement with an old password left in a comment.
-- UPDATE users SET password_hash = 'new_strong_hash' WHERE username = 'admin'; -- Old hash was 'plain_text_not_a_hash_password!'

-- 5. Stored procedure/function with a hardcoded secret.
DELIMITER $$
CREATE FUNCTION get_api_access_key(user_id INT)
    RETURNS VARCHAR(255)
    DETERMINISTIC
BEGIN
    -- This is a very insecure way to handle keys.
    DECLARE secret_key VARCHAR(255);
    SET secret_key = 'hardcoded-secret-key-in-stored-procedure';
RETURN secret_key;
END$$
DELIMITER ;

-- 6. Storing a Base64 encoded secret.
CREATE TABLE app_config (
                            config_key VARCHAR(100) PRIMARY KEY,
                            config_value TEXT
);
INSERT INTO app_config (config_key, 'config_value') VALUES ('jwt.secret.base64', 'c3FsaXplX3NlY3JldF9iYXNlNjQ='); -- "sqlize_secret_base64"

-- 7. False positive: A value that looks like a key but is a legitimate ID.
INSERT INTO logs (correlation_id, message) VALUES ('d4e2decd-a65c-48a5-9688-66eda7e4a3e3', 'User logged in successfully');

-- 8. A commented-out backup command that includes a password.
-- mysqldump -u root -p'root_password_in_backup_command' --all-databases > /mnt/backups/full_backup.sql


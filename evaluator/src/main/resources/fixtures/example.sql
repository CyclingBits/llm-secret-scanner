-- sql-header-token-1a2b3c

CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       temp_password VARCHAR(100) DEFAULT 'default_temporary_password123'
);

INSERT INTO users (username, password_hash, temp_password) VALUES
                                                               ('admin', 'plain_text_not_a_hash_password!', 'should_not_be_here'),
                                                               ('service_account', '5f4dcc3b5aa765d61d8327deb882cf99', 'some_other_secret'); -- api_key=api-key-in-inline-comment

/*
 Connection details for reporting DB:
 Host: reporting.db.internal
 User: report_user
 Pass: ReportingDB_P@ssw0rd
*/

-- UPDATE users SET password_hash = 'new_strong_hash' WHERE username = 'admin'; -- Old hash was 'plain_text_not_a_hash_password!'

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

CREATE TABLE app_config (
                            config_key VARCHAR(100) PRIMARY KEY,
                            config_value TEXT
);
INSERT INTO app_config (config_key, 'config_value') VALUES ('jwt.secret.base64', 'c3FsaXplX3NlY3JldF9iYXNlNjQ='); -- "sqlize_secret_base64"


-- mysqldump -u root -p'root_password_in_backup_command' --all-databases > /mnt/backups/full_backup.sql


CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    user_name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    bio VARCHAR(500) NOT NULL,
    created_at DATETIME DEFAULT NULL,
  	account_non_expired bit(1) DEFAULT NULL,
  	account_non_locked bit(1) DEFAULT NULL,
  	credentials_non_expired bit(1) DEFAULT NULL,
    enabled bit(1) DEFAULT FALSE,
    file_id BIGINT DEFAULT NULL,
    CONSTRAINT fk_file FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE SET NULL
);

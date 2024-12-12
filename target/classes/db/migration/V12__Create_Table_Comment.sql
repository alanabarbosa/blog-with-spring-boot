CREATE TABLE IF NOT EXISTS comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    status BOOLEAN NOT NULL,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_post_comment FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_comment FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT NULL,
    published_at DATETIME,
    slug VARCHAR(255) NULL,
    status BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    image_desktop_id BIGINT DEFAULT NULL,
    image_mobile_id BIGINT DEFAULT NULL,
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_image_desktop FOREIGN KEY (image_desktop_id) REFERENCES files(id),
    CONSTRAINT fk_post_image_mobile FOREIGN KEY (image_mobile_id) REFERENCES files(id)
);




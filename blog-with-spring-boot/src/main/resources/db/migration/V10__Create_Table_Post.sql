CREATE TABLE IF NOT EXISTS post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    published_at DATETIME,
    slug VARCHAR(255) NOT NULL,
    status BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    image_desktop_id BIGINT,
    image_mobile_id BIGINT,
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_image_desktop FOREIGN KEY (image_desktop_id) REFERENCES files(id),
    CONSTRAINT fk_post_image_mobile FOREIGN KEY (image_mobile_id) REFERENCES files(id)
);

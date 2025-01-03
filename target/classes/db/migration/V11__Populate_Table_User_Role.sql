-- Inserção de roles (admin, moderator, user)
-- Admin (4 usuários)
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1), -- Admin
(2, 1), -- Admin
(3, 1), -- Admin
(4, 1); -- Admin

-- Moderator (30 usuários)
INSERT INTO user_role (user_id, role_id) VALUES
(5, 3), (6, 3), (7, 3), (8, 3), (9, 3), (10, 3), (11, 3), (12, 3), (13, 3),
(14, 3), (15, 3), (16, 3), (17, 3), (18, 3), (19, 3), (20, 3), (21, 3), (22, 3),
(23, 3), (24, 3), (25, 3), (26, 3), (27, 3), (28, 3), (29, 3), (30, 3), (31, 3),
(32, 3), (33, 3), (34, 3);

-- User (restante dos usuários)
-- Certifique-se de que o nome da coluna seja correto, como 'id' em 'users'
INSERT INTO user_role (user_id, role_id)
SELECT id, 2 FROM users WHERE id > 34;

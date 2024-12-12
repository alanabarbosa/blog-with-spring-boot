INSERT INTO role (name, description, created_at) VALUES
('Admin', 'Has full access to all resources', NOW()),
('User', 'Has limited access to resources', NOW()),
('Moderator', 'Can manage user-generated content', NOW());
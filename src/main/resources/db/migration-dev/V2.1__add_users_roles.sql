INSERT INTO roles (name, description)
VALUES
    ('Admin', 'Administrator z pełnym dostępem do systemu'),
    ('User',  'Standardowy użytkownik systemu');

INSERT INTO users (role, email, password)
VALUES
    (
        (SELECT id FROM roles WHERE name = 'Admin'),
        'admin@example.com',
        '$2a$10$2PWAz5EWL30tVNl9dXg1..5QpEnmsCLWL83y7nir2BMM1yV3I0I.C' -- adminpass
    ),
    (
        (SELECT id FROM roles WHERE name = 'User'),
        'defaultuser@example.com',
        '$2a$10$6gBWNrgdWw3YwOP8rniYIuZlrkXoND/a0l7hMA3M2rG2PoI9rWVSm' -- pass
    );

INSERT INTO user_role (user_id, role_id)
VALUES
    (
        (SELECT id FROM users WHERE email = 'admin@example.com'),
        (SELECT id FROM roles WHERE name = 'Admin')
    ),
    (
        (SELECT id FROM users WHERE email = 'defaultuser@example.com'),
        (SELECT id FROM roles WHERE name = 'User')
    );
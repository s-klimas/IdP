INSERT INTO roles (name, description)
VALUES
    ('Admin', 'Administrator z pełnym dostępem do systemu'),
    ('User',  'Standardowy użytkownik systemu');

INSERT INTO users (role, email, password)
VALUES
    (
        (SELECT id FROM roles WHERE name = 'Admin'),
        'admin@example.com',
        '{noop}adminpass'
    ),
    (
        (SELECT id FROM roles WHERE name = 'User'),
        'defaultuser@example.com',
        '{noop}pass'
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
INSERT into role (id, name) 
VALUES (1, 'Administrator'),
       (2, 'Manager'),
       (3, 'Operator')
ON CONFLICT (name) DO NOTHING

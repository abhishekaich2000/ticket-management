INSERT INTO users (name, email, password, is_active, created_at, updated_at)
VALUES (
  'Admin',
  'admin@123.com',
  '$2a$10$a/5g8WTFNLdIfBdNArhzq.z5sB9FlXZiFM3.1H7R3sLAfJITOrcMG',
  TRUE,
  NOW(),
  NOW()
);
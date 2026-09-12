-- Adding user_id link in customer table with foreign key to users table

ALTER TABLE customers ADD COLUMN user_id BIGINT NOT NULL UNIQUE;
ALTER TABLE customers ADD CONSTRAINT fk_customers_users FOREIGN KEY (user_id) REFERENCES users (id);
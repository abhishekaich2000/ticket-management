-- drop customer table

ALTER TABLE tickets DROP CONSTRAINT fk_tickets_customer;

ALTER TABLE tickets
  ADD CONSTRAINT fk_tickets_customer
  FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE RESTRICT;

DROP TABLE customers;
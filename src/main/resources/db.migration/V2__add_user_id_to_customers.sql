ALTER TABLE customers
ADD COLUMN user_id UUID;

ALTER TABLE customers
ADD CONSTRAINT fk_customer_user
FOREIGN KEY (user_id) REFERENCES users(id)
ON DELETE SET NULL;
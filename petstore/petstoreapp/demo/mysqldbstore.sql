-- Create the customer table
CREATE TABLE customers (
  id INT PRIMARY KEY,
  name VARCHAR(255),
  email VARCHAR(255)
);

-- Insert data into the customer table
INSERT INTO customers (id, name, email)
VALUES
  (1, 'John Doe', 'john.doe@example.com'),
  (2, 'Jane Smith', 'jane.smith@example.com'),
  (3, 'Bob Johnson', 'bob.johnson@example.com'),
  (4, 'Chris Tremblay', 'chris.tremblay@microsoft.com');
  

-- Create the order table
CREATE TABLE orders (
  id INT PRIMARY KEY,
  customer_id INT,
  product VARCHAR(255),
  price DECIMAL(10, 2)
);

-- Insert data into the order table
INSERT INTO orders (id, customer_id, product, price)
VALUES
  (1, 1, 'Widget', 10.00),
  (2, 1, 'Gadget', 20.00),
  (3, 2, 'Thingamajig', 15.00),
  (4, 3, 'Doohickey', 5.00);

// generate a sql query to get all the customers
SELECT * FROM customers;

// generate a sql query to get all the customers with a missing email
SELECT * FROM customers WHERE COALESCE(email, '') = '';

// generate a sql query to get all the customers with a missing email or a "microsoft.com" email
SELECT * FROM customers WHERE email IS NULL OR email LIKE '%microsoft.com';

// generate a sql query to get all the customers that have placed an order
SELECT * FROM customers WHERE id IN (SELECT customer_id FROM orders);
 
SELECT * FROM 
  (SELECT * FROM 
    (SELECT * FROM orders WHERE id > 1 AND product = 'Widget') 
  WHERE price > 10)

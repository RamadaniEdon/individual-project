-- Create the database
CREATE DATABASE IF NOT EXISTS orders;

-- Use the database
USE orders;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    afm VARCHAR(20),
    country VARCHAR(255),
    postalcode VARCHAR(20)
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userId INT,
    createdAt DATE,
    FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    price DECIMAL(10,2)
);

CREATE TABLE orderItems (
    orderId INT,
    itemId INT,
    count INT,
    FOREIGN KEY (orderId) REFERENCES orders(id),
    FOREIGN KEY (itemId) REFERENCES items(id)
);

-- Insert data into users
INSERT INTO users (name, afm, country, postalcode) VALUES
('John Doe', '123456789', 'USA', '90210'),
('Jane Smith', '987654321', 'Canada', 'M5V 3L9'),
('Alice Johnson', '567890123', 'UK', 'SW1A 1AA'),
('Bob Brown', '654321987', 'Australia', '2000');

-- Insert data into items
INSERT INTO items (description, price) VALUES
('Laptop', 999.99),
('Smartphone', 499.99),
('Tablet', 299.99),
('Headphones', 89.99),
('Charger', 19.99);

-- Insert data into orders
INSERT INTO orders (userId, createdAt) VALUES
(1, '2024-06-01'),
(2, '2024-06-02'),
(3, '2024-06-03'),
(1, '2024-06-04'),
(4, '2024-06-05');

-- Insert data into orderItems
INSERT INTO orderItems (orderId, itemId, count) VALUES
(1, 1, 1),  -- John Doe ordered 1 Laptop
(1, 5, 2),  -- John Doe ordered 2 Chargers
(2, 2, 1),  -- Jane Smith ordered 1 Smartphone
(3, 3, 2),  -- Alice Johnson ordered 2 Tablets
(3, 4, 1),  -- Alice Johnson ordered 1 Headphones
(4, 1, 1),  -- John Doe ordered 1 Laptop
(4, 3, 1),  -- John Doe ordered 1 Tablet
(5, 2, 1),  -- Bob Brown ordered 1 Smartphone
(5, 4, 2);  -- Bob Brown ordered 2 Headphones
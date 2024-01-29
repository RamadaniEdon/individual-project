-- Create the database
CREATE DATABASE IF NOT EXISTS sql_test_orders;
USE sql_test_orders;

-- Create Users table
CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    afm INT UNIQUE NOT NULL,
    name VARCHAR(255),
    surname VARCHAR(255),
    birthday DATE,
    country VARCHAR(255),
    city VARCHAR(255),
    street VARCHAR(255),
    postalCode VARCHAR(20)
);

-- Create Orders table
CREATE TABLE Orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE,
    totalPrice DECIMAL(10, 2),
    userId INT,
    FOREIGN KEY (userId) REFERENCES Users(id)
);

-- Create Items table
CREATE TABLE Items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(10, 2)
);

-- Create OrderItems table
CREATE TABLE OrderItems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quantity INT,
    orderId INT,
    itemId INT,
    FOREIGN KEY (orderId) REFERENCES Orders(id),
    FOREIGN KEY (itemId) REFERENCES Items(id)
);

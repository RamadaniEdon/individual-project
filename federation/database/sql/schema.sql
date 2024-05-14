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
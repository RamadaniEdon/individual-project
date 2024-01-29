USE sql_test_orders;

-- Insert Users
INSERT INTO Users (afm, name, surname, birthday, country, city, street, postalCode)
VALUES
    (123456789, 'John', 'Doe', '1990-01-15', 'USA', 'New York', '123 Main St', '10001'),
    (987654321, 'Jane', 'Smith', '1985-05-20', 'Canada', 'Toronto', '456 Oak St', 'M5V 2H1');

-- Insert Items
INSERT INTO Items (name, description, price)
VALUES
    ('Laptop', 'High-performance laptop', 1200.00),
    ('Smartphone', 'Latest model smartphone', 800.00),
    ('Headphones', 'Noise-canceling headphones', 150.00);

-- Insert Orders
INSERT INTO Orders (date, totalPrice, userId)
VALUES
    ('2024-01-22', 2000.00, 1),
    ('2024-01-23', 950.00, 2),
    ('2024-01-24', 300.00, 1);

-- Insert OrderItems
INSERT INTO OrderItems (quantity, orderId, itemId)
VALUES
    (2, 1, 1),
    (1, 1, 2),
    (3, 2, 3),
    (2, 3, 1),
    (1, 3, 2);

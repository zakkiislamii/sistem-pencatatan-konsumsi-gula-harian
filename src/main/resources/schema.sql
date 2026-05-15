CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS sugar_consumptions (
    consumption_id INT PRIMARY KEY,
    amount DECIMAL(5,2) NOT NULL CHECK (amount >= 0),
    description TEXT,
    consumed_at TIMESTAMP NOT NULL,
    user_id INT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
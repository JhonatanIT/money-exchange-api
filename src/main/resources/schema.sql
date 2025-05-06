CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exchange_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(19, 6) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT unique_currency_pair UNIQUE (source_currency, target_currency)
);

CREATE TABLE IF NOT EXISTS exchange_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    source_amount DECIMAL(19, 6) NOT NULL,
    target_amount DECIMAL(19, 6) NOT NULL,
    exchange_rate DECIMAL(19, 6) NOT NULL,
    user_id BIGINT,
    username VARCHAR(100) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Initial data
INSERT INTO users (username, password, role)
VALUES ('admin', '$2a$10$hdVBmozMbj9zndSB6BXoh.eVe7y1CCqMzYxdayeMrOes6pme/mD1e', 'ADMIN'); -- password = admin
--VALUES ('admin', 'admin', 'ADMIN'); -- password = admin

INSERT INTO exchange_rates (source_currency, target_currency, rate, created_by)
VALUES
    ('USD', 'PEN', 3.70, 'system'),
    ('EUR', 'PEN', 4.20, 'system'),
    ('PEN', 'USD', 0.27, 'system'),
    ('PEN', 'EUR', 0.24, 'system');
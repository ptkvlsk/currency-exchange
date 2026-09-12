CREATE TABLE IF NOT EXISTS currencies(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    sign TEXT NOT NULL
);

INSERT OR IGNORE INTO currencies (code, full_name, sign) VALUES
     ('USD', 'United States dollar', '$'),
     ('EUR', 'EURO', '€'),
     ('RUB', 'Russian Ruble', '₽'),
     ('GBP', 'British Pound', '£');

CREATE TABLE IF NOT EXISTS exchange_rates(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    base_currency_id INTEGER NOT NULL,
    target_currency_id INTEGER NOT NULL,
    rate DECIMAL(6,6) NOT NULL,
    FOREIGN KEY (base_currency_id) REFERENCES currencies(id),
    FOREIGN KEY (target_currency_id) REFERENCES currencies(id),
    UNIQUE (base_currency_id, target_currency_id)
);
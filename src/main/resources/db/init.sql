CREATE TABLE IF NOT EXISTS currencies (
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

CREATE TABLE IF NOT EXISTS exchange_rates (
id INTEGER PRIMARY KEY AUTOINCREMENT,
base_currency_id INTEGER NOT NULL,
target_currency_id INTEGER NOT NULL,
rate DECIMAL(6,6) NOT NULL,
    FOREIGN KEY (base_currency_id) REFERENCES currencies(id),
    FOREIGN KEY (target_currency_id) REFERENCES currencies(id),
    UNIQUE (base_currency_id, target_currency_id)
    );

INSERT OR IGNORE INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES
    ((SELECT id FROM currencies WHERE code = 'USD'),
     (SELECT id FROM currencies WHERE code = 'EUR'), 0.92),

    ((SELECT id FROM currencies WHERE code = 'USD'),
     (SELECT id FROM currencies WHERE code = 'RUB'), 90.5),

    ((SELECT id FROM currencies WHERE code = 'USD'),
     (SELECT id FROM currencies WHERE code = 'GBP'), 0.79),

    ((SELECT id FROM currencies WHERE code = 'EUR'),
     (SELECT id FROM currencies WHERE code = 'USD'), 1.09),

    ((SELECT id FROM currencies WHERE code = 'RUB'),
     (SELECT id FROM currencies WHERE code = 'USD'), 0.011);
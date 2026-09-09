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
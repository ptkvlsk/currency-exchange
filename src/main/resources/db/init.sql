CREATE TABLE IF NOT EXISTS Currencies(
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Code TEXT UNIQUE NOT NULL,
    FullName TEXT NOT NULL,
    Sign TEXT NOT NULL
);

INSERT OR IGNORE INTO Currencies (Code, FullName, Sign) VALUES
                                                  ('USD', 'United States dollar', '$'),
                                                  ('EUR', 'EURO', '€'),
                                                  ('RUB','Russian Ruble','₽'),
                                                  ('GPB','British Pound', '£');
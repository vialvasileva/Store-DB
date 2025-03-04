CREATE TABLE items (
    id CHAR(3) PRIMARY KEY,
    name TEXT NOT NULL,
    price NUMERIC(10,2) CHECK (price >= 0),
    category TEXT, 
    amount INT CHECK (amount >= 0) 
);

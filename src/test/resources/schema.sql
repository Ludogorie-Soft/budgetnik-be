CREATE TABLE IF NOT EXISTS income_category (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    bg_name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS expense_category (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    bg_name VARCHAR(255)
);
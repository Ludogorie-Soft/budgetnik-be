
CREATE TABLE IF NOT EXISTS subcategory (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    bg_name VARCHAR(255) NOT NULL,
    income_category_id UUID,
    expense_category_id UUID,
    CONSTRAINT fk_income_category
        FOREIGN KEY (income_category_id)
        REFERENCES income_category (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_expense_category
        FOREIGN KEY (expense_category_id)
        REFERENCES expense_category (id)
        ON DELETE SET NULL
);


DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_income_category_id') THEN
        CREATE INDEX idx_income_category_id ON subcategory (income_category_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_expense_category_id') THEN
        CREATE INDEX idx_expense_category_id ON subcategory (expense_category_id);
    END IF;
END $$;
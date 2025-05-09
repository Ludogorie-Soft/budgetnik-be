-- Delete any subscription plans with NULL stripe_price_id
DELETE FROM subscription_plans WHERE stripe_price_id IS NULL;

-- Update any subscription plans with placeholder stripe IDs
UPDATE subscription_plans
SET stripe_product_id = 'temp_product_id_' || id::text,
    stripe_price_id = 'temp_price_id_' || id::text
WHERE stripe_product_id LIKE 'placeholder_%' OR stripe_price_id LIKE 'placeholder_%';

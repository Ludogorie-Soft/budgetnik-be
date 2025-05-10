-- Make stripe_product_id and stripe_price_id nullable
ALTER TABLE subscription_plans
ALTER COLUMN stripe_product_id DROP NOT NULL,
ALTER COLUMN stripe_price_id DROP NOT NULL;

-- Update the default plans with actual Stripe IDs (these will be replaced with real IDs)
UPDATE subscription_plans
SET stripe_product_id = NULL, stripe_price_id = NULL
WHERE stripe_product_id LIKE 'prod_placeholder%' OR stripe_price_id LIKE 'price_placeholder%';

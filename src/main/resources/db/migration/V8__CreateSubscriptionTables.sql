-- Drop old subscriptions table if it exists
DROP TABLE IF EXISTS subscriptions;
-- Create subscription_plans table
CREATE TABLE IF NOT EXISTS subscription_plans (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    amount BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    interval VARCHAR(10) NOT NULL,
    features TEXT,
    stripe_product_id VARCHAR(255) NOT NULL,
    stripe_price_id VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

-- Create subscriptions table
CREATE TABLE IF NOT EXISTS subscriptions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    subscription_plan_id UUID,
    stripe_subscription_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    current_period_start TIMESTAMP,
    current_period_end TIMESTAMP,
    cancel_at_period_end BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_subscription_plan FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans(id) ON DELETE SET NULL
);

-- Insert default subscription plans
INSERT INTO subscription_plans (id, name, description, amount, currency, interval, features, stripe_product_id, stripe_price_id, active)
VALUES 
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Monthly Plan', 'Access to all premium features with monthly billing', 999, 'bgn', 'month', 'All premium features,Priority support,Unlimited transactions', 'prod_placeholder_monthly', 'price_placeholder_monthly', true),
    ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Annual Plan', 'Access to all premium features with annual billing', 9000, 'bgn', 'year', 'All premium features,Priority support,Unlimited transactions', 'prod_placeholder_yearly', 'price_placeholder_yearly', true);

-- Create index for faster lookups
CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_subscription_plans_active ON subscription_plans(active);

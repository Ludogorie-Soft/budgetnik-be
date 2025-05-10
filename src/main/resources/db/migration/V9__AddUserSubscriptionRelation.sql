-- Add subscription_id column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS subscription_id UUID;

-- Add foreign key constraint
ALTER TABLE users
ADD CONSTRAINT fk_user_subscription
FOREIGN KEY (subscription_id)
REFERENCES subscriptions(id)
ON DELETE SET NULL;

-- Create index for faster lookups
CREATE INDEX idx_users_subscription_id ON users(subscription_id);

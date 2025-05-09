
INSERT INTO subscription_plans (id, name, description, amount, currency, interval, features, stripe_product_id, stripe_price_id, active)
SELECT
    gen_random_uuid(),
    'Monthly Plan',
    'Access to all premium features with monthly billing',
    999,
    'bgn',
    'month',
    'All premium features,Priority support,Unlimited transactions',
    'placeholder_product_id_monthly',
    'placeholder_price_id_monthly',
    true
WHERE NOT EXISTS (
    SELECT 1 FROM subscription_plans WHERE interval = 'month'
);

INSERT INTO subscription_plans (id, name, description, amount, currency, interval, features, stripe_product_id, stripe_price_id, active)
SELECT
    gen_random_uuid(),
    'Annual Plan',
    'Access to all premium features with annual billing (save 25%)',
    9000,
    'bgn',
    'year',
    'All premium features,Priority support,Unlimited transactions,Annual discount',
    'placeholder_product_id_annual',
    'placeholder_price_id_annual',
    true
WHERE NOT EXISTS (
    SELECT 1 FROM subscription_plans WHERE interval = 'year'
);

-- Update the monthly plan price to match the correct amount (9.99 BGN)
UPDATE subscription_plans
SET amount = 999
WHERE interval = 'month' AND currency = 'bgn';

-- Update the yearly plan price to match the correct amount (90.99 BGN)
UPDATE subscription_plans
SET amount = 9000
WHERE interval = 'year' AND currency = 'bgn';

-- Ensure all plans have the correct currency
UPDATE subscription_plans
SET currency = 'bgn'
WHERE currency != 'bgn';

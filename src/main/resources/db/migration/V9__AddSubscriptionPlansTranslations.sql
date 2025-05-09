-- Add translations column to subscription_plans table
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS translations jsonb;

-- Update existing monthly plan with translations
UPDATE subscription_plans
SET translations = '{
  "en": {
    "name": "Monthly Plan",
    "description": "Access to all premium features with monthly billing",
    "features": "All premium features,Priority support,Unlimited transactions"
  },
  "bg": {
    "name": "Месечен план",
    "description": "Достъп до всички премиум функции с месечно таксуване",
    "features": "Всички премиум функции,Приоритетна поддръжка,Неограничени транзакции"
  }
}'::jsonb
WHERE interval = 'month';

-- Update existing yearly plan with translations
UPDATE subscription_plans
SET translations = '{
  "en": {
    "name": "Annual Plan",
    "description": "Access to all premium features with annual billing",
    "features": "All premium features,Priority support,Unlimited transactions,Annual discount"
  },
  "bg": {
    "name": "Годишен план",
    "description": "Достъп до всички премиум функции с годишно таксуване",
    "features": "Всички премиум функции,Приоритетна поддръжка,Неограничени транзакции"
  }
}'::jsonb
WHERE interval = 'year';

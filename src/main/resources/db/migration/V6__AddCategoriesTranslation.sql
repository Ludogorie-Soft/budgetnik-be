ALTER TABLE income_category ADD COLUMN IF NOT EXISTS translations jsonb;
ALTER TABLE expense_category ADD COLUMN IF NOT EXISTS translations jsonb;

UPDATE expense_category
SET translations = '{
  "en": "Home",
  "bg": "Жилище"
}'::jsonb
WHERE name = 'home';

UPDATE expense_category
SET translations = '{
  "en": "Utility",
  "bg": "Комунални услуги"
}'::jsonb
WHERE name = 'utilityBills';

UPDATE expense_category
SET translations = '{
  "en": "Food and Drinks",
  "bg": "Храна и напитки"
}'::jsonb
WHERE name = 'food';

UPDATE expense_category
SET translations = '{
  "en": "Car",
  "bg": "Автомобил"
}'::jsonb
WHERE name = 'car';

UPDATE expense_category
SET translations = '{
  "en": "Transport",
  "bg": "Транспорт"
}'::jsonb
WHERE name = 'transport';

UPDATE expense_category
SET translations = '{
  "en": "Health",
  "bg": "Здраве"
}'::jsonb
WHERE name = 'health';

UPDATE expense_category
SET translations = '{
  "en": "Education",
  "bg": "Образование"
}'::jsonb
WHERE name = 'education';

UPDATE expense_category
SET translations = '{
  "en": "Fun",
  "bg": "Забавление и Свободно време"
}'::jsonb
WHERE name = 'fun';

UPDATE expense_category
SET translations = '{
  "en": "Trips and Holidays",
  "bg": "Пътувания и ваканции"
}'::jsonb
WHERE name = 'trip';

UPDATE expense_category
SET translations = '{
  "en": "Personal",
  "bg": "Лични разходи"
}'::jsonb
WHERE name = 'personal';

UPDATE expense_category
SET translations = '{
  "en": "Family",
  "bg": "Семейни разходи"
}'::jsonb
WHERE name = 'family';

UPDATE expense_category
SET translations = '{
  "en": "Credits",
  "bg": "Дългове и кредити"
}'::jsonb
WHERE name = 'credits';

UPDATE expense_category
SET translations = '{
  "en": "Investment",
  "bg": "Спестявания и инвестиции"
}'::jsonb
WHERE name = 'investments';

UPDATE expense_category
SET translations = '{
  "en": "Donation",
  "bg": "Благотворителност"
}'::jsonb
WHERE name = 'donation';

UPDATE expense_category
SET translations = '{
  "en": "Other",
  "bg": "Други"
}'::jsonb
WHERE name = 'other';




UPDATE income_category
SET translations = '{
  "en": "Salary",
  "bg": "Заплата"
}'::jsonb
WHERE name = 'salary';

UPDATE income_category
SET translations = '{
  "en": "Bonuses",
  "bg": "Бонуси и премии"
}'::jsonb
WHERE name = 'bonus';

UPDATE income_category
SET translations = '{
  "en": "Passive incomes",
  "bg": "Пасивни доходи"
}'::jsonb
WHERE name = 'passive';

UPDATE income_category
SET translations = '{
  "en": "Business incomes",
  "bg": "Бизнес приходи"
}'::jsonb
WHERE name = 'business';

UPDATE income_category
SET translations = '{
  "en": "Projects",
  "bg": "Доходи от странични проекти"
}'::jsonb
WHERE name = 'projects';

UPDATE income_category
SET translations = '{
  "en": "Social",
  "bg": "Социални плащания"
}'::jsonb
WHERE name = 'social';

UPDATE income_category
SET translations = '{
  "en": "Gifts",
  "bg": "Подаръци"
}'::jsonb
WHERE name = 'gifts';

UPDATE income_category
SET translations = '{
  "en": "Sales of goods",
  "bg": "Продажби на вещи"
}'::jsonb
WHERE name = 'purchase';

UPDATE income_category
SET translations = '{
  "en": "Funds recovered",
  "bg": "Възстановени средства"
}'::jsonb
WHERE name = 'recovered';

UPDATE income_category
SET translations = '{
  "en": "Other",
  "bg": "Други"
}'::jsonb
WHERE name = 'other';



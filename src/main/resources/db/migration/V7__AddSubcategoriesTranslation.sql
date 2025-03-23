ALTER TABLE subcategory ADD COLUMN IF NOT EXISTS translations jsonb;

UPDATE subcategory
SET translations = '{
  "en": "Dividends",
  "bg": "Дивиденти"
}'::jsonb
WHERE name = 'dividents';

UPDATE subcategory
SET translations = '{
  "en": "Rent",
  "bg": "Наеми"
}'::jsonb
WHERE name = 'rent';

UPDATE subcategory
SET translations = '{
  "en": "Freelance",
  "bg": "Фрийланс"
}'::jsonb
WHERE name = 'freelance';

UPDATE subcategory
SET translations = '{
  "en": "Consult",
  "bg": "Консултации"
}'::jsonb
WHERE name = 'consult';

UPDATE subcategory
SET translations = '{
  "en": "Hobby",
  "bg": "Хоби"
}'::jsonb
WHERE name = 'hobby';

UPDATE subcategory
SET translations = '{
  "en": "Pensions",
  "bg": "Пенсии"
}'::jsonb
WHERE name = 'pensions';

UPDATE subcategory
SET translations = '{
  "en": "Child benefits",
  "bg": "Детски"
}'::jsonb
WHERE name = 'childBenefits';

UPDATE subcategory
SET translations = '{
  "en": "Motherhood",
  "bg": "Майчинство"
}'::jsonb
WHERE name = 'motherhood';

UPDATE subcategory
SET translations = '{
  "en": "Compensations",
  "bg": "Обезщетения"
}'::jsonb
WHERE name = 'compensations';

UPDATE subcategory
SET translations = '{
  "en": "Cashback",
  "bg": "Кешбек"
}'::jsonb
WHERE name = 'cashBack';

UPDATE subcategory
SET translations = '{
  "en": "Insurances",
  "bg": "Застраховки"
}'::jsonb
WHERE name = 'insurances';

UPDATE subcategory
SET translations = '{
  "en": "Deposits",
  "bg": "Депозити"
}'::jsonb
WHERE name = 'deposits';

UPDATE subcategory
SET translations = '{
  "en": "Mortgage",
  "bg": "Ипотека"
}'::jsonb
WHERE name = 'mortgage';

UPDATE subcategory
SET translations = '{
  "en": "Water",
  "bg": "Вода"
}'::jsonb
WHERE name = 'water';

UPDATE subcategory
SET translations = '{
  "en": "Internet",
  "bg": "Интернет"
}'::jsonb
WHERE name = 'internet';

UPDATE subcategory
SET translations = '{
  "en": "Television",
  "bg": "Телевизия"
}'::jsonb
WHERE name = 'tv';

UPDATE subcategory
SET translations = '{
  "en": "Electricity",
  "bg": "Газ и електричество"
}'::jsonb
WHERE name = 'electricity';

UPDATE subcategory
SET translations = '{
  "en": "Food products",
  "bg": "Хранителни продукти"
}'::jsonb
WHERE name = 'foodProducts';

UPDATE subcategory
SET translations = '{
  "en": "Restaurants",
  "bg": "Ресторанти"
}'::jsonb
WHERE name = 'restaurants';

UPDATE subcategory
SET translations = '{
  "en": "Food delivery",
  "bg": "Доставка на храна"
}'::jsonb
WHERE name = 'foodDelivery';

UPDATE subcategory
SET translations = '{
  "en": "Fuel",
  "bg": "Гориво"
}'::jsonb
WHERE name = 'fuel';

UPDATE subcategory
SET translations = '{
  "en": "City transport",
  "bg": "Градски транспорт"
}'::jsonb
WHERE name = 'cityTransport';

UPDATE subcategory
SET translations = '{
  "en": "Leasing",
  "bg": "Лизинг"
}'::jsonb
WHERE name = 'lising';

UPDATE subcategory
SET translations = '{
  "en": "Taxi",
  "bg": "Такси"
}'::jsonb
WHERE name = 'taxi';

UPDATE subcategory
SET translations = '{
  "en": "Medicals",
  "bg": "Лекарства"
}'::jsonb
WHERE name = 'medicals';

UPDATE subcategory
SET translations = '{
  "en": "Doctors",
  "bg": "Лекари"
}'::jsonb
WHERE name = 'doctors';

UPDATE subcategory
SET translations = '{
  "en": "Sport",
  "bg": "Спорт"
}'::jsonb
WHERE name = 'sport';

UPDATE subcategory
SET translations = '{
  "en": "Courses",
  "bg": "Курсове"
}'::jsonb
WHERE name = 'courses';

UPDATE subcategory
SET translations = '{
  "en": "Lessons",
  "bg": "Уроци"
}'::jsonb
WHERE name = 'lessons';

UPDATE subcategory
SET translations = '{
  "en": "Cinema",
  "bg": "Кино"
}'::jsonb
WHERE name = 'cinema';

UPDATE subcategory
SET translations = '{
  "en": "Theater",
  "bg": "Театър"
}'::jsonb
WHERE name = 'theater';

UPDATE subcategory
SET translations = '{
  "en": "Subscriptions",
  "bg": "Абонаменти"
}'::jsonb
WHERE name = 'abonaments';

UPDATE subcategory
SET translations = '{
  "en": "Tickets",
  "bg": "Билети"
}'::jsonb
WHERE name = 'tickets';

UPDATE subcategory
SET translations = '{
  "en": "Hotels",
  "bg": "Хотели"
}'::jsonb
WHERE name = 'hotels';

UPDATE subcategory
SET translations = '{
  "en": "Excursions",
  "bg": "Екскурзии"
}'::jsonb
WHERE name = 'excursions';

UPDATE subcategory
SET translations = '{
  "en": "Cosmetic",
  "bg": "Козметика"
}'::jsonb
WHERE name = 'cosmetic';

UPDATE subcategory
SET translations = '{
  "en": "Clothing",
  "bg": "Облекло"
}'::jsonb
WHERE name = 'clothing';

UPDATE subcategory
SET translations = '{
  "en": "Skin and hair care",
  "bg": "Грижи за коса и кожа"
}'::jsonb
WHERE name = 'skinAndHairCare';

UPDATE subcategory
SET translations = '{
  "en": "Children care",
  "bg": "Грижи за деца"
}'::jsonb
WHERE name = 'children';

UPDATE subcategory
SET translations = '{
  "en": "Pets",
  "bg": "Домашни любимци"
}'::jsonb
WHERE name = 'pets';

UPDATE subcategory
SET translations = '{
  "en": "Gifts",
  "bg": "Подаръци"
}'::jsonb
WHERE name = 'gifts';

UPDATE subcategory
SET translations = '{
  "en": "Loan",
  "bg": "Вноски по заеми"
}'::jsonb
WHERE name = 'loan';

UPDATE subcategory
SET translations = '{
  "en": "Interests",
  "bg": "Лихви"
}'::jsonb
WHERE name = 'interests';

UPDATE subcategory
SET translations = '{
  "en": "Credit cards",
  "bg": "Кредитни карти"
}'::jsonb
WHERE name = 'creditCards';

UPDATE subcategory
SET translations = '{
  "en": "Fonds",
  "bg": "Фондове"
}'::jsonb
WHERE name = 'fonds';

UPDATE subcategory
SET translations = '{
  "en": "Actions",
  "bg": "Акции"
}'::jsonb
WHERE name = 'actions';

UPDATE subcategory
SET translations = '{
  "en": "Donations",
  "bg": "Дарения"
}'::jsonb
WHERE name = 'donations';

UPDATE subcategory
SET translations = '{
  "en": "Volunteering",
  "bg": "Доброволчество"
}'::jsonb
WHERE name = 'volunteering';

UPDATE subcategory
SET translations = '{
  "en": "Car registration",
  "bg": "Регистрация"
}'::jsonb
WHERE name = 'carRegistration';

UPDATE subcategory
SET translations = '{
  "en": "Car service",
  "bg": "Сервиз"
}'::jsonb
WHERE name = 'carService';

UPDATE subcategory
SET translations = '{
  "en": "Phone",
  "bg": "Телефон"
}'::jsonb
WHERE name = 'phone';
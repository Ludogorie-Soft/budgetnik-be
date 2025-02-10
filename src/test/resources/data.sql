INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('1ca59796-8617-4223-a0d3-70ffef366d22'::uuid, 'car', 'Автомобил')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('d5cc6102-142b-4797-a10d-d721a62d41d5'::uuid, 'fuel', 'Автомобил: Гориво')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('2c534a18-6c72-4978-b38d-871a04994d85'::uuid, 'carRegistration', 'Автомобил: Регистрация')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('9572d842-6f4c-44c0-a48a-e98cea18c0f8'::uuid, 'carService', 'Автомобил: Сервиз')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('bc47b296-e2c2-4ae8-9c8d-a4058a47a79d'::uuid, 'bankFee', 'Банкова такса')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('5d6e0553-bde1-463e-94a4-89c9eeb9c584'::uuid, 'beauty', 'Красота')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7d0406ae-5916-4e51-875a-3ec6ca7df4d4'::uuid, 'donations', 'Дарения')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('0744ca86-0e2e-4185-8598-54eeb669fe7b'::uuid, 'childrenCare', 'Грижи за деца')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('c6af8409-6c09-457d-bf3b-627ad4afa169'::uuid, 'clothing', 'Облекло')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('981accc1-26b1-4a93-b6aa-f47eeafd4974'::uuid, 'courses', 'Курсове')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('6437b8fe-74d1-4a25-8185-96a86ab90c1b'::uuid, 'outsideDrinks', 'Напитки навън')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('29a98a85-f593-412a-b367-f164a0a719d7'::uuid, 'eatingOut', 'Хранене навън')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('9d069c31-1052-46d5-a8fe-3895702e0edb'::uuid, 'education', 'Образование')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('b1d857be-6a6f-4bac-a5b5-b16ef0cb9303'::uuid, 'electronic', 'Електроника')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('d004d3e9-358c-483c-b2ad-63b31c8d4c1b'::uuid, 'fun', 'Забавления')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('ecc65da7-df65-496f-9cfc-98de968b0485'::uuid, 'furniture', 'Мебели')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7545c08a-2263-4da2-b9eb-90c9b88f1aaa'::uuid, 'gifts', 'Подаръци')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('df964579-42cb-4a4c-960b-ed2ba2827cae'::uuid, 'foodProducts', 'Хранителни стоки')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('d763eaaf-fa64-41f4-9397-5aa9af77fbc3'::uuid, 'health', 'Здраве и фитнес')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('ebeadab0-d217-49b1-afc1-8f0b8cad1f70'::uuid, 'homeRepair', 'Ремонт на дома')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('15055266-1c09-452f-8721-14c2c34601c0'::uuid, 'household', 'Домакинство')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('44ca614e-93a1-4538-a539-57fa5b7f385a'::uuid, 'insurance', 'Застраховка')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('a182ca27-5f80-484c-97a0-192716cb98dd'::uuid, 'investment', 'Инвестиция')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('753e9f05-0aa3-47ce-b0cd-49fbedfff096'::uuid, 'loan', 'Заем')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('5b92581d-99bd-4ee0-b39a-e4df36de4c1c'::uuid, 'medicine', 'Медицински разходи')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('e7179219-b2c8-4221-976e-dda22f161352'::uuid, 'mortgage', 'Ипотечно плащане')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('f3b93e0c-0c29-4044-9ee2-aef98c6b2a16'::uuid, 'other', 'Други')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('cc1f2225-a06c-4126-a802-684cddb9695c'::uuid, 'pets', 'Домашни любимци')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('f3b93e0c-0c29-4044-9ee2-aef98c6b2a18'::uuid, 'rent', 'Наем')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('5a4f0fe2-947c-4638-b289-f2f0170d934b'::uuid, 'transport', 'Транспорт')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('13457d63-3bf4-4482-b8cb-ce33e0323321'::uuid, 'travels', 'Пътувания')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('2ffa212f-33c2-4ad7-ba1f-ab26fa64b2a6'::uuid, 'services', 'Услуги')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('56435011-9709-44f2-89c3-6734fbe49a1f'::uuid, 'tvService', 'Услуги: Кабелна телевизия')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('a91cf75c-579b-4b8e-bc31-a54fb90a7c1d'::uuid, 'electricity', 'Услуги: Газ и електричество')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('192f76d3-e4b0-42d9-bab4-a713f253da41'::uuid, 'internet', 'Услуги: Интернет')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('70d83446-788a-4c48-a978-520cd9b102f2'::uuid, 'phone', 'Услуги: Телефон')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('9e7df080-dfb2-4dba-b1d8-79180f896ddd'::uuid, 'water', 'Услуги: Вода')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53f5'::uuid, 'work', 'Работа')
ON CONFLICT (id) DO NOTHING;




INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('50de22ce-ff62-4236-a299-d6632e077316'::uuid, 'bonus', 'Бонус')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('f3b93e0c-0c29-4044-9ee2-aef98c6b2a15'::uuid, 'other', 'Други')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('45d4ce3b-3b23-4035-b63f-ca4211dba2d0'::uuid, 'salary', 'Заплата')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('461b75bc-c69f-4586-bfed-07fa71881fd7'::uuid, 'deposit', 'Депозит в спестовна сметка')
ON CONFLICT (id) DO NOTHING;






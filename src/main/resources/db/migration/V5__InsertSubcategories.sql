INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('ef0026c0-1c43-48fb-8d0c-8ce3622f1b22'::uuid, 'Дивиденти', 'dividents', NULL, '50de22ce-ff62-4236-a299-d6632e077317'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('b21423f3-e79f-41ea-bc5c-7dc3eb3d0b76'::uuid, 'Наеми', 'rent', NULL, '50de22ce-ff62-4236-a299-d6632e077317'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('c331d7df-d50a-4a4f-abf1-a8e84b586d50'::uuid, 'Фрийланс', 'freelance', NULL, '50de22ce-ff62-4236-a299-d6632e077320'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('f553e66d-4d97-4b95-a8cc-789ee0e3847e'::uuid, 'Консултации', 'consult', NULL, '50de22ce-ff62-4236-a299-d6632e077320'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('15b64e90-dc49-4683-ad8b-88e6e39b38c3'::uuid, 'Хоби', 'hobby', NULL, '50de22ce-ff62-4236-a299-d6632e077320'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('e7373f6d-e6e8-48bb-828f-8281d5856d0a'::uuid, 'Пенсии', 'pensions', NULL, '50de22ce-ff62-4236-a299-d6632e077323'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('26624111-16c5-47bb-8e50-6f5e0c20c03b'::uuid, 'Детски', 'childBenefits', NULL, '50de22ce-ff62-4236-a299-d6632e077323'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('31028a68-1040-42f4-a52d-3fab86a11823'::uuid, 'Майчинство', 'motherhood', NULL, '50de22ce-ff62-4236-a299-d6632e077323'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('5656b39e-727c-4ae4-b419-07c53bd59dbf'::uuid, 'Обезщетения', 'compensations', NULL, '50de22ce-ff62-4236-a299-d6632e077323'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('a6c95dd6-8c30-4698-bce1-60f79432dc2b'::uuid, 'Кешбек', 'cashBack', NULL, '50de22ce-ff62-4236-a299-d6632e077329'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('aa3baccb-cf66-49f6-b5dc-de8ec0b1b1ab'::uuid, 'Застраховки', 'insurances', NULL, '50de22ce-ff62-4236-a299-d6632e077329'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('b1c0620c-9fdb-43a2-8033-21fa43dca491'::uuid, 'Депозити', 'deposits', NULL, '50de22ce-ff62-4236-a299-d6632e077329'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('5d6e460e-c6a1-413f-88ab-a0666b5d89cf'::uuid, 'Наем', 'rent', '7ac171ce-96a8-41b9-b208-db45915e53f6'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('711ae1e7-c082-4fd7-b161-ea97040977f7'::uuid, 'Ипотека', 'mortgage', '7ac171ce-96a8-41b9-b208-db45915e53f6'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('1757c450-b09f-49cd-9eff-c83e489afa85'::uuid, 'Вода', 'water', '7ac171ce-96a8-41b9-b208-db45915e53f7'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('0a5f4a57-8e2f-437c-b101-f6b0fd5aea39'::uuid, 'Интернет', 'internet', '7ac171ce-96a8-41b9-b208-db45915e53f7'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('d3d52737-0754-4ebb-b94c-06d954acc500'::uuid, 'Телевизия', 'tv', '7ac171ce-96a8-41b9-b208-db45915e53f7'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('0459fc2f-9a17-47dc-a855-ac710359a807'::uuid, 'Газ и електричество', 'electricity', '7ac171ce-96a8-41b9-b208-db45915e53f7'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('932d1051-6293-4ef1-9f9a-369eb79fe4b2'::uuid, 'Хранителни продукти', 'foodProducts', '7ac171ce-96a8-41b9-b208-db45915e53f8'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('a123420b-d1ab-4f2b-a685-d0fac440f192'::uuid, 'Ресторанти', 'restaurants', '7ac171ce-96a8-41b9-b208-db45915e53f8'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('0d7b0e88-52b0-422c-9452-8f11423f7d0c'::uuid, 'Доставки', 'foodDelivery', '7ac171ce-96a8-41b9-b208-db45915e53f8'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('ee4d95f8-df9d-4062-9eaa-da0698821cb2'::uuid, 'Гориво', 'fuel', '7ac171ce-96a8-41b9-b208-db45915e53d2'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('ebb1e37a-b4e6-4dfa-a028-beab726b749e'::uuid, 'Градски транспорт', 'cityTransport', '7ac171ce-96a8-41b9-b208-db45915e53d2'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('fd1411fe-174b-4fe7-a8c5-dc3b50d00f2a'::uuid, 'Лизинг', 'lising', '7ac171ce-96a8-41b9-b208-db45915e53d2'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('4b965229-735f-4052-9c06-f72fb8b5afa0'::uuid, 'Такси', 'taxi', '7ac171ce-96a8-41b9-b208-db45915e53d2'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('aca4a7ab-e105-4086-8197-eac931ace9e3'::uuid, 'Лекарства', 'medicals', '7ac171ce-96a8-41b9-b208-db45915e53d3'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('9f59b852-3bf2-4198-9885-37d4218dda43'::uuid, 'Застраховки', 'insurances', '7ac171ce-96a8-41b9-b208-db45915e53d3'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('cf652c32-ca99-421e-a45d-40dc1372e50e'::uuid, 'Лекари', 'doctors', '7ac171ce-96a8-41b9-b208-db45915e53d3'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('638ed2a1-c440-409f-b3c7-30c47e20a0f0'::uuid, 'Спорт', 'sport', '7ac171ce-96a8-41b9-b208-db45915e53d3'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('44c1a7cc-2766-4fcb-8d8d-7bb85cea703f'::uuid, 'Курсове', 'courses', '7ac171ce-96a8-41b9-b208-db45915e53d4'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('757e269e-bce5-40b5-a610-35ecff508a82'::uuid, 'Книги', 'books', '7ac171ce-96a8-41b9-b208-db45915e53d4'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('0756d9a3-e2e8-4cb2-8012-08295b552229'::uuid, 'Уроци', 'lessons', '7ac171ce-96a8-41b9-b208-db45915e53d4'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('5f34f258-e30c-4c83-a240-6c163ba4cfba'::uuid, 'Кино', 'cinema', '7ac171ce-96a8-41b9-b208-db45915e53d5'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('00e90028-9517-430f-91f6-50ad3acbaf9e'::uuid, 'Театър', 'theater', '7ac171ce-96a8-41b9-b208-db45915e53d5'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('a83d7c2c-67ef-4dc0-abde-0089357113dd'::uuid, 'Абонаменти', 'abonaments', '7ac171ce-96a8-41b9-b208-db45915e53d5'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('bf76dcbe-174f-4250-99b6-80ccb8135791'::uuid, 'Хоби', 'hobby', '7ac171ce-96a8-41b9-b208-db45915e53d5'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('8c196ab1-801c-4a96-b700-13368a85d342'::uuid, 'Билети', 'tickets', '7ac171ce-96a8-41b9-b208-db45915e53d6'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('af2eb1ec-cad1-4fea-9ee0-c4bdf97e4c6c'::uuid, 'Хотели', 'hotels', '7ac171ce-96a8-41b9-b208-db45915e53d6'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('ccd79969-ec4d-4bbc-b9d5-d4215193ef73'::uuid, 'Екскурзии', 'excursions', '7ac171ce-96a8-41b9-b208-db45915e53d6'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('b1bbb118-d5a3-40da-a73d-ca5612c5a3ad'::uuid, 'Козметика', 'cosmetic', '7ac171ce-96a8-41b9-b208-db45915e53d7'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('cdb4e60b-c1d9-4977-bcce-93474c274d64'::uuid, 'Облекло', 'clothing', '7ac171ce-96a8-41b9-b208-db45915e53d7'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('57f7db9a-84d3-487a-9c81-760c2c8e3318'::uuid, 'Грижи за коса и кожа', 'skinAndHairCare', '7ac171ce-96a8-41b9-b208-db45915e53d7'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('6655726f-2cd9-4da0-9e86-684a75622583'::uuid, 'Грижи за деца', 'children', '7ac171ce-96a8-41b9-b208-db45915e53d8'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('cd8e1727-c00e-4bac-bde4-f7f540c894ac'::uuid, 'Домашни любимци', 'pets', '7ac171ce-96a8-41b9-b208-db45915e53d8'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('863d8959-7cfa-496d-8855-d8e7d240020d'::uuid, 'Подаръци', 'gifts', '7ac171ce-96a8-41b9-b208-db45915e53d8'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('82416e1d-2c8f-40d5-b344-71261ca376ae'::uuid, 'Вноски по заеми', 'loan', '7ac171ce-96a8-41b9-b208-db45915e5389'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('d77658f3-b8ba-42c0-ace6-e76ce101ac90'::uuid, 'Лихви', 'interests', '7ac171ce-96a8-41b9-b208-db45915e5389'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('4a47f446-c379-4c0f-b198-7aac1a598e82'::uuid, 'Кредитни карти', 'creditCards', '7ac171ce-96a8-41b9-b208-db45915e5389'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('0f23e1d8-c7d5-46b0-8a9d-25dbfe0a7ce9'::uuid, 'Депозити', 'deposits', '7ac171ce-96a8-41b9-b208-db45915e5390'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('2303d370-1632-45fd-a776-281b8971594c'::uuid, 'Фондове', 'fonds', '7ac171ce-96a8-41b9-b208-db45915e5390'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('2098ef2f-19c5-4ca3-83a9-219707638b2c'::uuid, 'Акции', 'actions', '7ac171ce-96a8-41b9-b208-db45915e5390'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('8a26c739-7769-4160-96da-794dff657db9'::uuid, 'Дарения', 'donations', '7ac171ce-96a8-41b9-b208-db45915e5391'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('146e421d-740f-4ead-958a-4106e6e40d5a'::uuid, 'Доброволчество', 'volunteering', '7ac171ce-96a8-41b9-b208-db45915e5391'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('bf8c0aca-8dd7-435b-9a1e-62aa771b4526'::uuid, 'Регистрация', 'carRegistration', '7ac171ce-96a8-41b9-b208-db45915e53f9'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('8715a2e0-b178-452b-94de-60a2a423986c'::uuid, 'Сервиз', 'carService', '7ac171ce-96a8-41b9-b208-db45915e53f9'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.subcategory
(id, bg_name, "name", expense_category_id, income_category_id)
VALUES('58b37699-f175-4573-abaf-0c6fa3ebe7f6'::uuid, 'Телефон', 'phone', '7ac171ce-96a8-41b9-b208-db45915e53f7'::uuid, NULL)
ON CONFLICT (id) DO NOTHING;
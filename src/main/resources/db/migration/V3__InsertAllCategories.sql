
INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53f6'::uuid, 'home', 'Жилище')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53f7'::uuid, 'utilityBills', 'Комунални услуги')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53f8'::uuid, 'food', 'Храна и напитки')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53f9'::uuid, 'car', 'Автомобил')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53d2'::uuid, 'transport', 'Транспорт')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53d3'::uuid, 'health', 'Здраве')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53d4'::uuid, 'education', 'Образование')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53d5'::uuid, 'fun', 'Забавления и свободно време')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53d6'::uuid, 'trip', 'Пътувания и ваканции')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53d7'::uuid, 'personal', 'Лични разходи')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e53d8'::uuid, 'family', 'Семейни разходи')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e5389'::uuid, 'credits', 'Дългове и кредити')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e5390'::uuid, 'investments', 'Спестявания и инвестиции')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e5391'::uuid, 'donation', 'Благотворителност')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('7ac171ce-96a8-41b9-b208-db45915e5392'::uuid, 'other', 'Други')
ON CONFLICT (id) DO NOTHING;




INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('45d4ce3b-3b23-4035-b63f-ca4211dba2d0'::uuid, 'salary', 'Заплата')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('50de22ce-ff62-4236-a299-d6632e077316'::uuid, 'bonus', 'Бонуси и премии')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('50de22ce-ff62-4236-a299-d6632e077317'::uuid, 'passive', 'Пасивни доходи')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('50de22ce-ff62-4236-a299-d6632e077319'::uuid, 'business', 'Бизнес приходи')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('50de22ce-ff62-4236-a299-d6632e077320'::uuid, 'projects', 'Доходи от странични проекти')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('50de22ce-ff62-4236-a299-d6632e077323'::uuid, 'social', 'Социални плащания')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('50de22ce-ff62-4236-a299-d6632e077327'::uuid, 'gifts', 'Подаръци')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('50de22ce-ff62-4236-a299-d6632e077328'::uuid, 'purchase', 'Продажба на вещи')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('50de22ce-ff62-4236-a299-d6632e077329'::uuid, 'recovered', 'Възстановени средства')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('f3b93e0c-0c29-4044-9ee2-aef98c6b2a15'::uuid, 'other', 'Други')
ON CONFLICT (id) DO NOTHING;

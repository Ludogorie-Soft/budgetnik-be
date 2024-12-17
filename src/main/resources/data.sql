INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('45d4ce3b-3b23-4035-b63f-ca4211dba2d0'::uuid, 'salary', 'Работна заплата')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('9a6cacdd-a4a8-4eaa-9527-9ea762e1ab25'::uuid, 'rent', 'Наем')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('12a5213c-d04b-4c6d-b899-593ad54fcfbc'::uuid, 'sale', 'Продажба')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.income_category
(id, "name", bg_name)
VALUES('f3b93e0c-0c29-4044-9ee2-aef98c6b2a15'::uuid, 'other', 'Друго')
ON CONFLICT (id) DO NOTHING;



INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('f3b93e0c-0c29-4044-9ee2-aef98c6b2a16'::uuid, 'other', 'Друго')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('f3b93e0c-0c29-4044-9ee2-aef98c6b2a17'::uuid, 'purchase', 'Покупка')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('f3b93e0c-0c29-4044-9ee2-aef98c6b2a18'::uuid, 'rent', 'Наем')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('f3b93e0c-0c29-4044-9ee2-aef98c6b2a19'::uuid, 'phone', 'Телефон')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('f3b93e0c-0c29-4044-9ee2-aef98c6b2a20'::uuid, 'internet', 'Интернет')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category
(id, "name", bg_name)
VALUES('f3b93e0c-0c29-4044-9ee2-aef98c6b2a21'::uuid, 'water', 'Вода')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.expense_category (id, "name", bg_name)
VALUES ('f3b93e0c-0c29-4044-9ee2-aef98c6b2a22'::uuid, 'electricity', 'Ток')
ON CONFLICT (id) DO NOTHING;




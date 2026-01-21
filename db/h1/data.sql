-- Клиенты
INSERT INTO clients (full_name, phone, email, created_at) VALUES
('Иван Петров', '+79161234567', 'ivan.petrov@mail.ru', '2023-01-15'),
('Мария Сидорова', '+79262345678', 'maria.sidorova@gmail.com', '2023-02-20'),
('Алексей Иванов', '+79373456789', 'alex.ivanov@yandex.ru', '2023-03-10'),
('Елена Кузнецова', '+79484567890', 'elena.kuz@mail.ru', '2023-04-05'),
('Дмитрий Смирнов', '+79595678901', 'dmitry.smirnov@gmail.com', '2023-05-12'),
('Ольга Васнецова', '+79606789012', 'olga.vas@mail.ru', '2023-06-18'),
('Сергей Попов', '+79717890123', 'sergey.popov@gmail.com', '2023-07-22'),
('Анна Ковалева', '+79828901234', 'anna.koval@yandex.ru', '2023-08-30'),
('Павел Морозов', '+79939012345', 'pavel.moroz@mail.ru', '2023-09-11'),
('Юлия Федорова', '+79040123456', 'yulia.fedorova@gmail.com', '2023-10-05'),
('Михаил Волков', '+79151234567', 'mikhail.volkov@mail.ru', '2023-11-19'),
('Татьяна Новикова', '+79262345677', 'tanya.novikova@yandex.ru', '2023-12-01'),
('Андрей Павлов', '+79373456788', 'andrey.pavlov@gmail.com', '2024-01-15'),
('Наталья Лебедева', '+79484567899', 'natalia.lebedeva@mail.ru', '2024-02-20'),
('Виктор Соколов', '+79595678900', 'victor.sokolov@yandex.ru', '2024-03-10');

-- Счета
INSERT INTO accounts (client_id, account_type, currency, opened_at, status) VALUES
(1, 'debit', 'RUB', '2023-01-15', 'active'),
(1, 'savings', 'USD', '2023-01-20', 'active'),
(1, 'credit', 'EUR', '2023-02-10', 'active'),

(2, 'debit', 'RUB', '2023-02-20', 'active'),
(2, 'debit', 'EUR', '2024-01-15', 'active'),

(3, 'credit', 'RUB', '2023-03-10', 'active'),
(3, 'debit', 'USD', '2024-01-05', 'active'),
(3, 'savings', 'EUR', '2024-01-10', 'active'),
(3, 'debit', 'RUB', '2024-01-15', 'active'),

(4, 'debit', 'EUR', '2024-01-20', 'active'),

(5, 'debit', 'RUB', '2023-05-12', 'active'),

(6, 'debit', 'EUR', '2024-02-01', 'active'),
(6, 'savings', 'EUR', '2024-02-15', 'active'),
(6, 'credit', 'EUR', '2024-03-01', 'active'),

(7, 'debit', 'RUB', '2023-07-22', 'active'),
(7, 'savings', 'RUB', '2024-01-20', 'active'),

(8, 'debit', 'USD', '2023-08-30', 'active'),
(8, 'credit', 'EUR', '2024-01-25', 'active'),

(10, 'debit', 'RUB', '2024-02-01', 'active'),
(10, 'savings', 'USD', '2024-02-10', 'active'),

(11, 'debit', 'EUR', '2024-02-15', 'active'),

(12, 'credit', 'RUB', '2024-02-20', 'active'),

(13, 'debit', 'USD', '2024-02-25', 'active'),

(14, 'debit', 'RUB', '2024-03-01', 'active'),

(15, 'credit', 'USD', '2024-03-05', 'active');

-- Карты
INSERT INTO cards (account_id, card_type, issued_at, expires_at, status) VALUES
(1, 'debit', '2023-01-20', CURRENT_DATE + INTERVAL '1 year', 'active'),
(2, 'credit', '2023-01-25', CURRENT_DATE + INTERVAL '1 year', 'active'),
(3, 'debit', '2023-02-15', CURRENT_DATE + INTERVAL '1 year', 'active'),
(4, 'debit', '2023-02-25', CURRENT_DATE + INTERVAL '1 year', 'active'),
(5, 'debit', '2024-01-20', CURRENT_DATE + INTERVAL '1 year', 'active'),
(6, 'debit', '2024-01-10', CURRENT_DATE + INTERVAL '1 year', 'active'),
(7, 'credit', '2024-01-12', CURRENT_DATE + INTERVAL '1 year', 'active'),
(8, 'debit', '2024-01-20', CURRENT_DATE + INTERVAL '1 year', 'active'),
(9, 'debit', '2024-01-25', CURRENT_DATE + INTERVAL '1 year', 'active'),
(10, 'debit', '2024-02-05', CURRENT_DATE + INTERVAL '1 year', 'active'),
(11, 'debit', '2024-02-10', CURRENT_DATE + INTERVAL '1 year', 'active'),
(12, 'debit', '2024-02-15', CURRENT_DATE + INTERVAL '1 year', 'active'),
(13, 'credit', '2024-02-20', CURRENT_DATE + INTERVAL '1 year', 'active'),
(14, 'debit', '2024-02-25', CURRENT_DATE + INTERVAL '1 year', 'active'),
(15, 'debit', '2024-03-01', CURRENT_DATE + INTERVAL '1 year', 'active'),
(16, 'debit', '2024-03-05', CURRENT_DATE + INTERVAL '1 year', 'active'),
(17, 'virtual', '2024-03-10', CURRENT_DATE + INTERVAL '6 months', 'active'),
(18, 'debit', '2024-03-15', CURRENT_DATE + INTERVAL '1 year', 'active'),
(19, 'credit', '2024-03-20', CURRENT_DATE + INTERVAL '1 year', 'active');

-- Транзакции с АКТУАЛЬНЫМИ датами для запроса 7
INSERT INTO transactions (account_id, txn_type, amount, txn_date, description) VALUES
-- Иван Петров: HIGH активность (15 операций за последние 90 дней)
(1, 'deposit', 50000.00, CURRENT_DATE - INTERVAL '10 days', 'Зарплата'),
(1, 'withdrawal', 10000.00, CURRENT_DATE - INTERVAL '15 days', 'Снятие наличных'),
(1, 'transfer_out', 5000.00, CURRENT_DATE - INTERVAL '20 days', 'Перевод'),
(2, 'deposit', 15000.00, CURRENT_DATE - INTERVAL '25 days', 'Пополнение'),
(2, 'transfer_in', 8000.00, CURRENT_DATE - INTERVAL '30 days', 'Перевод от друга'),
(3, 'deposit', 30000.00, CURRENT_DATE - INTERVAL '35 days', 'Премия'),
(1, 'fee', 500.00, CURRENT_DATE - INTERVAL '40 days', 'Комиссия'),
(1, 'deposit', 20000.00, CURRENT_DATE - INTERVAL '45 days', 'Зарплата'),
(2, 'withdrawal', 7000.00, CURRENT_DATE - INTERVAL '50 days', 'Снятие'),
(3, 'transfer_out', 12000.00, CURRENT_DATE - INTERVAL '55 days', 'Перевод'),
(1, 'deposit', 25000.00, CURRENT_DATE - INTERVAL '60 days', 'Пополнение'),
(2, 'deposit', 18000.00, CURRENT_DATE - INTERVAL '65 days', 'Пополнение'),
(3, 'withdrawal', 9000.00, CURRENT_DATE - INTERVAL '70 days', 'Снятие'),
(1, 'fee', 300.00, CURRENT_DATE - INTERVAL '75 days', 'Комиссия'),
(2, 'transfer_in', 6000.00, CURRENT_DATE - INTERVAL '85 days', 'Перевод'),

-- Мария Сидорова: MEDIUM активность (8 операций)
(4, 'deposit', 40000.00, CURRENT_DATE - INTERVAL '5 days', 'Зарплата'),
(4, 'withdrawal', 15000.00, CURRENT_DATE - INTERVAL '12 days', 'Снятие'),
(5, 'deposit', 20000.00, CURRENT_DATE - INTERVAL '20 days', 'Пополнение'),
(4, 'transfer_out', 8000.00, CURRENT_DATE - INTERVAL '28 days', 'Перевод'),
(5, 'withdrawal', 10000.00, CURRENT_DATE - INTERVAL '35 days', 'Снятие'),
(4, 'deposit', 30000.00, CURRENT_DATE - INTERVAL '42 days', 'Премия'),
(5, 'transfer_in', 7000.00, CURRENT_DATE - INTERVAL '50 days', 'Перевод'),
(4, 'fee', 400.00, CURRENT_DATE - INTERVAL '60 days', 'Комиссия'),

-- Алексей Иванов: LOW активность (4 операции)
(6, 'deposit', 60000.00, CURRENT_DATE - INTERVAL '15 days', 'Зарплата'),
(7, 'withdrawal', 20000.00, CURRENT_DATE - INTERVAL '30 days', 'Снятие'),
(8, 'deposit', 35000.00, CURRENT_DATE - INTERVAL '45 days', 'Пополнение'),
(9, 'transfer_out', 15000.00, CURRENT_DATE - INTERVAL '60 days', 'Перевод'),

-- Елена Кузнецова: 2 операции (LOW)
(10, 'deposit', 45000.00, CURRENT_DATE - INTERVAL '20 days', 'Зарплата'),
(10, 'withdrawal', 18000.00, CURRENT_DATE - INTERVAL '40 days', 'Снятие'),

-- Дмитрий Смирнов: 1 операция (LOW)
(11, 'deposit', 30000.00, CURRENT_DATE - INTERVAL '25 days', 'Пополнение'),

-- Ольга Васнецова: 6 операций (MEDIUM)
(12, 'deposit', 70000.00, CURRENT_DATE - INTERVAL '8 days', 'Зарплата'),
(13, 'withdrawal', 25000.00, CURRENT_DATE - INTERVAL '18 days', 'Снятие'),
(14, 'deposit', 40000.00, CURRENT_DATE - INTERVAL '28 days', 'Пополнение'),
(12, 'transfer_out', 12000.00, CURRENT_DATE - INTERVAL '38 days', 'Перевод'),
(13, 'deposit', 35000.00, CURRENT_DATE - INTERVAL '48 days', 'Премия'),
(14, 'fee', 600.00, CURRENT_DATE - INTERVAL '58 days', 'Комиссия'),

-- Транзакции за 2025 год для запроса 6
(1, 'deposit', 100000.00, '2025-01-10 10:00:00', 'Зарплата за 2025'),
(2, 'deposit', 75000.00, '2025-01-20 09:45:00', 'Пополнение за 2025'),
(6, 'deposit', 120000.00, '2025-01-05 12:15:00', 'Поступление за 2025'),
(12, 'deposit', 80000.00, '2025-01-15 14:50:00', 'Зарплата за 2025');

-- Кредиты
INSERT INTO loans (client_id, principal, interest_rate, start_date, end_date, status) VALUES
(1, 100000.00, 8.5, CURRENT_DATE - INTERVAL '6 months', CURRENT_DATE + INTERVAL '6 months', 'active'),
(3, 150000.00, 7.9, CURRENT_DATE - INTERVAL '5 months', CURRENT_DATE + INTERVAL '7 months', 'active'),
(6, 80000.00, 8.0, CURRENT_DATE - INTERVAL '4 months', CURRENT_DATE + INTERVAL '8 months', 'active'),
(8, 120000.00, 9.5, CURRENT_DATE - INTERVAL '3 months', CURRENT_DATE + INTERVAL '9 months', 'active');

-- Платежи по кредитам
INSERT INTO loan_payments (loan_id, amount, payment_date, status) VALUES
(1, 10000.00, CURRENT_DATE - INTERVAL '3 months', 'success'),
(1, 10000.00, CURRENT_DATE - INTERVAL '2 months', 'success'),
(1, 10000.00, CURRENT_DATE - INTERVAL '1 month', 'success'),
(2, 30000.00, CURRENT_DATE - INTERVAL '2 months', 'success'),
(3, 8000.00, CURRENT_DATE - INTERVAL '1 month', 'success');
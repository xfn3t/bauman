-- Создание дополнительных баз данных для разных окружений
CREATE DATABASE satellite_dev;
CREATE DATABASE satellite_test;
CREATE DATABASE satellite_local;

-- Создание пользователей для разных окружений
CREATE USER dev_user WITH PASSWORD 'dev_password';
CREATE USER test_user WITH PASSWORD 'test_password';

-- Предоставление прав
GRANT ALL PRIVILEGES ON DATABASE satellite_dev TO dev_user;
GRANT ALL PRIVILEGES ON DATABASE satellite_test TO test_user;
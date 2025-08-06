# Database Setup Guide 🗄️

**Разработчик: Herman Likyanov**

## Обзор базы данных

Electronic Department System использует H2 Database в качестве основной СУБД с поддержкой миграции на PostgreSQL/MySQL для production среды.

## 🔧 Конфигурация H2 Database (по умолчанию)

### Текущие настройки (development):

```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Доступ к H2 Console:
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (оставить пустым)

## 🐘 Настройка PostgreSQL (Production)

### 1. Установка PostgreSQL:

#### Windows:
```bash
# Скачать с официального сайта: https://www.postgresql.org/download/windows/
# Или через Chocolatey:
choco install postgresql
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

### 2. Создание базы данных:
```sql
-- Подключиться к PostgreSQL
sudo -u postgres psql

-- Создать базу данных
CREATE DATABASE electronic_department;

-- Создать пользователя
CREATE USER dept_user WITH PASSWORD 'your_secure_password';

-- Предоставить права
GRANT ALL PRIVILEGES ON DATABASE electronic_department TO dept_user;

-- Выйти
\q
```

### 3. Конфигурация application-prod.properties:
```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/electronic_department
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=dept_user
spring.datasource.password=your_secure_password

# JPA/Hibernate для PostgreSQL
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

### 4. Добавить зависимость в pom.xml:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 🐬 Настройка MySQL (альтернатива)

### 1. Установка MySQL:
```bash
# Windows (через Chocolatey)
choco install mysql

# Linux (Ubuntu/Debian)
sudo apt install mysql-server mysql-client
```

### 2. Создание базы данных:
```sql
-- Подключиться к MySQL
mysql -u root -p

-- Создать базу данных
CREATE DATABASE electronic_department CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Создать пользователя
CREATE USER 'dept_user'@'localhost' IDENTIFIED BY 'your_secure_password';

-- Предоставить права
GRANT ALL PRIVILEGES ON electronic_department.* TO 'dept_user'@'localhost';
FLUSH PRIVILEGES;

-- Выйти
EXIT;
```

### 3. Конфигурация для MySQL:
```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/electronic_department?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=dept_user
spring.datasource.password=your_secure_password

# JPA/Hibernate для MySQL
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
```

### 4. Добавить зависимость в pom.xml:
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 📊 Структура базы данных

### Основные таблицы:

#### users
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN','MANAGER','TEACHER','STUDENT','GUEST')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### students
```sql
CREATE TABLE students (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    student_number VARCHAR(255) NOT NULL UNIQUE,
    enrollment_year INTEGER,
    study_form VARCHAR(50) CHECK (study_form IN ('FULL_TIME','PART_TIME','EVENING','DISTANCE')),
    group_id BIGINT,
    phone_number VARCHAR(255),
    address VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (group_id) REFERENCES student_groups(id)
);
```

#### teachers
```sql
CREATE TABLE teachers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    academic_title VARCHAR(255),
    scientific_degree VARCHAR(255),
    department_position VARCHAR(255),
    phone_number VARCHAR(255),
    office_number VARCHAR(255),
    hire_date DATE,
    biography TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## 🔄 Миграции базы данных

### Использование Flyway (рекомендуется для production):

1. **Добавить зависимость:**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

2. **Конфигурация:**
```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

3. **Создать файлы миграций в `src/main/resources/db/migration/`:**
- `V1__Create_initial_schema.sql`
- `V2__Add_sample_data.sql`

## 🛡️ Безопасность базы данных

### Рекомендации для production:

1. **Использовать переменные окружения:**
```bash
# .env file
DB_URL=jdbc:postgresql://localhost:5432/electronic_department
DB_USERNAME=dept_user
DB_PASSWORD=very_secure_password_123!
```

2. **Spring Boot конфигурация:**
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

3. **SSL соединение:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/electronic_department?sslmode=require
```

## 📋 Backup и восстановление

### PostgreSQL:
```bash
# Backup
pg_dump -U dept_user -h localhost electronic_department > backup.sql

# Restore
psql -U dept_user -h localhost electronic_department < backup.sql
```

### MySQL:
```bash
# Backup
mysqldump -u dept_user -p electronic_department > backup.sql

# Restore
mysql -u dept_user -p electronic_department < backup.sql
```

## 🔍 Мониторинг и логирование

### Включить логирование SQL запросов:
```properties
# Для development
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Для production (осторожно с размером логов)
logging.level.org.hibernate.SQL=WARN
```

### Метрики производительности:
```properties
spring.jpa.properties.hibernate.generate_statistics=true
management.endpoints.web.exposure.include=health,metrics,env
```

## 🚀 Запуск с разными профилями

```bash
# Development (H2)
mvn spring-boot:run

# Production (PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Тестирование (H2 in-memory)
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

---

**Автор конфигурации базы данных: Herman Likyanov**

Для дополнительных вопросов по настройке базы данных обращайтесь к документации Spring Boot и Hibernate.

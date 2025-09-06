# Electronic Department System 📚

> 🎓 **Комплексная система управления электронным деканатом университета**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Educational-yellow.svg)](LICENSE)

---

## 📖 Описание проекта

Electronic Department System - это современная система управления электронным деканатом университета, созданная для полной автоматизации учебного процесса. Система обеспечивает эффективное управление студентами, преподавателями, учебными предметами и академическими оценками.

## 👨‍💻 Автор

**Herman Lukyanov** - разработчик и архитектор системы  
📧 *Для связи и предложений*

---

## 🚀 Технологический стек

| 🏗️ **Категория** | 🛠️ **Технология** | 📝 **Описание** |
|-------------------|-------------------|------------------|
| **Backend Framework** | Spring Boot 3.2.1 | Основной фреймворк приложения |
| **Language** | Java 17 | Язык программирования |
| **Database** | MySQL Database | Основная база данных |
| **ORM** | Hibernate/JPA | Объектно-реляционное отображение |
| **Security** | Spring Security | Безопасность и аутентификация |
| **Build Tool** | Maven | Сборка и управление зависимостями |
| **Documentation** | Swagger/OpenAPI 3 | Документация API |
| **Testing** | JUnit 5, Mockito | Юнит-тестирование |
| **Code Quality** | SonarQube | Анализ качества кода |

## 📋 Функциональность системы

### 🎯 Основные модули:

| 🔧 **Модуль** | 📝 **Описание** | 🎭 **Доступные роли** |
|---------------|-----------------|----------------------|
| **👥 Управление пользователями** | Администрирование учетных записей | ADMIN |
| **🎓 Управление студентами** | Регистрация и управление данными студентов | ADMIN, MANAGER |
| **👩‍🏫 Управление преподавателями** | Профили и информация о преподавателях | ADMIN, MANAGER |
| **📚 Управление предметами** | Каталог учебных дисциплин | ADMIN, MANAGER |
| **📊 Система оценок** | Выставление и управление оценками | TEACHER, ADMIN |
| **👥 Группы студентов** | Организация студентов по группам | MANAGER, ADMIN |

### 🎭 Роли пользователей:

| 👤 **Роль** | 🔑 **Уровень доступа** | 📝 **Основные функции** |
|-------------|------------------------|--------------------------|
| **👑 ADMIN** | Полный доступ | Управление всей системой, пользователями, настройками |
| **🏢 MANAGER** | Управленческий | Управление учебным процессом, студентами, предметами |
| **🎓 TEACHER** | Преподавательский | Работа с оценками, своими предметами и студентами |
| **📚 STUDENT** | Студенческий | Просмотр своих данных, оценок, расписания |
| **👀 GUEST** | Ограниченный | Доступ к публичной информации кафедры |

## 🏗️ Архитектура системы

Проект построен на основе паттерна **Unit of Work** с чистой многослойной архитектурой:

```
📁 Electronic Department System
├── 🌐 pl (Presentation Layer)
│   ├── 🎮 Controllers - REST API контроллеры
│   └── 📄 DTOs - Объекты передачи данных
├── 🧠 bll (Business Logic Layer)  
│   ├── 🔧 Services - Бизнес-логика и правила
│   ├── 🔄 Mappers - Преобразование данных
│   └── 📋 DTOs - Бизнес-объекты
├── 🗄️ dal (Data Access Layer)
│   ├── 📊 Entities - JPA сущности
│   ├── 🔍 Repositories - Доступ к данным
│   └── 🔧 UnitOfWork - Паттерн единицы работы
└── ⚙️ config
    ├── 🔐 Security - Конфигурация безопасности
    ├── 🗃️ Database - Настройки БД
    └── 📚 Swagger - Документация API
```

## 🛠️ Установка и запуск

### ⚡ Требования:
- **Java 17+**
- **Maven 3.6+**
- **MySQL 8.0+**

### 🚀 Пошаговый запуск:

#### 1. 📥 Клонирование репозитория:
```bash
git clone <repository-url>
cd electronic-department
```

#### 2. 🔨 Сборка проекта:
```bash
mvn clean compile
```

#### 3. ▶️ Запуск приложения:
```bash
# Запуск MySQL сервера
sudo service mysql start

# Запуск в режиме разработки
mvn spring-boot:run

# ИЛИ запуск в production режиме (в фоне)
mvn spring-boot:run -Dspring-boot.run.profiles=prod > server.log 2>&1 &
```

#### 4. ⏹️ Остановка процесса:
```bash
pkill -f "spring-boot:run"
```

#### 5. 🗄️ Работа с базой данных:
```bash
# Загрузка БД из дампа
sudo mysql -u dept_user -pb8ef2g6 electronic_department < electronic_department_dump_v****.sql 

# Создание дампа БД
sudo mysqldump -u root electronic_department > electronic_department_dump_v0.6.8.sql
```

#### 6. 🌐 Доступ к приложению:
- **Главное приложение:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html

## 🔐 Безопасность

### 👤 Предустановленные пользователи:

| 👤 **Пользователь** | 🔑 **Пароль** | 🎭 **Роль** | 📝 **Описание** |
|---------------------|---------------|-------------|------------------|
| `admin` | `admin123` | **ADMIN** | 👑 Полный доступ к системе |
| `manager` | `manager123` | **MANAGER** | 🏢 Управление учебным процессом |
| `teacher1` | `teacher123` | **TEACHER** | 🎓 Работа с оценками и предметами |
| `teacher2` | `teacher123` | **TEACHER** | 🎓 Работа с оценками и предметами |
| `student1` | `student123` | **STUDENT** | 📚 Просмотр своих данных и оценок |
| `student2` | `student123` | **STUDENT** | 📚 Просмотр своих данных и оценок |
| `student3` | `student123` | **STUDENT** | 📚 Просмотр своих данных и оценок |
| `guest` | `guest123` | **GUEST** | 👀 Ограниченный доступ к информации |

### 🔧 Генерированный пароль Spring Security:
При каждом запуске система генерирует временный пароль для разработки (отображается в логах консоли).

## 📡 API Endpoints

### 🌐 Публичные endpoints (без авторизации):
- `GET /api/public/teachers` - список активных преподавателей
- `GET /api/public/teachers/{id}` - информация о преподавателе по ID
- `GET /api/public/teachers/search` - поиск преподавателей по имени
- `GET /api/public/subjects` - список активных предметов
- `GET /api/public/subjects/{id}` - информация о предмете по ID
- `GET /api/public/subjects/search` - поиск предметов по названию
- `GET /api/public/students` - список активных студентов
- `GET /api/public/students/search` - поиск студентов по имени
- `GET /api/public/education-levels` - список уровней образования
- `GET /api/public/health` - проверка работоспособности системы
- `GET /api/public/status` - статус системы
- `GET /api/public/department-info` - общая информация о кафедре

### 🔐 Аутентификация:
- `POST /api/auth/login` - авторизация пользователя
- `POST /api/auth/register` - регистрация нового пользователя
- `POST /api/auth/logout` - выход из системы
- `POST /api/auth/guest-login` - авторизация гостя
- `GET /api/auth/current-user` - информация о текущем пользователе

### 🔒 Защищенные endpoints по ролям:

#### 👑 Только для ADMIN:
- `/api/users/**` - управление пользователями (создание, обновление, удаление)

#### 🏢 Для MANAGER и выше:
- `/api/subjects/**` - управление предметами (кроме просмотра)
- `/api/groups/**` - управление группами студентов
- `/api/students/**` - управление студентами

#### 🎓 Для TEACHER и выше:
- `/api/grades/**` - управление оценками
- `/api/teachers/**` - управление информацией о преподавателях
- `/api/archive/**` - доступ к архивным данным

#### 📚 Для STUDENT и выше:
- `GET /api/subjects/**` - просмотр предметов
- `GET /api/users/{id}` - просмотр информации о пользователе
- различные GET endpoints для чтения данных

## 📊 База данных

### 🐬 MySQL Setup (Linux/Ubuntu):

#### 📦 Установка и запуск:
```bash
sudo apt install mysql-server
sudo service mysql start
sudo mysql
```

#### 👤 Создание пользователя:
```sql
CREATE USER 'dept_user'@'localhost' IDENTIFIED WITH 'caching_sha2_password' BY 'b8ef2g6';
GRANT ALL PRIVILEGES ON electronic_department.* TO 'dept_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 🗃️ Создание базы данных:
```bash
sudo mysql -e "CREATE DATABASE electronic_department CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
sudo mysql -e "GRANT ALL PRIVILEGES ON \`electronic_department\`.* TO 'dept_user'@'localhost'; FLUSH PRIVILEGES;"
```

#### 📋 Загрузка данных из дампа:
```bash
# Проверка дампа на наличие команды создания БД
grep -i -m1 -E "CREATE DATABASE|USE " electronic_department_dump_v0.6.9.sql || echo "no CREATE/USE found"

# Загрузка данных
sudo mysql -u dept_user -p electronic_department < electronic_department_dump_v0.6.9.sql
```

### 🗂️ Структура базы данных:

Система использует MySQL database с автоматической инициализацией тестовых данных в режиме разработки.

**Основные таблицы:**
- 👥 `users` - пользователи системы
- 🎓 `students` - информация о студентах  
- 👩‍🏫 `teachers` - информация о преподавателях
- 📚 `subjects` - учебные предметы
- 👥 `student_groups` - группы студентов
- 📊 `grades` - оценки студентов
- 🔗 `teacher_subjects` - связи преподавателей и предметов

## 🧪 Тестирование

### 🎨 PurgeCSS (Оптимизация CSS):
```bash
# Требуется Node.js и npm
sudo npm install -g purgecss

# Очистка CSS файлов
cd /home/mensotor/Desktop/electronic-department-system
purgecss --css ./src/main/resources/static/*.css \
         --content ./src/main/resources/static/*.html ./src/main/resources/static/*.js \
         --output ./src/main/resources/static/purgecss --rejected
```

### 📊 SonarQube Setup:

#### 🐘 PostgreSQL для SonarQube:
```bash
sudo -u postgres psql -c "CREATE USER sonarqube WITH PASSWORD 'sonarqube';"
sudo -u postgres psql -c "CREATE DATABASE sonarqube OWNER sonarqube;"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE sonarqube TO sonarqube;"
sudo -u postgres psql -d sonarqube -c "GRANT ALL ON SCHEMA public TO sonarqube;"
```

#### 🚀 Установка SonarQube:
```bash
sudo mkdir -p /opt/sonarqube && cd /opt/sonarqube
sudo wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-10.7.0.96327.zip
sudo unzip -q sonarqube-10.7.0.96327.zip
sudo useradd -r -s /bin/false sonarqube
sudo chown -R sonarqube:sonarqube /opt/sonarqube/
```

#### ⚙️ Конфигурация SonarQube:
```bash
sudo nano /opt/sonarqube/sonarqube-10.7.0.96327/conf/sonar.properties
```

Добавить в файл:
```properties
sonar.jdbc.username=sonarqube
sonar.jdbc.password=sonarqube
sonar.jdbc.url=jdbc:postgresql://localhost:5432/sonarqube
sonar.web.host=0.0.0.0
sonar.web.port=9000
```

#### 🔄 Запуск как системный сервис:
```bash
sudo systemctl daemon-reload
sudo systemctl enable sonarqube
sudo systemctl start sonarqube
sudo systemctl status sonarqube
```

#### 🌐 Веб-интерфейс SonarQube:
- **URL:** http://localhost:9000
- **Логин по умолчанию:** admin/admin (изменить при первом входе)

#### 🔍 Анализ проекта:

**Шаг 1: Создание токена аутентификации**
1. Откройте http://localhost:9000
2. Войдите как admin/admin (измените пароль при первом входе)
3. Перейдите в **My Account > Security > Generate Token**
4. Введите название токена (например, "maven-analysis")
5. Скопируйте созданный токен

**Шаг 2: Создание проекта (опционально)**
1. В SonarQube нажмите **Create Project**
2. Выберите **Manually**
3. Project Key: `electronic-department-system`
4. Display Name: `Electronic Department System`

**Шаг 3: Запуск анализа**
```bash
# Команда с токеном аутентификации
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=electronic-department-system \
  -Dsonar.projectName="Electronic Department System" \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_GENERATED_TOKEN_HERE

# Или упрощенный вариант (после настройки токена в переменной окружения)
export SONAR_TOKEN=your_token_here
mvn clean verify sonar:sonar

# Альтернатива - только анализ без тестов (быстрее)
mvn sonar:sonar -Dsonar.token=YOUR_TOKEN_HERE
```

**Шаг 4: Просмотр результатов**
- Откройте http://localhost:9000
- Найдите ваш проект в списке
- Изучите отчеты по качеству кода и покрытию

### ⚡ Быстрый старт тестирования:
```bash
# Запуск unit тестов (с автоматическим JaCoCo покрытием)
mvn test

# Запуск тестов без JaCoCo (быстрее)
mvn test -Djacoco.skip=true

# Запуск полной проверки с coverage
mvn clean verify

# Запуск приложения для ручного тестирования
mvn spring-boot:run

# Запуск в production режиме (в фоне)
mvn spring-boot:run -Dspring-boot.run.profiles=prod > server.log 2>&1 &

# Остановка background процесса
pkill -f "spring-boot:run"

# Просмотр coverage отчета (после mvn test)
open target/site/jacoco/index.html  # На macOS
xdg-open target/site/jacoco/index.html  # На Linux
```

### ✅ Что включает тестирование:
- ✅ **61 автоматических unit тестов**
- ✅ **Тестирование с H2 в памяти** (только для тестов)
- ✅ **API тестирование** (публичные и защищенные endpoints)
- ✅ **Аутентификация и авторизация**
- ✅ **CRUD операции**
- ✅ **Обработка ошибок**
- ✅ **JaCoCo покрытие кода**
- ✅ **SonarQube интеграция**

## 🔄 Версионирование

**Текущая версия**: 0.7.0

### История релизов:
- **v0.6.8** - Исправлено отображение страниц для студентов: оценки и дисциплины теперь корректно загружаются из групп, переработана система архивации удаленных и отредактированных оценок под новые типы оценок, улучшено отображение количества групп в дисциплинах
- **v0.6.7** - Доработана система оценок: расширен enum GradeType, исправлена фильтрация оценок для учителей, добавлены колонки "Рівень освіти" и "Форма навчання" на странице студентов, предоставлены полные права администратору
- **v0.6.5** - Доработана система дисциплин и групп для менеджеров: редактирование, добавление, просмотр, списки, фильтрация, поиски, модальные окна, добавление дисциплин, групп, группы в дисциплины, студентов в группы. Оценки на стадии разработки
- **v0.6.2** - Обновление модальных окон групп: добавлен выбор рівня освіти, динамічні курси, розширені фільтри груп, оновлені типи оцінювання дисциплін, виправлення помилок створення дисциплін
- **v0.5.7** - Улучшения UI: скрытие поиска студентов для роли STUDENT, вертикальный скролл в модальных окнах, исправление отображения среднего балла
- **v0.5.6** - Ограничение доступа учителей: студенты и предметы только по назначению
- **v0.5.5** - Добавлен пользователь гость, логика гостя, фильтр поиска в оценках, дамп БД v0.55
- **v0.5.1** - Реализация роле-ориентированного доступа (Role-Based Access Control)
- **v0.5.0** - Полный редизайн системы
- **v0.3.0** - Полная система аутентификации и тестирования
- **v0.2.0** - Comprehensive unit test suite (37 тестов)
- **v0.1-SNAPSHOT** - Первоначальная версия с базовой функциональностью

## 📞 Поддержка и связь

### 👨‍💻 Разработчик:
**Herman Lukyanov**  
📧 Для вопросов и предложений по улучшению системы

### 🤝 Вклад в проект:
Мы приветствуем вклад в развитие проекта! Если у вас есть идеи по улучшению или вы нашли ошибку, создайте Issue или Pull Request.

### 🐛 Баг-репорты:
При обнаружении ошибок, пожалуйста, предоставьте:
- Описание проблемы
- Шаги для воспроизведения
- Ожидаемое поведение
- Логи системы (если есть)

---

## 📝 Лицензия

**Этот проект разработан в учебных целях.**  
© 2025 Herman Lukyanov. Все права защищены.


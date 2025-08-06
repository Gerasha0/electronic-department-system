# Electronic Department System 📚

## Описание проекта

Electronic Department System - это комплексная система управления электронным деканатом университета, разработанная для автоматизации учебного процесса, управления студентами, преподавателями, предметами и оценками.

## 👨‍💻 Автор

**Herman Likyanov** - разработчик и архитектор системы

---

## 🚀 Технологии

- **Backend Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: H2 Database (in-memory)
- **ORM**: Hibernate/JPA
- **Security**: Spring Security
- **Build Tool**: Maven
- **Documentation**: Swagger/OpenAPI 3
- **Testing**: JUnit 5, Mockito

## 📋 Функциональность

### Основные модули:
- **Управление пользователями** - администрирование пользователей системы
- **Управление студентами** - регистрация и управление данными студентов
- **Управление преподавателями** - профили и информация о преподавателях
- **Управление предметами** - каталог учебных дисциплин
- **Система оценок** - выставление и управление оценками
- **Группы студентов** - организация студентов по группам

### Роли пользователей:
- **ADMIN** - полный доступ к системе
- **MANAGER** - управление учебным процессом
- **TEACHER** - работа с оценками и предметами
- **STUDENT** - просмотр своих данных и оценок
- **GUEST** - ограниченный доступ к публичной информации

## 🏗️ Архитектура

Проект построен на основе паттерна **Unit of Work** с многослойной архитектурой:

```
├── pl (Presentation Layer) - Контроллеры и REST API
├── bll (Business Logic Layer) - Бизнес-логика и сервисы
├── dal (Data Access Layer) - Репозитории и работа с БД
└── config - Конфигурация Spring и безопасности
```

## 🛠️ Установка и запуск

### Требования:
- Java 17+
- Maven 3.6+

### Запуск приложения:

1. **Клонирование репозитория:**
```bash
git clone <repository-url>
cd electronic-department
```

2. **Сборка проекта:**
```bash
mvn clean compile
```

3. **Запуск приложения:**
```bash
mvn spring-boot:run
```

4. **Доступ к приложению:**
- Главное приложение: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

## 🔐 Безопасность

### Предустановленные пользователи:

| Пользователь | Пароль | Роль |
|-------------|--------|------|
| admin | admin123 | ADMIN |
| manager | manager123 | MANAGER |
| teacher1 | teacher123 | TEACHER |
| teacher2 | teacher123 | TEACHER |
| student1 | student123 | STUDENT |
| student2 | student123 | STUDENT |
| student3 | student123 | STUDENT |
| guest | guest123 | GUEST |

### Генерированный пароль Spring Security:
При каждом запуске система генерирует временный пароль для разработки (отображается в логах).

## 📡 API Endpoints

### Публичные endpoints (без авторизации):
- `GET /api/public/teachers` - список преподавателей
- `GET /api/public/subjects` - список предметов
- `GET /h2-console` - консоль базы данных

### Защищенные endpoints:
- `/api/admin/**` - только для администраторов
- `/api/manager/**` - для менеджеров и выше
- `/api/teacher/**` - для преподавателей и выше
- `/api/student/**` - для студентов и выше

## 📊 База данных

Система использует H2 in-memory database с автоматической инициализацией тестовых данных.

### Основные таблицы:
- `users` - пользователи системы
- `students` - информация о студентах
- `teachers` - информация о преподавателях
- `subjects` - учебные предметы
- `student_groups` - группы студентов
- `grades` - оценки студентов
- `teacher_subjects` - связи преподавателей и предметов

## 🧪 Тестирование

Запуск тестов:
```bash
mvn test
```

Система включает интеграционные тесты для проверки:
- Загрузки контекста Spring
- Инициализации данных
- Работы основных компонентов

## 📖 Документация

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **Database README**: [DATABASE.md](DATABASE.md)
- **UI Setup README**: [UI_SETUP.md](UI_SETUP.md)
- **UML Diagrams**: [diagrams/](diagrams/)

## 🔄 Версионирование

**Текущая версия**: 0.1-SNAPSHOT

## 📞 Поддержка

Для вопросов и предложений обращайтесь к разработчику:
**Herman Likyanov**

---

## 📝 Лицензия

Этот проект разработан в учебных целях.


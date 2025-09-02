# Развертывание проекта Electronic Department System v0.4.0

## Требования
- Docker и Docker Compose
- Java 17+
- Maven 3.6+

## Быстрый запуск с Docker

### 1. Запуск MySQL в Docker
```bash
docker run --name mysql-electronic-dept -e MYSQL_ROOT_PASSWORD=rootpassword -e MYSQL_DATABASE=electronic_department -e MYSQL_USER=dept_user -e MYSQL_PASSWORD=b8ef2g6 -p 3306:3306 -d mysql:8.0
```

### 2. Восстановление базы данных
```bash
# Скопировать дамп в контейнер
docker cp electronic_department_dump_v0.4.sql mysql-electronic-dept:/tmp/

# Восстановить базу данных
docker exec -i mysql-electronic-dept mysql -u dept_user -pb8ef2g6 electronic_department < electronic_department_dump_v0.4.sql
```

### 3. Запуск приложения
```bash
# Сборка проекта
mvn clean package

# Запуск в production режиме
java -jar target/electronic-department-0.4.0.jar --spring.profiles.active=prod
```

## Доступ к приложению
- Веб-интерфейс: http://localhost:8080
- API документация: http://localhost:8080/swagger-ui.html
- API: http://localhost:8080/api-docs

## Тестовые пользователи
- **Администратор**: admin / admin123
- **Преподаватель**: teacher1 / password123
- **Студент**: student1 / password123

## Структура базы данных
База данных содержит:
- Пользователи (users)
- Студенты (students) 
- Преподаватели (teachers)
- Группы студентов (student_groups)
- Дисциплины (subjects)
- Оценки (grades)

## Версия 0.4.0 включает:
- Полнофункциональный веб-интерфейс
- REST API для всех сущностей
- Управление дисциплинами с назначением преподавателей
- Система оценок с различными типами
- Аутентификация и авторизация
- Интеграция с MySQL

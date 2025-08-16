# 🚀 Быстрые команды для тестирования

## Запуск приложения
```bash
mvn spring-boot:run
```

## Быстрая проверка API
```bash
# Проверка состояния
curl http://localhost:8080/api/public/health

# Статус системы  
curl http://localhost:8080/api/public/status

# Вход администратора
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"admin","password":"admin123"}'

# Список преподавателей
curl http://localhost:8080/api/public/teachers

# Список предметов
curl http://localhost:8080/api/public/subjects
```

## H2 Console
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `SA`
- Password: (пустой)

## Swagger UI
- URL: http://localhost:8080/swagger-ui.html

## Тестовые пользователи
- admin / admin123 (ADMIN)
- teacher1 / teacher123 (TEACHER)  
- student1 / student123 (STUDENT)

📋 Полное руководство: [TESTING_GUIDE.md](TESTING_GUIDE.md)

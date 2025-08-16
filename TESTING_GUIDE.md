# 🧪 Руководство по тестированию Electronic Department System

## 📋 Содержание
- [Автоматические тесты](#автоматические-тесты)
- [Запуск приложения](#запуск-приложения)
- [База данных H2](#база-данных-h2)
- [Тестирование API](#тестирование-api)
- [Аутентификация](#аутентификация)
- [Тестовые пользователи](#тестовые-пользователи)
- [Защищенные endpoints](#защищенные-endpoints)
- [Swagger документация](#swagger-документация)
- [Проверка ошибок](#проверка-ошибок)

## 🚀 Запуск приложения

### Предварительные требования
- Java 17+
- Maven 3.6+

### Команды запуска
```bash
# Запуск тестов
mvn test

# Запуск приложения
mvn spring-boot:run
```

Приложение будет доступно по адресу: **http://localhost:8080**

## 🧪 Автоматические тесты

### Запуск unit тестов
```bash
mvn test
```

**Ожидаемый результат:** 37 тестов должны пройти успешно
- GradeServiceTest: 14 тестов
- UserServiceTest: 14 тестов  
- UserControllerTest: 7 тестов
- AppTest: 2 теста

## 🗄️ База данных H2

### Доступ к H2 Console
1. Откройте браузер: http://localhost:8080/h2-console
2. **ВАЖНО:** Используйте правильные настройки подключения:
   - **JDBC URL:** `jdbc:h2:mem:testdb`
   - **User Name:** `SA`
   - **Password:** оставьте **ПУСТЫМ**
   - **Driver Class:** `org.h2.Driver`
3. Нажмите "Connect"

### Тестовые SQL запросы
```sql
-- Проверка всех пользователей
SELECT * FROM USERS;

-- Проверка студентов с их группами
SELECT s.*, sg.group_name FROM STUDENTS s 
JOIN STUDENT_GROUPS sg ON s.group_id = sg.id;

-- Проверка преподавателей
SELECT t.*, u.first_name, u.last_name FROM TEACHERS t 
JOIN USERS u ON t.user_id = u.id;

-- Проверка оценок
SELECT g.*, u.first_name, u.last_name, sub.subject_name 
FROM GRADES g 
JOIN STUDENTS s ON g.student_id = s.id 
JOIN USERS u ON s.user_id = u.id 
JOIN SUBJECTS sub ON g.subject_id = sub.id;
```

## 🌐 Тестирование API

### Публичные endpoints (не требуют аутентификации)

```bash
# Проверка состояния системы
curl http://localhost:8080/api/public/health
# Ожидаемый ответ: "OK - Electronic Department System is running"

# Статус системы
curl http://localhost:8080/api/public/status
# Ожидаемый ответ: JSON с версией и временем

# Информация о кафедре
curl http://localhost:8080/api/public/department-info

# Список активных преподавателей
curl http://localhost:8080/api/public/teachers

# Список активных предметов  
curl http://localhost:8080/api/public/subjects

# Поиск преподавателей
curl "http://localhost:8080/api/public/teachers/search?name=Иван"

# Поиск предметов
curl "http://localhost:8080/api/public/subjects/search?name=Программирование"
```

## 🔐 Тестовые пользователи

| Логин | Пароль | Роль | Описание |
|-------|--------|------|----------|
| admin | admin123 | ADMIN | Администратор системы |
| manager | manager123 | MANAGER | Менеджер кафедры |
| teacher1 | teacher123 | TEACHER | Преподаватель Иван Иванов |
| teacher2 | teacher123 | TEACHER | Преподаватель Петро Петров |
| student1 | student123 | STUDENT | Студент Сергій Сидоров |
| student2 | student123 | STUDENT | Студент Анна Коваленко |
| student3 | student123 | STUDENT | Студент Олексій Мороз |
| guest | guest123 | GUEST | Гостевой пользователь |

## 🔑 Аутентификация

### Тестирование входа в систему

#### Создание тестовых файлов
```bash
# Для администратора
echo '{"username":"admin","password":"admin123"}' > admin-login.json

# Для преподавателя  
echo '{"username":"teacher1","password":"teacher123"}' > teacher-login.json

# Для студента
echo '{"username":"student1","password":"student123"}' > student-login.json
```

#### Тестирование аутентификации
```bash
# Успешный вход администратора
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "@admin-login.json"

# Успешный вход преподавателя
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "@teacher-login.json"

# Неудачный вход (неверные данные)
echo '{"username":"wronguser","password":"wrongpass"}' > wrong-login.json
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "@wrong-login.json"
```

### Получение информации о текущем пользователе
```bash
curl http://localhost:8080/api/auth/current-user
```

### Выход из системы
```bash
curl -X POST http://localhost:8080/api/auth/logout
```

## 🔒 Защищенные endpoints

**Важно:** Эти endpoints требуют аутентификации и соответствующих ролей.

### Endpoints для студентов и выше (STUDENT+)
```bash
# Список всех пользователей
curl http://localhost:8080/api/users

# Информация о конкретном пользователе
curl http://localhost:8080/api/users/1

# Список оценок
curl http://localhost:8080/api/grades

# Список предметов
curl http://localhost:8080/api/subjects
```

### Endpoints для преподавателей и выше (TEACHER+)  
```bash
# Список групп студентов
curl http://localhost:8080/api/student-groups

# Список преподавателей
curl http://localhost:8080/api/teachers

# Создание новой оценки
curl -X POST http://localhost:8080/api/grades \
  -H "Content-Type: application/json" \
  -d '{"studentId":1,"subjectId":1,"teacherId":1,"gradeValue":85,"gradeType":"CURRENT"}'
```

### Endpoints для менеджеров и выше (MANAGER+)
```bash
# Управление группами студентов
curl -X POST http://localhost:8080/api/student-groups \
  -H "Content-Type: application/json" \
  -d '{"groupName":"КН-123","courseYear":2,"studyForm":"FULL_TIME","startYear":2023}'
```

### Endpoints только для администраторов (ADMIN)
```bash
# Создание пользователя
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"password123","email":"new@university.ua","firstName":"Новый","lastName":"Пользователь","role":"STUDENT"}'

# Удаление пользователя
curl -X DELETE http://localhost:8080/api/users/5
```

## 📚 Swagger документация

### Доступ к Swagger UI
Откройте в браузере: **http://localhost:8080/swagger-ui.html**

### Что проверить в Swagger:
1. Все контроллеры отображаются корректно
2. Endpoints сгруппированы по функциональности
3. Можно тестировать API через веб-интерфейс
4. Схемы DTO отображаются правильно

## 🚨 Проверка обработки ошибок

### Тестирование различных сценариев ошибок

```bash
# 1. Неверные данные для входа
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"wronguser","password":"wrongpass"}'
# Ожидается: HTTP 401 Unauthorized

# 2. Несуществующий endpoint
curl http://localhost:8080/api/nonexistent
# Ожидается: HTTP 404 Not Found

# 3. Доступ к защищенному endpoint без аутентификации
curl http://localhost:8080/api/users
# Ожидается: HTTP 403 Forbidden

# 4. Несуществующий пользователь
curl http://localhost:8080/api/users/999
# Ожидается: HTTP 404 Not Found

# 5. Некорректные данные при создании
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"","password":"123"}'  
# Ожидается: HTTP 400 Bad Request с описанием ошибок валидации
```

## 🎯 Checklist для полного тестирования

### ✅ Базовая функциональность
- [ ] Приложение запускается без ошибок
- [ ] H2 Console доступна с правильными настройками подключения
- [ ] Все 37 unit тестов проходят успешно
- [ ] База данных инициализируется тестовыми данными

### ✅ Публичные API
- [ ] `/api/public/health` возвращает статус OK
- [ ] `/api/public/status` возвращает корректную информацию
- [ ] `/api/public/teachers` возвращает список преподавателей
- [ ] `/api/public/subjects` возвращает список предметов
- [ ] Поиск работает корректно

### ✅ Аутентификация  
- [ ] Вход с правильными данными работает
- [ ] Вход с неверными данными возвращает ошибку
- [ ] Все тестовые пользователи могут войти в систему
- [ ] Выход из системы работает корректно

### ✅ Авторизация
- [ ] Публичные endpoints доступны без аутентификации
- [ ] Защищенные endpoints требуют соответствующих ролей
- [ ] Ролевая модель работает корректно (ADMIN > MANAGER > TEACHER > STUDENT > GUEST)

### ✅ CRUD операции
- [ ] Создание новых записей работает
- [ ] Чтение данных работает  
- [ ] Обновление записей работает
- [ ] Удаление записей работает (для соответствующих ролей)

### ✅ Обработка ошибок
- [ ] Корректные HTTP статус коды
- [ ] Информативные сообщения об ошибках
- [ ] Валидация входных данных работает

### ✅ Документация
- [ ] Swagger UI доступен и функционален
- [ ] Все endpoints документированы
- [ ] Схемы данных отображаются корректно

## 📝 Примечания

1. **Безопасность**: Пароли в тестовой среде простые для удобства тестирования. В продакшене должны использоваться сложные пароли.

2. **База данных**: Используется in-memory H2, данные не сохраняются между перезапусками.

3. **Сессии**: Приложение настроено на stateless режим. Для полнофункционального тестирования защищенных endpoints может потребоваться настройка сессионной аутентификации или JWT токенов.

4. **Порт**: По умолчанию приложение запускается на порту 8080. Убедитесь, что порт свободен.

5. **Логи**: Следите за логами в консоли для диагностики проблем.

## 🆘 Возможные проблемы и решения

### Проблема: H2 Console не подключается
**Решение**: Убедитесь, что используете точный JDBC URL: `jdbc:h2:mem:testdb`

### Проблема: API возвращает 403 Forbidden  
**Решение**: Проверьте, что endpoint не требует аутентификации или вы правильно аутентифицированы

### Проблема: Приложение не запускается
**Решение**: Проверьте, что порт 8080 свободен и используется Java 17+

### Проблема: Тесты не проходят
**Решение**: Выполните `mvn clean test` для чистого запуска тестов

---
**Версия документа**: 1.0  
**Дата обновления**: 07.08.2025  
**Версия приложения**: 0.2.0

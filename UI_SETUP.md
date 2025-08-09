# UI Setup Guide 🖥️

**Разработчик: Herman Lukyanov**

## Обзор UI архитектуры

Electronic Department System поддерживает несколько вариантов пользовательского интерфейса:
- **REST API** (текущая реализация)
- **Web-интерфейс** (React/Angular/Vue.js)
- **Desktop приложение** (JavaFX/Electron)
- **Mobile приложение** (React Native/Flutter)

---

## 🌐 Web-интерфейс (рекомендуется)

### Вариант 1: React + TypeScript

#### Структура проекта:
```
electronic-department-ui/
├── public/
├── src/
│   ├── components/
│   ├── pages/
│   ├── services/
│   ├── types/
│   └── utils/
├── package.json
└── README.md
```

#### Установка и настройка:

1. **Создание React приложения:**
```bash
npx create-react-app electronic-department-ui --template typescript
cd electronic-department-ui
```

2. **Установка зависимостей:**
```bash
npm install axios react-router-dom @mui/material @emotion/react @emotion/styled
npm install @types/react-router-dom --save-dev
```

3. **Конфигурация API клиента:**
```typescript
// src/services/api.ts
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptors for authentication
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

4. **Типы данных:**
```typescript
// src/types/index.ts
export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'ADMIN' | 'MANAGER' | 'TEACHER' | 'STUDENT' | 'GUEST';
}

export interface Student {
  id: number;
  studentNumber: string;
  firstName: string;
  lastName: string;
  groupName: string;
  enrollmentYear: number;
}

export interface Teacher {
  id: number;
  firstName: string;
  lastName: string;
  academicTitle: string;
  departmentPosition: string;
  subjects: string[];
}
```

5. **Основные компоненты:**
```typescript
// src/components/StudentList.tsx
import React, { useEffect, useState } from 'react';
import { Student } from '../types';
import { apiClient } from '../services/api';

const StudentList: React.FC = () => {
  const [students, setStudents] = useState<Student[]>([]);

  useEffect(() => {
    const fetchStudents = async () => {
      try {
        const response = await apiClient.get('/students');
        setStudents(response.data);
      } catch (error) {
        console.error('Error fetching students:', error);
      }
    };

    fetchStudents();
  }, []);

  return (
    <div>
      <h2>Список студентов</h2>
      {students.map(student => (
        <div key={student.id}>
          {student.firstName} {student.lastName} - {student.groupName}
        </div>
      ))}
    </div>
  );
};

export default StudentList;
```

6. **Запуск:**
```bash
npm start
# Приложение будет доступно на http://localhost:3000
```

### Вариант 2: Angular

#### Создание проекта:
```bash
npm install -g @angular/cli
ng new electronic-department-ui
cd electronic-department-ui
ng add @angular/material
```

#### Генерация сервисов:
```bash
ng generate service services/api
ng generate component components/student-list
ng generate component components/teacher-list
```

### Вариант 3: Vue.js

#### Создание проекта:
```bash
npm install -g @vue/cli
vue create electronic-department-ui
cd electronic-department-ui
vue add vuetify
```

---

## 🖥️ Desktop приложение

### Вариант 1: JavaFX (рекомендуется для Java разработчиков)

#### Добавление в pom.xml:
```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>19</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>19</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-maven-plugin</artifactId>
            <version>0.0.8</version>
            <configuration>
                <mainClass>com.kursova.ui.DesktopApp</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```

#### Структура JavaFX приложения:
```
src/main/java/com/kursova/ui/
├── DesktopApp.java
├── controllers/
│   ├── MainController.java
│   ├── StudentController.java
│   └── TeacherController.java
└── fxml/
    ├── main.fxml
    ├── student-list.fxml
    └── teacher-list.fxml
```

#### Основной класс приложения:
```java
// src/main/java/com/kursova/ui/DesktopApp.java
package com.kursova.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DesktopApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);
        
        primaryStage.setTitle("Electronic Department System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
```

#### Запуск JavaFX:
```bash
mvn javafx:run
```

### Вариант 2: Electron (Cross-platform)

#### Создание Electron приложения:
```bash
mkdir electronic-department-desktop
cd electronic-department-desktop
npm init -y
npm install electron --save-dev
```

#### package.json:
```json
{
  "main": "main.js",
  "scripts": {
    "start": "electron .",
    "build": "electron-builder"
  },
  "devDependencies": {
    "electron": "^22.0.0",
    "electron-builder": "^23.0.0"
  }
}
```

---

## 📱 Mobile приложение

### Вариант 1: React Native

#### Установка и создание проекта:
```bash
npx react-native init ElectronicDepartmentMobile
cd ElectronicDepartmentMobile
```

#### Установка зависимостей:
```bash
npm install @react-navigation/native @react-navigation/stack
npm install react-native-screens react-native-safe-area-context
npm install axios react-native-async-storage
```

#### Основной компонент:
```javascript
// App.js
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import StudentListScreen from './src/screens/StudentListScreen';
import TeacherListScreen from './src/screens/TeacherListScreen';

const Stack = createStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Students">
        <Stack.Screen name="Students" component={StudentListScreen} />
        <Stack.Screen name="Teachers" component={TeacherListScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
```

### Вариант 2: Flutter

#### Создание проекта:
```bash
flutter create electronic_department_mobile
cd electronic_department_mobile
```

#### Добавление зависимостей в pubspec.yaml:
```yaml
dependencies:
  flutter:
    sdk: flutter
  http: ^0.13.5
  shared_preferences: ^2.0.15
  provider: ^6.0.3
```

#### Основной Dart файл:
```dart
// lib/main.dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'services/api_service.dart';
import 'screens/home_screen.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        Provider<ApiService>(create: (_) => ApiService()),
      ],
      child: MaterialApp(
        title: 'Electronic Department',
        theme: ThemeData(primarySwatch: Colors.blue),
        home: HomeScreen(),
      ),
    );
  }
}
```

---

## 🔐 Аутентификация в UI

### Spring Security Basic Auth:

#### JavaScript (для Web):
```javascript
// src/services/auth.js
class AuthService {
  static async login(username, password) {
    try {
      const credentials = btoa(`${username}:${password}`);
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Basic ${credentials}`
        }
      });
      
      if (response.ok) {
        localStorage.setItem('auth_credentials', credentials);
        return true;
      }
      return false;
    } catch (error) {
      console.error('Login error:', error);
      return false;
    }
  }
  
  static getAuthHeader() {
    const credentials = localStorage.getItem('auth_credentials');
    return credentials ? `Basic ${credentials}` : null;
  }
}
```

---

## 🎨 UI/UX Рекомендации

### Material Design Components:
- **React**: @mui/material
- **Angular**: @angular/material
- **Vue**: Vuetify
- **Flutter**: встроенные Material компоненты

### Цветовая схема:
```css
:root {
  --primary-color: #1976d2;
  --secondary-color: #dc004e;
  --background-color: #f5f5f5;
  --text-color: #212121;
  --card-background: #ffffff;
}
```

### Responsive дизайн:
```css
/* Mobile First */
.container {
  padding: 16px;
}

@media (min-width: 768px) {
  .container {
    padding: 24px;
    max-width: 1200px;
    margin: 0 auto;
  }
}
```

---

## 🚀 Развертывание UI

### Web приложение:

#### Netlify:
```bash
npm run build
# Загрузить папку build на Netlify
```

#### Vercel:
```bash
npm install -g vercel
vercel --prod
```

#### Docker:
```dockerfile
# Dockerfile для React
FROM node:16-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Desktop приложение:

#### JavaFX с jpackage:
```bash
mvn clean javafx:jlink
jpackage --type exe --input target/app --main-jar electronic-department.jar
```

### Mobile приложение:

#### React Native:
```bash
# Android
npx react-native run-android

# iOS
npx react-native run-ios
```

#### Flutter:
```bash
flutter build apk
flutter build ios
```

---

## 📊 Мониторинг и аналитика

### Google Analytics для Web:
```javascript
// gtag configuration
gtag('config', 'GA_MEASUREMENT_ID', {
  page_title: 'Electronic Department System',
  page_location: window.location.href
});
```

### Crash Reporting:
- **Web**: Sentry
- **Mobile**: Firebase Crashlytics
- **Desktop**: Custom logging

---

**Автор UI архитектуры: Herman Lukyanov**

Для выбора оптимального UI решения рекомендуется начать с Web-интерфейса на React, так как он обеспечивает быструю разработку и легкое развертывание.

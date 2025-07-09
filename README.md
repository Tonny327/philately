# Philatelia - Приложение для филателистов 🎯

**Philatelia** - это современное Android-приложение для любителей филателии (коллекционирования почтовых марок). Приложение предоставляет comprehensive платформу для изучения, покупки и обмена почтовыми марками, а также включает AI-помощника для экспертных советов.

## ✨ Основные функции

### 📖 Каталог марок
- Обширная база данных почтовых марок с детальными описаниями
- Поиск и фильтрация по различным критериям
- Высококачественные изображения марок
- Информация о ценах и наличии

### 🤖 AI-помощник филателиста
- Интеллектуальный помощник на базе DeepSeek API
- Экспертные советы по филателии
- Идентификация марок по описанию
- Историческая информация о марках
- Оценка стоимости и редкости

### 🌍 Postcrossing
- Интеграция с мировым сообществом postcrossing
- Обмен открытками с коллекционерами по всему миру
- Система регистрации и статистики
- Опросы и голосования сообщества

### 🛒 Система покупок
- Добавление марок в корзину
- История заказов
- Отслеживание статуса заказов

### 👤 Профиль пользователя
- Персональная информация
- История активности
- Настройки приложения
- Система обратной связи

## 🏗️ Архитектура

Приложение построено на современных принципах архитектуры Android:

- **MVVM** - Model-View-ViewModel архитектура
- **Navigation Component** - для навигации между экранами
- **Room Database** - для локального кеширования
- **Firebase** - для аутентификации и cloud storage
- **Retrofit** - для работы с REST API
- **Glide** - для загрузки изображений

### 📱 Структура экранов

```
MainActivity
├── CatalogFragment (Каталог марок)
├── PostcrossingFragment (Postcrossing)
├── HelperFragment (AI-помощник)
├── CartFragment (Корзина)
└── UserFragment (Профиль)

Дополнительные активности:
├── LoginActivity (Вход)
├── RegisterActivity (Регистрация)
├── UserProfileActivity (Профиль пользователя)
├── OrderHistoryActivity (История заказов)
├── FeedbackActivity (Обратная связь)
└── ChatHistoryActivity (История чатов)
```

## 🛠️ Технологии

### Core
- **Java** - основной язык разработки
- **Android SDK** - платформа разработки
- **Gradle** - система сборки

### UI/UX
- **Material Design** - дизайн-система
- **Navigation Component** - навигация
- **SwipeRefreshLayout** - pull-to-refresh
- **RecyclerView** - списки и сетки

### Сетевое взаимодействие
- **Retrofit** - HTTP клиент
- **OkHttp** - HTTP/HTTP2 клиент
- **Gson** - JSON сериализация
- **JSoup** - HTML парсинг

### База данных
- **Firebase Auth** - аутентификация
- **Firebase Firestore** - облачная база данных
- **Firebase Storage** - файловое хранилище
- **Room** - локальная база данных

### AI интеграция
- **DeepSeek API** - AI модель для филателии
- **Markwon** - рендеринг Markdown

### Дополнительные инструменты
- **Glide** - загрузка изображений
- **Google Play Services** - авторизация через Google

## 🚀 Установка и запуск

### Требования
- Android Studio Arctic Fox (2020.3.1) или выше
- Android SDK API 24+ (Android 7.0)
- JDK 17
- Firebase проект с настроенными сервисами

### Настройка проекта

1. **Клонирование репозитория**
   ```bash
   git clone https://github.com/yourusername/philatelia.git
   cd philatelia
   ```

2. **Настройка Firebase**
   - Создайте проект в [Firebase Console](https://console.firebase.google.com/)
   - Добавьте Android приложение с package name `com.example.philatelia`
   - Скачайте `google-services.json` и поместите в папку `app/`
   - Настройте Authentication, Firestore и Storage

3. **Настройка API ключей**
   
   Создайте файл `gradle.properties` в корне проекта:
   ```properties
   MISTRAL_API_KEY=your_deepseek_api_key_here
   ```

4. **Сборка проекта**
   ```bash
   ./gradlew assembleDebug
   ```

### Структура данных

#### Марки (stamps.json)
```json
{
  "title": "Название марки",
  "price": "Цена в рублях",
  "imageUrl": "URL изображения марки"
}
```

#### Аналитика (stamp_analytics.json)
```json
{
  "year": "Год",
  "count": "Количество",
  "details": "Детали"
}
```

## 📊 Структура проекта

```
app/src/main/java/com/example/philatelia/
├── adapters/          # RecyclerView адаптеры
├── data/              # Модели данных и DAO
├── fragments/         # UI фрагменты
├── helpers/           # Вспомогательные классы
├── models/            # Модели данных
├── network/           # Сетевые интерфейсы
├── repositories/      # Репозитории данных
├── viewmodels/        # ViewModel классы
├── MainActivity.java  # Основная активность
├── LoginActivity.java # Авторизация
└── ...

app/src/main/res/
├── layout/            # XML макеты
├── drawable/          # Графические ресурсы
├── values/            # Строки, цвета, стили
└── navigation/        # Навигационные графы
```

## 🔧 Конфигурация

### Переменные окружения
```properties
# gradle.properties
MISTRAL_API_KEY=your_api_key
```

### Firebase конфигурация
- Authentication: Email/Password, Google Sign-In
- Firestore: коллекции пользователей, заказов, отзывов
- Storage: изображения марок, аватары пользователей

### Сетевая безопасность
```xml
<!-- app/src/main/res/xml/network_security_config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">api.deepinfra.com</domain>
    </domain-config>
</network-security-config>
```

## 🎨 Дизайн

Приложение следует принципам Material Design с кастомной цветовой схемой:

- **Основной цвет**: Синий (#2196F3)
- **Акцентный цвет**: Оранжевый (#FF9800)
- **Фон**: Белый/Темно-серый
- **Шрифт**: Roboto

### Компоненты UI
- Кастомные кнопки с округленными углами
- Карточки с тенями
- Плавные анимации переходов
- Адаптивная нижняя навигация

## 📱 Минимальные требования

- **Android**: 7.0 (API 24) и выше
- **RAM**: 2 GB
- **Хранилище**: 100 MB свободного места
- **Интернет**: Обязательно для основных функций

## 🤝 Участие в разработке

1. Сделайте Fork репозитория
2. Создайте feature branch (`git checkout -b feature/amazing-feature`)
3. Commit изменения (`git commit -m 'Add amazing feature'`)
4. Push в branch (`git push origin feature/amazing-feature`)
5. Создайте Pull Request

### Стиль кода
- Следуйте Java Code Style
- Используйте осмысленные имена переменных
- Добавляйте комментарии к сложному коду


### Планируемые обновления
- 🔄 Офлайн режим
- 🔄 QR код сканирование
- 🔄 Социальные функции
- 🔄 Расширенная аналитика
- 🔄 Unit и UI тесты

---

<p align="center">
  Сделано с ❤️ для сообщества филателистов
</p> 
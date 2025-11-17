# 🏗️ АРХИТЕКТУРА ШАХМАТНОГО ПРИЛОЖЕНИЯ

## 📁 Структура проекта

```
app/src/main/kotlin/com/chess/game/
├── ChessApplication.kt              # Application класс с инициализацией SDK
├── ads/                             # Модуль рекламы
│   └── AdsManager.kt               # Менеджер Yandex Mobile Ads SDK
├── core/                            # Общие утилиты
├── data/                            # Слой данных
│   └── preferences/                # SharedPreferences для настроек и статистики
├── domain/                          # Бизнес-логика
│   ├── model/                      # Модели данных
│   │   ├── Position.kt             # Позиция на доске
│   │   ├── PieceColor.kt           # Цвет фигуры (WHITE/BLACK)
│   │   ├── PieceType.kt            # Тип фигуры (PAWN, KNIGHT, etc.)
│   │   ├── Piece.kt                # Шахматная фигура
│   │   ├── Move.kt                 # Шахматный ход
│   │   ├── ChessBoard.kt           # Состояние доски
│   │   ├── GameState.kt            # Состояние игры (Playing, Check, Checkmate, etc.)
│   │   └── GameDifficulty.kt       # Уровни сложности AI
│   └── engine/                     # Шахматный движок
│       ├── MoveGenerator.kt        # Генератор ходов для всех фигур
│       ├── ChessEngine.kt          # Валидация ходов, проверка шахов/матов
│       ├── BoardEvaluator.kt       # Оценка позиции для AI
│       └── ChessAI.kt              # AI с Minimax и Alpha-Beta отсечением
└── ui/                              # UI слой
    ├── splash/                     # Экран загрузки
    ├── menu/                       # Главное меню
    ├── game/                       # Игровой экран
    ├── settings/                   # Настройки
    ├── stats/                      # Статистика
    └── common/                     # Общие UI компоненты
```

## 🎮 ШАХМАТНЫЙ ДВИЖОК

### 1. Модели данных

**Position** (Position.kt)
- Позиция на доске (row: 0-7, col: 0-7)
- Методы: `toAlgebraic()`, `fromAlgebraic()`

**Piece** (Piece.kt)
- Тип фигуры (PieceType)
- Цвет (PieceColor)
- Флаг hasMoved (для рокировки и первого хода пешки)

**Move** (Move.kt)
- Начальная и конечная позиция
- Поддержка специальных ходов:
  - Рокировка (isCastling)
  - Взятие на проходе (isEnPassant)
  - Превращение пешки (isPromotion)

**ChessBoard** (ChessBoard.kt)
- Массив 8x8 с фигурами
- История ходов
- Методы: `getPiece()`, `makeMove()`, `findKing()`

### 2. Генерация ходов (MoveGenerator.kt)

Генерирует все возможные ходы для каждой фигуры:

- **Пешка**: ход вперед на 1-2 клетки, захват по диагонали, превращение
- **Конь**: L-образные ходы
- **Слон**: диагональные линии
- **Ладья**: горизонтальные/вертикальные линии
- **Ферзь**: комбинация слона и ладьи
- **Король**: 1 клетка в любом направлении + рокировка

### 3. Валидация (ChessEngine.kt)

- `getLegalMoves()` - получить легальные ходы (с учетом шахов)
- `isKingInCheck()` - проверка шаха
- `isCheckmate()` - проверка мата
- `isStalemate()` - проверка пата
- `getGameState()` - получить текущее состояние игры

## 🤖 AI ДВИЖОК

### Уровни сложности

#### 1️⃣ НОВИЧОК (Beginner)
- **Алгоритм**: Случайные валидные ходы
- **Глубина поиска**: 1
- **Время "размышления"**: 500ms
- **Особенности**: 30% случайность, предпочтение захватам

#### 2️⃣ ЛЮБИТЕЛЬ (Amateur)
- **Алгоритм**: Minimax без отсечения
- **Глубина поиска**: 2
- **Время "размышления"**: 1000ms
- **Особенности**: Базовая оценка позиции, 15% случайность

#### 3️⃣ ОПЫТНЫЙ (Experienced)
- **Алгоритм**: Minimax с Alpha-Beta отсечением
- **Глубина поиска**: 3
- **Время "размышления"**: 2000ms
- **Особенности**: Улучшенная оценка (контроль центра, структура пешек), 5% случайность

#### 4️⃣ МАСТЕР (Master)
- **Алгоритм**: Alpha-Beta с сортировкой ходов
- **Глубина поиска**: 4
- **Время "размышления"**: 3000ms
- **Особенности**: Продвинутая оценка, детерминированный (0% случайность)

### Оценка позиции (BoardEvaluator.kt)

**Факторы оценки:**

1. **Материал** (100 = 1 пешка):
   - Пешка: 100
   - Конь: 320
   - Слон: 330
   - Ладья: 500
   - Ферзь: 900
   - Король: 20000

2. **Позиция фигур** (Piece-Square Tables):
   - Пешки: бонус за продвижение вперед
   - Кони: любят центр
   - Слоны: диагонали и центр
   - Король: безопасность в начале, активность в эндшпиле

3. **Контроль центра**: e4, d4, e5, d5

4. **Безопасность короля**: пешечный щит

### Алгоритмы поиска

**Minimax** (для Любителя):
```kotlin
fun minimax(board, depth, isMaximizing):
    if depth == 0: return evaluate(board)
    if isMaximizing:
        return max(minimax(child, depth-1, false) for child in children)
    else:
        return min(minimax(child, depth-1, true) for child in children)
```

**Alpha-Beta** (для Опытного и Мастера):
```kotlin
fun alphaBeta(board, depth, alpha, beta, isMaximizing):
    if depth == 0: return evaluate(board)
    for move in moves:
        score = alphaBeta(newBoard, depth-1, alpha, beta, !isMaximizing)
        alpha = max(alpha, score)
        if beta <= alpha: break  // Отсечение
    return alpha
```

## 📱 YANDEX MOBILE ADS SDK

### Конфигурация (AdsManager.kt)

**Типы рекламы:**

1. **Баннеры** (sticky)
   - ID: `R-M-DEMO-320x50` (замените на реальный!)
   - Расположение: внизу главного меню
   - Метод: `loadBanner(container)`

2. **Interstitial** (межстраничная)
   - ID: `R-M-DEMO-interstitial` (замените!)
   - Показ: при выходе из игры
   - Методы: `loadInterstitial()`, `showInterstitial()`

3. **Rewarded** (с вознаграждением)
   - ID: `R-M-DEMO-rewarded-client-side-rtb` (замените!)
   - Использование: подсказка за просмотр рекламы
   - Метод: `showRewarded(onRewardEarned)`

### Инициализация

В `ChessApplication.kt`:
```kotlin
MobileAds.initialize(this) {
    Log.d("Yandex Ads", "SDK инициализирован")
}
```

### Проверка интеграции

Запустите приложение и проверьте logcat:
```
adb logcat | grep "Yandex"
```

Ожидаемый вывод:
```
Yandex Mobile Ads SDK успешно инициализирован
Проверьте logcat по фразе 'Yandex Ads' для деталей
```

## 🎨 UI/UX (В РАЗРАБОТКЕ)

### Экраны

1. **SplashActivity** - загрузка приложения, инициализация SDK
2. **MainActivity** - главное меню с навигацией
3. **GameActivity** - игровой экран с шахматной доской
4. **SettingsFragment** - настройки игры
5. **StatisticsFragment** - статистика игр

### ChessBoardView (Custom View)

Кастомный View для отрисовки шахматной доски:
- Отрисовка клеток (светлые/темные)
- Рендеринг фигур (Unicode символы или drawable)
- Подсветка выбранной фигуры
- Отображение возможных ходов
- Анимация перемещения фигур
- Drag & Drop для ходов

## 📊 ДАННЫЕ И НАСТРОЙКИ

### SharedPreferences

**Настройки** (`game_settings`):
- `sound_enabled`: Boolean
- `vibration_enabled`: Boolean
- `show_legal_moves`: Boolean
- `animations_enabled`: Boolean

**Статистика** (`game_stats`):
- `games_played`: Int
- `games_won`: Int
- `games_lost`: Int
- `games_draw`: Int
- `best_difficulty`: String

## 🔧 КАК ИСПОЛЬЗОВАТЬ

### 1. Замените тестовые ID рекламы

В `AdsManager.kt` замените:
```kotlin
private const val BANNER_AD_UNIT_ID = "R-M-DEMO-320x50"
```
на ваш реальный ID из кабинета РСЯ.

### 2. Создайте игру

```kotlin
val board = ChessBoard() // Начальная позиция
val engine = ChessEngine()
val ai = ChessAI(GameDifficulty.MASTER)

// Получить легальные ходы
val moves = engine.getLegalMoves(board, Position(1, 4)) // e2

// Сделать ход
val newBoard = board.makeMove(moves.first())

// AI ход
val aiMove = ai.findBestMove(newBoard)
```

### 3. Проверка состояния игры

```kotlin
val gameState = engine.getGameState(board)

when (gameState) {
    is GameState.Checkmate -> println("Мат! Победили ${gameState.winner}")
    is GameState.Check -> println("Шах!")
    is GameState.Stalemate -> println("Пат!")
    is GameState.Playing -> println("Игра продолжается")
}
```

## 📝 ТРЕБОВАНИЯ

- **Kotlin**: 1.9.22
- **AGP**: 8.7.2
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Yandex Mobile Ads SDK**: 7.6.1

## 🚀 ЗАПУСК ПРОЕКТА

```bash
# Клонирование
git clone <repository-url>

# Сборка
./gradlew assembleDebug

# Установка
./gradlew installDebug

# Запуск
adb shell am start -n com.chess.game/.ui.splash.SplashActivity
```

## 📄 ЛИЦЕНЗИЯ

См. LICENSE файл в корне проекта.

---

**Примечание**: Проект находится в активной разработке. UI компоненты и экраны будут добавлены в следующих итерациях.

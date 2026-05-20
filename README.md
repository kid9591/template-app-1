# README.md
---

## 1. What this project is

A **template Android project** structured as a simple Todo app. It is meant to be cloned and adapted as the base for new apps. The Todo domain (tasks, categories, status) is intentionally small so the focus stays on the architecture.

### Business logic

- **Task list (Home screen):** load all tasks from local storage, display them with filter chips (`ALL`, `TODO`, `IN_PROGRESS`, `DONE`), and show derived counts/progress. Tapping a task opens its detail screen.
- **Create task (Add Task screen):** the user enters a title, picks a `TaskCategory`, optionally sets a due date and a time, and saves. The new task is persisted locally (Room) with a generated UUID.
- **Task detail (Task Detail screen):** view a task by id, update its status/fields, or delete it.
- **Sync:** on app launch (`HomeViewModel.init`) the repository pulls tasks from a remote endpoint (`GET /tasks`) via Retrofit and **replaces** the local Room dataset. The UI always reads from Room's `Flow`, so network failures are silent — the cached data stays visible.

---

## 2. Tech stack

| Concern | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVI (State / Action / Event) |
| DI | Hilt (`hilt-android`, `hilt-navigation-compose`) |
| Navigation | `androidx.navigation.compose` with type-safe `@Serializable` routes |
| Local DB | Room |
| Key-value store | Jetpack DataStore (Preferences) |
| Networking | Retrofit + OkHttp |
| Serialization | Moshi (JSON / DTOs), kotlinx.serialization (navigation routes only) |
| Image loading | Glide (GIF support built-in) |
| Async | Kotlin Coroutines + Flow |

---

### Navigation

Type-safe routes via `kotlinx.serialization` (required by the Navigation Compose API):

```kotlin
@Serializable object HomeRoute
@Serializable object AddTaskRoute
@Serializable data class TaskDetailRoute(val taskId: String)
```

Flow: `Home → AddTask` and `Home → TaskDetail(taskId)`. Start destination is `HomeRoute`.

### MVI conventions (per feature folder)

Each `screens/{feature}/` folder contains exactly three files:

1. **`{Feature}State.kt`** — `data class State`, `sealed interface Action`, `sealed interface Event`. Derived values (counts, filtered lists, progress) live as computed properties on `State`, not in the ViewModel.
2. **`{Feature}ViewModel.kt`** — annotated with `@HiltViewModel`, consumes a repository via `@Inject constructor`, exposes `uiState: StateFlow<State>` and `events: Flow<Event>`. Events use a `Channel` exposed via `receiveAsFlow()` (not `SharedFlow`).
3. **`{Feature}Screen.kt`** — two composables:
   - `{Feature}ScreenRoot` injects the ViewModel with `hiltViewModel()`, observes events via `ObserveAsEvents`, and forwards actions.
   - `{Feature}Screen` is pure UI: takes `state` + lambda callbacks, no DI.

---

## 3. Repository abstraction & data sources

The template demonstrates the **repository-with-multiple-data-sources** pattern. Use it as a recipe for any new domain entity.

### 3.1 The interface lives in `domain/`

```kotlin
interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    fun getTaskById(id: String): Flow<Task?>
    suspend fun addTask(title: String, category: TaskCategory, dueDate: LocalDate? = null, time: String? = null)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: String)
    suspend fun sync()
}
```

The `domain` layer knows nothing about Room, Retrofit, or Android. ViewModels depend on this interface only.

### 3.2 Two sample implementations

- **`OfflineFirstTaskRepository`** (production) — composes a `RoomTaskSource` (cache) and a `RetrofitTaskSource` (remote). Reads always come from Room (`local.observeTasks()`); `sync()` fetches from the network and does a full replace into Room. Network errors are logged and swallowed so cached data continues to serve the UI.
- **`FakeTaskRepository`** (previews) — in-memory `MutableStateFlow<List<Task>>` seeded with sample data. Can be used in `@Preview` composables.

### 3.3 Data sources

- **Cache**
  - **Room** — structured / relational data. File: `data/cache/room/RoomTaskSource.kt` (wraps `TaskDao`, exposes `Flow<List<Task>>`). `ApplicationDatabase` is built in `DatabaseModule` with `fallbackToDestructiveMigration()` — replace with real migrations before shipping. Use this for entities with multiple fields, queries, or relations (tasks, messages, products…).
  - **DataStore** — key-value data. File: `data/cache/datastore/DataStoreSource.kt`, implementing the generic `SimpleKVRepository` (string / int / boolean / long get / put / observe). Use this for flat values: onboarding flags, user preferences, auth tokens, last-sync timestamps.

- **Network**
  - **Retrofit + OkHttp** — HTTP client. Files: `data/remote/TaskApiService.kt` (Retrofit interface with suspend functions) and `data/remote/RetrofitTaskSource.kt` (calls `GET /tasks`, returns `NetworkResponse<List<TaskDto>>`). Add new endpoints as methods on a feature-scoped `TaskApiService` and wrap them in a `*Source` class injected via Hilt.

### 3.4 Mappers & Models

- **Domain**
  - Named simply `Task` — used for UI
- **Cache**
  - **Model**: ends with `Entity` (e.g. `TaskEntity`)
  - **Mappers**: `toEntity()` and `toDomain()`, written as extension functions in the model file directly
- **Remote**
  - **Model**: ends with `Dto` (e.g. `TaskDto`), annotated with `@JsonClass(generateAdapter = true)`
  - **Mappers**: `toDto()` and `toDomain()`, written as extension functions in the model file directly

### 3.5 DI wiring (Hilt modules in `di/`)

Four Hilt modules, each installed in `SingletonComponent`:

- **`NetworkModule`** — `OkHttpClient`, `Moshi`, `Retrofit`, `TaskApiService`
- **`DatabaseModule`** — `ApplicationDatabase`, `TaskDao`
- **`DataStoreModule`** — `DataStore<Preferences>`
- **`RepositoryModule`** — `@Binds` `TaskRepository → OfflineFirstTaskRepository`, `SimpleKVRepository → DataStoreSource`

`TodoApp` is annotated with `@HiltAndroidApp`. `MainActivity` is annotated with `@AndroidEntryPoint`. ViewModels use `@HiltViewModel` + `@Inject constructor`.

---

## 4. Using this template for a new app

1. Rename the package root (`com.example.todoist` → your namespace) and the `applicationId` in `app/build.gradle.kts`.
2. Replace the `Task` domain model (and `TaskCategory` / `TaskStatus`) with your own entities. Keep `domain/` framework-free.
3. Update `TaskRepository` → `YourEntityRepository`, the Room entity/DAO, the Retrofit API service, and the DTOs accordingly.
4. Add a new Hilt module (or extend the existing ones) to bind the new repository and data sources.
5. Replace the three feature folders (`home`, `addtask`, `taskdetail`) with your own screens, following the State / ViewModel / Screen + Root split.
6. Update `ApplicationNavigationGraph.kt` routes and start destination.
7. Before shipping, replace `fallbackToDestructiveMigration()` in `DatabaseModule` with real Room migrations.

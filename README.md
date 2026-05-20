# README.md
---

## 1. What this project is

A **template Android project** structured as a simple Todo app. It is meant to be cloned and adapted as the base for new apps. The Todo domain (tasks, categories, status) is intentionally small so the focus stays on the architecture.

### Business logic

- **Task list (Home screen):** load all tasks from local storage, display them with filter chips (`ALL`, `TODO`, `IN_PROGRESS`, `DONE`), and show derived counts/progress. Tapping a task opens its detail screen.
- **Create task (Add Task screen):** the user enters a title, picks a `TaskCategory`, optionally sets a due date and a time, and saves. The new task is persisted locally (Room) with a generated UUID.
- **Task detail (Task Detail screen):** view a task by id, update its status/fields, or delete it.
- **Sync:** on app launch (`HomeViewModel.init`) the repository pulls tasks from a remote endpoint (`GET /tasks`) via Ktor and **replaces** the local Room dataset. The UI always reads from Room's `Flow`, so network failures are silent — the cached data stays visible.

---

## 2. Tech stack

| Concern | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVI (State / Action / Event) |
| DI | Koin (`koin-android`, `koin-androidx-compose`) |
| Navigation | `androidx.navigation.compose` with type-safe `@Serializable` routes |
| Local DB | Room (v2) |
| Key-value store | Jetpack DataStore (Preferences) |
| Networking | Ktor client (Android engine) + `kotlinx.serialization` |
| Async | Kotlin Coroutines + Flow |
| Testing | JUnit 5 (Jupiter), Turbine, AssertK, `kotlinx-coroutines-test` |

---

### Navigation

Type-safe routes via `kotlinx.serialization`:

```kotlin
@Serializable object HomeRoute
@Serializable object AddTaskRoute
@Serializable data class TaskDetailRoute(val taskId: String)
```

Flow: `Home → AddTask` and `Home → TaskDetail(taskId)`. Start destination is `HomeRoute`.

### MVI conventions (per feature folder)

Each `presentation/{feature}/` folder contains exactly three files:

1. **`{Feature}State.kt`** — `data class State`, `sealed interface Action`, `sealed interface Event`. Derived values (counts, filtered lists, progress) live as computed properties on `State`, not in the ViewModel.
2. **`{Feature}ViewModel.kt`** — consumes a repository, exposes `uiState: StateFlow<State>` and `events: Flow<Event>`. Events use a `Channel` exposed via `receiveAsFlow()` (not `SharedFlow`).
3. **`{Feature}Screen.kt`** — two composables:
   - `{Feature}ScreenRoot` injects the ViewModel with `koinViewModel()`, observes events via `ObserveAsEvents`, and forwards actions.
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

The `domain` layer knows nothing about Room, Ktor, or Android. ViewModels depend on this interface only.

### 3.2 Two sample implementations

- **`OfflineFirstTaskRepository`** (production) — composes a `RoomTaskDataSource` (local) and a `KtorTaskDataSource` (remote). Reads always come from Room (`local.observeTasks()`); `sync()` fetches from the network and does a full replace into Room. Network errors are logged and swallowed so cached data continues to serve the UI.
- **`FakeTaskRepository`** (testing / previews) — in-memory `MutableStateFlow<List<Task>>` seeded with sample data. Used by JUnit tests and can be used in `@Preview` composables.

### 3.3 Data sources

- **Cache**
  - **Room** — structured / relational data. File: `data/local/room/RoomTaskDataSource.kt` (wraps `TaskDao`, exposes `Flow<List<Task>>`). `AppDatabase` is built in `databaseModule` with `fallbackToDestructiveMigration()` — replace with real migrations before shipping. Use this for entities with multiple fields, queries, or relations (tasks, messages, products…).
  - **DataStore** — key-value data. File: `data/local/datastore/DataStoreKeyValueDataSource.kt`, implementing the generic `KeyValueRepository` (string / int / boolean / long get / put / observe). Use this for flat values: onboarding flags, user preferences, auth tokens, last-sync timestamps.

- **Network**
  - **Ktor** — HTTP client. Files: `data/remote/HttpClientFactory.kt` (Android engine, `ContentNegotiation`, logging) and `data/remote/KtorTaskDataSource.kt` (calls `GET /tasks`, returns `NetworkResult<List<TaskDto>>`). Add new endpoints as methods on a feature-scoped `*DataSource` that takes the shared `HttpClient` via Koin.

### 3.4 Mappers & Models

- **Domain**
  - name simple `Task` -> used for UI
- **Cache** 
   - **Model**: ends with `Entity` (eg: `TaskEntity`)
   - **Mappers**: ends with `toEntity()` and `toDomain`, write mapper extensions in model file directly: `Task.toEntity()` / `TaskEntity.toDomain()`
- **Remote**
   - **Model**: ends with `Dto` (Data transfer object)
   - **Mappers**: ends with `toDto()` and `toDomain`, write mapper extensions in model file directly: `Task.toDto()` / `TaskDto.toDomain()`

### 3.5 DI wiring (`di/AppModule.kt`)

Five modules, assembled into `appModule` and passed to `startKoin` in `TodoApp`:

```kotlin
val appModule = listOf(networkModule, databaseModule, dataModule, datastoreModule, viewModelModule)
```

- `networkModule` — `HttpClient`, `KtorTaskDataSource`
- `databaseModule` — `AppDatabase`, `TaskDao`, `RoomTaskDataSource`
- `dataModule` — binds `TaskRepository` to `OfflineFirstTaskRepository`
- `datastoreModule` — `DataStore<Preferences>`, binds `KeyValueRepository` to `DataStoreKeyValueDataSource`
- `viewModelModule` — `viewModel { ... }` for every ViewModel

---

## 4. Testing

Tests live under `app/src/test/` (JVM only; the `androidTest/` directory contains Compose UI tests but the CI baseline targets JVM unit tests).

### Stack

- **JUnit 5 (Jupiter)** — `@Test`, `@BeforeEach`, `@AfterEach`
- **Turbine** — `flow.test { awaitItem(); cancelAndIgnoreRemainingEvents() }`
- **AssertK** — `assertThat(actual).isEqualTo(expected)`
- **kotlinx-coroutines-test** — `runTest`, `UnconfinedTestDispatcher`, `Dispatchers.setMain` / `resetMain`
- **No mocking framework** — use `FakeTaskRepository` (hand-written) instead of Mockito/MockK.

### Sample test files

- `HomeViewModelTest.kt` — initial state, action → state transitions, action → one-shot event.
- `AddTaskViewModelTest.kt` — form validation, save action, navigation event.
- `TaskDetailViewModelTest.kt` — loading a task by id, update / delete flows.
- `FakeTaskRepositoryTest.kt` — sanity tests for the fake itself, so test failures aren't hidden behind a buggy fake.

### Template for a new ViewModel test

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class FooViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var viewModel: FooViewModel

    @BeforeEach fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        viewModel = FooViewModel(repository)
    }

    @AfterEach fun tearDown() = Dispatchers.resetMain()

    @Test fun `action X emits event Y`() = runTest {
        viewModel.events.test {
            viewModel.onAction(FooAction.X)
            assertThat(awaitItem() is FooEvent.Y).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

---

## 5. Using this template for a new app

1. Rename the package root (`com.example.todoist` → your namespace) and the `applicationId` in `app/build.gradle.kts`.
2. Replace the `Task` domain model (and `TaskCategory` / `TaskStatus`) with your own entities. Keep `domain/` framework-free.
3. Update `TaskRepository` → `YourEntityRepository`, the Room entity/DAO, the Ktor endpoint, and the DTOs accordingly.
4. Rebuild Koin modules in `di/AppModule.kt` to bind the new repository and ViewModels.
5. Replace the three feature folders (`home`, `addtask`, `taskdetail`) with your own screens, following the State / ViewModel / Screen + Root split.
6. Update `AppNavigation.kt` routes and start destination.
7. Before shipping, replace `fallbackToDestructiveMigration()` in `databaseModule` with real Room migrations.
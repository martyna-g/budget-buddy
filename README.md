# BudgetBuddy

BudgetBuddy is an Android app that helps users track recurring payments, manage shopping lists, and view UK bank holidays.  
Built with Kotlin and Jetpack Compose.

## Features

* Manage recurring and one-time payments with optional reminders
* View, manage, and delete payments; browse Payment History
* Manage a shopping list
* View upcoming UK bank holidays
* Automatically schedule recurring payments
* Navigate quickly via deep links and app shortcuts

## Screenshots

| Payment List (dark mode)                                   | Payment List (light mode)                                    | Add Payment                                 |
|------------------------------------------------------------|--------------------------------------------------------------|---------------------------------------------|
| ![Payment List (dark mode)](screenshots/payments-dark.png) | ![Payment List (light mode)](screenshots/payments-light.png) | ![Add Payment](screenshots/add-payment.png) |

| Shopping List                                   | Shopping List (multi-select)                                         | Bank Holidays                                   |
|-------------------------------------------------|----------------------------------------------------------------------|-------------------------------------------------|
| ![Shopping List](screenshots/shopping-list.png) | ![Shopping List (multi-select)](screenshots/shopping-list-multi.png) | ![Bank Holidays](screenshots/bank-holidays.png) |

## Tech Stack

* Kotlin, Jetpack Compose
* Clean Architecture, MVVM
* Hilt
* Navigation Compose
* Room
* Retrofit & Gson
* Coroutines & Flow
* WorkManager (recurring payments and notifications)
* Testing: JUnit, Mockito, Hilt Testing, Compose UI Tests

## Architecture

Clean Architecture for clear separation of concerns and easy testing.

```
presentation  -->  domain  <--  data
(UI/ViewModel)    (use cases)   (db/network/workers)
```

**Data flow:** `UI → ViewModel → Use case → Repository → Flow<Result<…>> → ViewModel → UI`

### Structure

This project follows a feature-first package layout. Each feature contains its Compose UI, ViewModel, and the required data access (repositories, DAOs, or API clients).

* `payment/`: screens, ViewModel, and use cases.
  Contains `PaymentRepository`, `PaymentDao`, and WorkManager schedulers for recurring payments and notifications.
* `shopping/`: screens and ViewModel.
  Contains `ShoppingRepository` and `ShoppingDao`.
* `bankholiday/`: screens and ViewModel.
  Backed by Retrofit `ApiService`, mapper, and `BankHolidayRetriever`.
* `ui/theme/`: theming and small UI utilities (e.g., `BudgetBuddyTheme`, `StatusBarBackground`)
* **Root:** app entry and navigation (`MainActivity`, `MainScreen`, `AppNavGraph`/`BottomNav`); shared UI (`ErrorScreen`, `LoadingScreen`, `SectionHeader`); app setup (`BudgetBuddyApplication`, `Room DB`, `RoomTypeConverter`); `Result`
* **Hilt modules** (e.g., `NetworkModule`, `BankHolidayRetrieverModule`): live next to the feature they wire up

### Dependency Rules

* Dependencies point inward only: `presentation → domain`, `data → domain`
* `domain` stays Android-free (no Retrofit/Room/Hilt/Compose imports)
* Repository interfaces live in `domain`; implementations live next to features and are bound via Hilt

## Key Design Decisions

* MVVM in presentation; one ViewModel per screen; state exposed as `StateFlow<UiState>`
* Single-purpose use cases (one intent per operation)
* WorkManager schedules the next payment and reminder notifications
* Deep links and app shortcuts to Shopping List and Bank Holidays
* Edge-to-edge system bars with automatic icon contrast
* Material 3 components and theming

## Architecture Layers

* **Domain**: pure Kotlin use cases; no Android/IO; depends only on interfaces
* **Data**: repository and background-work implementations; adapts Room/Retrofit/WorkManager; mapping and streams
* **Presentation**: Compose UI and ViewModels; navigation (deep links, app shortcuts); no direct DB/network access

## Models

The app currently reuses Room entities because they match UI needs. If the project grows in complexity, separate domain models and mappers will be introduced to keep clear boundaries.

## Tests

* Unit tests for ViewModels and Use Cases
* Integration tests for Repositories and DAOs
* Compose UI tests for screens and components

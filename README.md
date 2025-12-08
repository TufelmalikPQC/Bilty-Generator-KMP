# Bilty Generator - KMP Project

A Kotlin Multiplatform (KMP) project for generating and managing bilty (receipt/invoice) documents with multi-platform support for Android, iOS, JVM Desktop, and Web (WasmJS).

## 📁 Project Structure

```
composeApp/
└── src/
    ├── commonMain/          # Shared code across all platforms
    │   ├── kotlin/
    │   │   └── com/bilty/generator/
    │   │       ├── AppNavigation.kt # Navigation routes
    │   │       ├── KMPApp.kt           # Main app entry point with navigation setup
    │   │       ├── Platform.kt          # Platform-specific definitions
    │   │       ├── bridge/              # Platform bridge implementations
    │   │       ├── di/                  # Dependency Injection (Koin)
    │   │       │   ├── AppModule.kt
    │   │       │   ├── helperModules/
    │   │       │   ├── repositoryModules/
    │   │       │   └── viewModelModules/
    │   │       ├── model/               # Data models and constants
    │   │       │   ├── constants/       # App-wide constants
    │   │       │   ├── data/            # Data classes
    │   │       │   ├── enums/           # Enumerations
    │   │       │   ├── interfaces/      # Interface definitions
    │   │       │   └── reponse/         # API response models
    │   │       │─ modules/             # Organizes features into distinct modules
    │   │       │   ├── auth/            # Authentication feature module
    │   │       │   │   ├── components/  # Reusable UI components for authentication
    │   │       │   │   ├── navigation/  # Navigation routes and logic for authentication
    │   │       │   │   ├── ui/          # UI screens/composables for authentication
    │   │       │   │   └── viewModel/   # ViewModels for authentication screens (can contain multiple)
    │   │       │   ├── home/            # Home screen feature module
    │   │       │   │   ├── components/  # Reusable UI components for the home screen
    │   │       │   │   ├── navigation/  # Navigation routes and logic for the home screen
    │   │       │   │   ├── ui/          # UI screens/composables for the home screen
    │   │       │   │   └── viewModel/   # ViewModels for home screens (can contain multiple)
    │   │       │   ├── profile/         # User profile management feature module
    │   │       │   │   ├── components/  # Reusable UI components for profile management
    │   │       │   │   ├── navigation/  # Navigation routes and logic for profile flow
    │   │       │   │   ├── ui/          # UI screens/composables for profile management
    │   │       │   │   └── viewModel/   # ViewModels for profile screens (can contain multiple)
    │   │       │   └── settings/        # User settings feature module
    │   │       │       ├── components/  # Reusable UI components for settings
    │   │       │       ├── navigation/  # Navigation routes and logic for settings flow
    │   │       │       ├── ui/          # UI screens/composables for settings
    │   │       │       └── viewModel/   # ViewModels for settings screens (can contain multiple)
    │   │       ├── repository/          # Data repositories
    │   │       ├── theme/               # Theming (Colors, Typography)
    │   │       ├── uiToolKit/           # Reusable UI components
    │   │       └── utils/               # Utility functions
    │   └── composeResources/
    │       ├── drawable/       # Image assets
    │       ├── files/          # Static files (e.g., receipt templates)
    │       ├── font/           # Font files
    │       └── values/
    │           └── strings.xml # String resources
    ├── androidMain/            # Android-specific code
    ├── iosMain/                # iOS-specific code
    ├── jvmMain/                # JVM Desktop-specific code
    └── wasmJsMain/             # Web (WasmJS)-specific code
```

## 🎨 Color Management

### Location
`composeApp/src/commonMain/kotlin/com/bilty/generator/theme/Colors.kt`

### Implementation
Colors are defined in a **singleton object** `ThemeColors` for easy access throughout the app.

### Color Categories

#### 1. **Primary Colors**
```kotlin
val printRequestPrimaryColor = Color(0xFF1F55C6)
```

#### 2. **Status Icon Colors**
Used for indicating various statuses:
```kotlin
val statusGray = Color(0xFF757575)        // Not Started
val statusOrange = Color(0xFFF57C00)      // Pending
val statusBlue = Color(0xFF1976D2)        // Printing
val statusGreen = Color(0xFF388E3C)       // Completed
val statusRed = Color(0xFFD32F2F)         // Failed
val statusDarkOrange = Color(0xFFE65100)  // Cancelled
val statusPurple = Color(0xFF6A1B9A)      // Not Supported
```

#### 3. **Status Background Colors**
Light variants for status backgrounds:
```kotlin
val statusBackgroundLightGray = Color(0xFFF5F5F5)
val statusBackgroundLightOrange = Color(0xFFFFF3E0)
val statusBackgroundLightBlue = Color(0xFFE3F2FD)
val statusBackgroundLightGreen = Color(0xFFE8F5E9)
val statusBackgroundLightRed = Color(0xFFFFEBEE)
val statusBackgroundLightOrangeRed = Color(0xFFFBE9E7)
val statusBackgroundLightPurple = Color(0xFFF3E5F5)
```

#### 4. **UI Component Colors**
Specific colors for UI components:
```kotlin
val notificationCardBackground = Color(0xFFF5F5F5)
val rejectButtonBackground = Color(0xFFFFEBEE)
val rejectButtonTint = Color(0xFFD32F2F)
val approveButtonBackground = Color(0xFFE8F5E9)
val approveButtonTint = Color(0xFF388E3C)
val activeStatusColor = Color(0xFF388E3C)
```

### Usage
```kotlin
import com.bilty.generator.theme.ThemeColors

// Usage in Composable
Text(
    text = "Approved",
    color = ThemeColors.approveButtonTint
)
```

## 📝 String Management

### Location
`composeApp/src/commonMain/composeResources/values/strings.xml`

### Naming Conventions
All strings follow a **prefix-based naming convention** for better organization:

#### String Prefixes

| Prefix | Purpose | Example |
|--------|---------|---------|
| `app_` | App name | `app_name` |
| `title_` | Screen titles | `title_select_printer` |
| `button_` | Button labels | `button_print_pdf` |
| `message_` | User messages | `message_generating_pdf` |
| `label_` | Field labels | `label_company`, `label_branch` |
| `str_` | Descriptions | `str_choose_print_description` |
| `dialog_` | Dialog titles | `dialog_select_company_branch` |
| `cd_` | Content descriptions (accessibility) | `cd_close`, `cd_approve` |
| `status_` | Status labels | `status_pending`, `status_completed` |

### String Categories

1. **Screen Titles** (`title_*`)
   - `title_select_printer`
   - `title_receipt_preview`
   - `title_select_print_method`

2. **Button Labels** (`button_*`)
   - `button_print_pdf`
   - `button_print_text`
   - `button_confirm`

3. **Messages** (`message_*`)
   - `message_generating_pdf`
   - `message_printing`
   - `message_print_completed`

4. **Field Labels** (`label_*`)
   - `label_company`
   - `label_branch`
   - `label_select_company`

5. **Content Descriptions** (`cd_*`) - For Accessibility
   - `cd_close`
   - `cd_approve`
   - `cd_reject`

6. **Status Labels** (`status_*`)
   - `status_not_started`
   - `status_pending`
   - `status_printing`
   - `status_completed`

### Usage with String Formatting
For dynamic strings with placeholders:
```xml
<string name="label_admin_prefix">Admin: %1$s</string>
<string name="label_code_prefix">Code: %1$s</string>
<string name="label_rate_prefix">Rate: %1$s</string>
```

Usage in code:
```kotlin
import org.jetbrains.compose.resources.stringResource
import biltygenerator.composeapp.generated.resources.Res

// Simple string
Text(text = stringResource(Res.string.title_select_printer))

// Formatted string
Text(text = stringResource(Res.string.label_admin_prefix, adminName))
```

## 🧭 Navigation Management

### Location
`composeApp/src/commonMain/kotlin/com/bilty/generator/modules/AppNavigation.kt`

### Implementation
Navigation uses **Type-Safe Navigation** with Kotlin Serialization.

### Navigation Routes
Defined as a **sealed class** with `@Serializable` annotation:

```kotlin
@Serializable
sealed class AppRoutes {
    @Serializable
    data object PrintMethodSelectionScreen : AppRoutes()
    
    @Serializable
    data object PrintPreviewScreen : AppRoutes()
    
    @Serializable
    data object SendPrintRequestScreen : AppRoutes()
    
    @Serializable
    data class PrinterScreen(
        var isPreviewWithImageBitmap: Boolean,
        var fontSize: Int,
        var isLandscapeMode: Boolean,
        var fontFamilyName: String
    ) : AppRoutes()
}
```

### Navigation Setup
Navigation is configured in `KMPApp.kt`:

```kotlin
NavHost(
    navController = navController,
    startDestination = AppRoutes.SendPrintRequestScreen
) {
    composable<AppRoutes.PrintMethodSelectionScreen> {
        PrintMethodSelectionScreen(navController)
    }
    
    composable<AppRoutes.PrintPreviewScreen> {
        PrintPreviewScreen(navController)
    }
    
    composable<AppRoutes.PrinterScreen> { route ->
        // Retrieve parameters from savedStateHandle
        route.savedStateHandle.apply {
            val isPreviewWithImageBitmap = get<Boolean>("isPreviewWithImageBitmap") ?: false
            val fontSize = get<Int>("fontSize") ?: 24
            val isLandscapeMode = get<Boolean>("isLandscapeMode") ?: false
            val fontFamilyName = get<String>("fontFamilyName") ?: FontStyles.DOT_MATRIX.name
            
            PrinterScreen(
                navController = navController,
                isPreviewWithImageBitmap = isPreviewWithImageBitmap,
                fontSize = fontSize,
                isLandscapeMode = isLandscapeMode,
                fontFamilyName = fontFamilyName
            )
        }
    }
    
    composable<AppRoutes.SendPrintRequestScreen> {
        SendPrintRequestScreen(paddingValues)
    }
}
```

### Navigation Usage
```kotlin
// Navigate to a simple route
navController.navigate(AppRoutes.PrintPreviewScreen)

// Navigate with parameters
navController.navigate(
    AppRoutes.PrinterScreen(
        isPreviewWithImageBitmap = true,
        fontSize = 24,
        isLandscapeMode = false,
        fontFamilyName = FontStyles.DOT_MATRIX.name
    )
)

// Navigate back
navController.popBackStack()
```

## 🔧 Dependency Injection (DI)

### Framework
**Koin** - A pragmatic lightweight dependency injection framework for Kotlin

### Location
`composeApp/src/commonMain/kotlin/com/bilty/generator/di/`

### Module Structure
DI is organized into **modular components**:

```
di/
├── AppModule.kt              # Main module that aggregates all sub-modules
├── helperModules/
│   └── HelperModule.kt       # Helper/Utility dependencies
├── repositoryModules/
│   └── RepositoryModule.kt   # Repository dependencies
└── viewModelModules/
    └── ViewModelModule.kt    # ViewModel dependencies
```

### Main Module
`AppModule.kt` consolidates all sub-modules:

```kotlin
val appModule = module {
    includes(
        viewModelModule,
        repositoryModule,
        helperModule
    )
}
```

### Koin Initialization
Koin is initialized at the app entry point in `KMPApp.kt`:

```kotlin
@Composable
fun KMPApp() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        // App content
    }
}
```

### Usage

#### In ViewModel
```kotlin
class MyViewModel(
    private val repository: MyRepository
) : ViewModel() {
    // ViewModel logic
}

// In viewModelModule
val viewModelModule = module {
    viewModel { MyViewModel(get()) }
}
```

#### In Composable (Using Koin)
```kotlin
@Composable
fun MyScreen() {
    val viewModel: MyViewModel = koinViewModel()
    // Use viewModel
}
```

## 📦 Feature Modules

### Module Organization
Each feature is organized in its own package under `modules/`:

```
modules/
├── AppNavigation.kt          # Navigation routes definition
├── preview/                  # Receipt preview feature
│   ├── components/
│   └── ui/
├── print/                    # Printing feature
│   ├── components/
│   └── ui/
├── printmethod/              # Print method selection
│   ├── components/
│   └── ui/
├── printqueue/               # Print queue management
│   └── ui/
└── remotePrint/              # Remote printing with Firebase
    ├── components/
    └── ui/
```

### Feature Structure
Each feature module typically contains:
- **ui/** - Screen composables
- **components/** - Feature-specific composable components
- **viewModel/** (in separate DI module) - Business logic
- **repository/** (in separate DI module) - Data access

## 🧩 Reusable UI Components

### Location
`composeApp/src/commonMain/kotlin/com/bilty/generator/uiToolKit/`

### Available Components
- `CommonDropdown.kt` - Reusable dropdown component
- `CommonRadioGroup.kt` - Reusable radio button group
- `PreviewTabs.kt` - Tab component for preview modes
- `PrintingStatusBottomSheet.kt` - Bottom sheet for printing status
- `GenerateRoadLineDeliveryReceipt.kt` - Receipt generation component

### Helper Functions
- `GetFontByName.kt` - Font retrieval utility
- `GetHtmlPageZoomLevel.kt` - HTML zoom level calculator
- `GetImageAsBase64.kt` - Image to Base64 converter

### Usage
```kotlin
import com.bilty.generator.uiToolKit.CommonDropdown

CommonDropdown(
    items = listOf("Option 1", "Option 2"),
    selectedItem = selectedOption,
    onItemSelected = { selectedOption = it }
)
```

## 🗄️ Repository Pattern

### Location
`composeApp/src/commonMain/kotlin/com/bilty/generator/repository/`

### Available Repositories
- `PrintQueueRepository.kt` - Manages print queue operations
- `SendPrintRequestRepository.kt` - Handles remote print requests

### Architecture
Repositories act as a **single source of truth** for data operations, abstracting data sources from ViewModels.

```
ViewModel → Repository → Data Source (Firebase, Local DB, etc.)
```

## 📊 Data Models

### Location
`composeApp/src/commonMain/kotlin/com/bilty/generator/model/`

### Model Categories

#### 1. **Data Classes** (`model/data/`)
Domain models representing business entities:
- `Company.kt` - Company information
- `Branch.kt` - Branch details
- `GrMaster.kt` - GR (Goods Receipt) master data
- `PrintJob.kt` - Print job information
- `PrintIndex.kt` - Print indexing data
- `PrintRequestData.kt` - Print request payload
- `NotificationItem.kt` - Notification data

#### 2. **Constants** (`model/constants/`)
- `Constants.kt` - App-wide constants (date formats, file paths, font names, platform identifiers)
- `FirebaseConstants.kt` - Firebase-specific constants (node names, paths)
- `PDF.kt` - PDF-related constants

#### 3. **Enums** (`model/enums/`)
Type-safe constants:
- `FontStyles` - Available font styles
- `PrintStatus` - Print job statuses
- Other enumerations for app state

#### 4. **Interfaces** (`model/interfaces/`)
Contract definitions for implementations

#### 5. **Responses** (`model/reponse/`)
API response models

## 🔤 Constants Management

### Global Constants
`Constants.kt` defines application-wide constants using **nested objects** for organization:

```kotlin
object Constants {
    const val DATE_FORMAT = "dd-MM-yyyy"
    const val RECEIPT_IMAGE_PATH = "transport_road_line_invoice.jpeg"
    const val BILTY_TEXT_FILE_PATH = "files/new_pqc_epson_lx.txt"
    
    object Fonts {
        const val BASE_PREFIX = "font/"
        const val FONT_FAMILY_NAME = "DynamicFontFamily"
        const val DIGI_TRACE = "digi_trace.otf"
        const val ENHANCED_DOT_DIGITAL_7 = "enhanced_dot_digital_7.ttf"
        const val DOT_DIGITAL_7 = "dot_matrix.ttf"
        const val DOT_MATRIX_7 = "dot_digital_7.ttf"
        const val DIGITAL_7 = "digital_7.ttf"
    }
    
    object Platforms {
        const val PLATFORM_ANDROID = "Android"
        const val PLATFORM_IOS = "iOS"
        const val PLATFORM_DESKTOP = "Java"
        const val PLATFORM_WEB = "Web with Kotlin/Wasm"
    }
    
    // Receipt dimensions
    const val receiptWidthInches = 5.82
    const val receiptHeightInches = 4.12
    const val RECEIPT_WIDTH_POINTS = receiptWidthInches * 72
    const val RECEIPT_HEIGHT_POINTS = receiptHeightInches * 72
}
```

### Firebase Constants
`FirebaseConstants.kt` stores Firebase node paths and keys for consistent database access.

### Usage
```kotlin
import com.bilty.generator.model.constants.Constants

val fontPath = Constants.Fonts.BASE_PREFIX + Constants.Fonts.DOT_MATRIX_7
val dateFormat = Constants.DATE_FORMAT
```

## 🛠️ Utility Functions

### Location
`composeApp/src/commonMain/kotlin/com/bilty/generator/utils/`

### Organization
- **FirebaseOptions.kt** - Firebase configuration
- **helpers/** - Helper functions
- **extention/** - Extension functions

## 🎯 Best Practices

### 1. **String Resources**
- ✅ **ALWAYS** use string resources from `strings.xml`
- ✅ Use appropriate prefixes (`title_`, `button_`, `label_`, etc.)
- ✅ Use `cd_` prefix for content descriptions (accessibility)
- ❌ **NEVER** hardcode strings in Kotlin code

### 2. **Color Management**
- ✅ **ALWAYS** define colors in `ThemeColors` object
- ✅ Use semantic color names (e.g., `approveButtonTint` instead of `green`)
- ❌ **NEVER** use hardcoded `Color()` values in composables

### 3. **Navigation**
- ✅ Use type-safe navigation with sealed classes
- ✅ Define all routes in `AppNavigation.kt`
- ✅ Use `@Serializable` for routes with parameters

### 4. **Dependency Injection**
- ✅ Organize dependencies into logical modules
- ✅ Use constructor injection
- ✅ Keep modules focused and single-purpose

### 5. **Code Organization**
- ✅ Follow the established folder structure
- ✅ Keep feature code in respective `modules/` packages
- ✅ Place reusable components in `uiToolKit/`
- ✅ Use repositories for data operations

### 6. **Constants**
- ✅ Define constants in appropriate constant objects
- ✅ Group related constants using nested objects
- ❌ **NEVER** use magic numbers or strings

### 7. **Composables**
- ✅ Keep composables small and focused
- ✅ Extract reusable components to `uiToolKit/`
- ✅ Use preview annotations for UI development

## 🚀 Getting Started

### Prerequisites
- Kotlin 1.9+
- Gradle 8.0+
- JDK 17+

### Build & Run

#### Android
```bash
./gradlew :composeApp:assembleDebug
```

#### iOS
```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

#### Desktop (JVM)
```bash
./gradlew :composeApp:run
```

#### Web (WasmJS)
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

## 📄 License

[Add your license information here]

## 👥 Contributors

[Add contributor information here]

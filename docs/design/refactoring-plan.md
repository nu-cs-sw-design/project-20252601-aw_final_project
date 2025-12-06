# Refactoring Plan

This document outlines the plan to refactor the messy prototype into the clean design. It identifies the violations in the current code and describes the steps to address them.

## Current Prototype Violations

### Violation 1: God Class

The WardrobeApp class contains over 1000 lines of code and handles multiple responsibilities including UI rendering, event handling, business logic, data storage, file I/O, validation, and analytics calculations.

Location: WardrobeApp.java (entire file)

Principle Violated: Single Responsibility Principle

### Violation 2: No Domain Model

Items are stored as String arrays with index-based access. There are no ClothingItem, Outfit, or UsageLog classes. This leads to fragile code with magic numbers for array indices.

Location: Lines where items are accessed as item[0], item[1], etc.

Principle Violated: Object-Oriented Design, Encapsulation

### Violation 3: Mixed UI and Business Logic

Business logic such as validation and data manipulation is embedded directly in UI event handlers. The addItemDialog() and editItemDialog() methods contain both UI code and business logic.

Location: addItemDialog(), editItemDialog(), createOutfitDialog(), logItemUsage()

Principle Violated: Single Responsibility Principle, Separation of Concerns

### Violation 4: Direct File I/O

The saveDataToFile() and loadDataFromFile() methods are embedded in the UI class with no abstraction. The file format is hardcoded, and there is no way to switch storage mechanisms.

Location: saveDataToFile(), loadDataFromFile()

Principle Violated: Dependency Inversion Principle, Single Responsibility Principle

### Violation 5: Hardcoded Filter Logic

The search functionality uses nested if-else chains with hardcoded filter conditions. Adding a new filter type requires modifying the existing search code.

Location: Search button action listener in createSearchPanel()

Principle Violated: Open/Closed Principle

### Violation 6: Hardcoded Analytics

All analytics calculations are in a single 150+ line method. Adding new analytics requires modifying this large method.

Location: calculateAndDisplayAnalytics()

Principle Violated: Open/Closed Principle, Single Responsibility Principle

### Violation 7: Code Duplication

The addItemDialog() and editItemDialog() methods share approximately 90% of the same code. Validation logic is duplicated in multiple places. Category, season, and occasion arrays are defined in multiple locations.

Location: addItemDialog(), editItemDialog(), createSearchPanel()

Principle Violated: DRY (Don't Repeat Yourself)

### Violation 8: No Interfaces

There are no interfaces in the codebase. Everything depends on concrete implementations, making it impossible to swap implementations or mock dependencies for testing.

Location: Entire codebase

Principle Violated: Dependency Inversion Principle, Interface Segregation Principle

### Violation 9: Tight Coupling to Swing

The entire application is tightly coupled to Swing components. Business logic cannot be tested without launching the GUI.

Location: All UI-related code

Principle Violated: Dependency Inversion Principle, Testability

## Refactoring Steps

### Step 1: Create Domain Model Classes

Create the model package with proper domain classes.

Tasks:
1. Create Category, Season, and Occasion enums
2. Create ClothingItem class with proper fields and getters
3. Create Outfit class with list of ClothingItem references
4. Create UsageLog class with date and item/outfit references

This step extracts the implicit data model from String arrays into proper objects. After this step, the code will have type-safe domain objects instead of fragile array access.

Violations Addressed: No Domain Model, Magic Numbers

### Step 2: Create Repository Interfaces and Implementations

Create the repository package with data access abstraction.

Tasks:
1. Create ItemRepository interface with CRUD methods
2. Create OutfitRepository interface with CRUD methods
3. Create UsageLogRepository interface with CRUD methods
4. Create FileItemRepository implementing ItemRepository
5. Create FileOutfitRepository implementing OutfitRepository
6. Create FileUsageLogRepository implementing UsageLogRepository
7. Move file I/O logic from WardrobeApp to repository implementations

This step extracts data persistence into a separate layer with proper abstraction. The file format and I/O logic will be isolated in repository classes.

Violations Addressed: Direct File I/O, No Interfaces, Tight Coupling

### Step 3: Create Filter Classes Using Strategy Pattern

Create the filter package with pluggable filter implementations.

Tasks:
1. Create ItemFilter interface with matches() method
2. Create CategoryFilter implementing ItemFilter
3. Create SeasonFilter implementing ItemFilter
4. Create OccasionFilter implementing ItemFilter
5. Create ColorFilter implementing ItemFilter
6. Create NameFilter implementing ItemFilter
7. Create CompositeFilter that combines multiple filters

This step replaces the hardcoded if-else filter logic with a flexible Strategy pattern. New filters can be added without modifying existing code.

Violations Addressed: Hardcoded Filter Logic, Open/Closed Violation

### Step 4: Create Analytics Classes Using Strategy Pattern

Create the analytics package with pluggable analytics calculators.

Tasks:
1. Create AnalyticsCalculator interface with calculate() method
2. Create AnalyticsResult class to hold calculation results
3. Create CategoryDistributionCalculator
4. Create ColorDistributionCalculator
5. Create SeasonDistributionCalculator
6. Create MostWornCalculator
7. Create LeastWornCalculator
8. Create CostAnalysisCalculator
9. Create AnalyticsService to coordinate calculators

This step extracts the monolithic analytics method into separate calculator classes. Each calculation becomes a focused class that can be tested independently.

Violations Addressed: Hardcoded Analytics, Single Responsibility Violation, Open/Closed Violation

### Step 5: Create Service Layer

Create the service package with business logic classes.

Tasks:
1. Create ItemValidator interface and DefaultItemValidator
2. Create OutfitValidator interface and DefaultOutfitValidator
3. Create ValidationResult class
4. Create ItemService with item management methods
5. Create OutfitService with outfit management methods
6. Create UsageService with usage logging methods
7. Move validation logic from UI event handlers to validators
8. Move business logic from UI event handlers to services

This step extracts business logic from the UI layer into a dedicated service layer. Services coordinate between repositories and handle validation.

Violations Addressed: Mixed UI and Business Logic, Code Duplication (validation)

### Step 6: Create View Interfaces

Create the view package with UI abstraction.

Tasks:
1. Create ItemView interface with display methods
2. Create OutfitView interface with display methods
3. Create UsageView interface with display methods
4. Create AnalyticsView interface with display methods

This step defines what the UI needs to do without specifying how. Controllers will depend on these interfaces rather than Swing classes.

Violations Addressed: Tight Coupling to Swing, No Interfaces

### Step 7: Create Controller Classes

Create the controller package with coordination logic.

Tasks:
1. Create ItemController connecting ItemService and ItemView
2. Create OutfitController connecting OutfitService and OutfitView
3. Create UsageController connecting UsageService and UsageView
4. Create AnalyticsController connecting AnalyticsService and AnalyticsView
5. Move event handling logic from WardrobeApp to controllers

This step separates the coordination logic from both the UI and the business logic. Controllers handle user actions and update views.

Violations Addressed: God Class, Mixed Responsibilities

### Step 8: Create Swing View Implementations

Implement the view interfaces with Swing components.

Tasks:
1. Create SwingItemView implementing ItemView
2. Create SwingOutfitView implementing OutfitView
3. Create SwingUsageView implementing UsageView
4. Create SwingAnalyticsView implementing AnalyticsView
5. Extract UI component creation from WardrobeApp to view classes
6. Eliminate duplicated UI code between add and edit dialogs

This step moves UI code into dedicated view classes that implement the view interfaces. The views become interchangeable.

Violations Addressed: God Class, Code Duplication (UI)

### Step 9: Create Application Entry Point

Create the app package with application initialization.

Tasks:
1. Create WardrobeApplication class
2. Implement dependency injection by creating all components and wiring them together
3. Remove all code from original WardrobeApp except main() which delegates to WardrobeApplication

This step creates a clean entry point that assembles all the components. The original god class is replaced with a simple bootstrap class.

Violations Addressed: God Class

### Step 10: Clean Up and Testing

Final cleanup and verification.

Tasks:
1. Delete unused code from original WardrobeApp
2. Ensure all components are properly connected
3. Test each layer independently
4. Verify all original functionality still works
5. Update documentation

This step ensures the refactoring is complete and the application works correctly.

## Refactoring Order Rationale

The refactoring is ordered to minimize risk and maintain a working application throughout:

1. Domain model first because it has no dependencies and everything else depends on it
2. Repositories second because they depend only on domain model
3. Filters and analytics third because they depend only on domain model
4. Services fourth because they depend on repositories and domain model
5. View interfaces fifth because they define what controllers need
6. Controllers sixth because they depend on services and view interfaces
7. View implementations seventh because they implement view interfaces
8. Application entry point last because it wires everything together

Each step produces working code that can be tested before proceeding to the next step.

## Estimated Effort

| Step | Estimated Hours |
|------|-----------------|
| Domain Model | 2-3 |
| Repositories | 4-5 |
| Filters | 2-3 |
| Analytics | 3-4 |
| Services | 3-4 |
| View Interfaces | 1-2 |
| Controllers | 3-4 |
| View Implementations | 4-5 |
| Application Entry Point | 1-2 |
| Testing and Cleanup | 2-3 |
| Total | 25-35 hours |

## Risk Mitigation

To reduce risk during refactoring:

1. Commit after each step so progress is not lost
2. Keep the original WardrobeApp working until the new structure is complete
3. Test each new component independently before integrating
4. Use the existing prototype as a reference for expected behavior
5. Refactor incrementally rather than rewriting everything at once

## Summary

The refactoring plan addresses all identified violations by:

1. Extracting domain model from String arrays to proper classes
2. Extracting data access into repository layer with interfaces
3. Extracting filter logic into Strategy pattern
4. Extracting analytics into Strategy pattern
5. Extracting business logic into service layer
6. Abstracting views behind interfaces
7. Creating controllers to coordinate between services and views
8. Implementing Swing views separately from controllers
9. Creating clean application entry point with dependency injection

The result will be a clean, maintainable, testable application that follows SOLID principles and demonstrates proper use of design patterns.


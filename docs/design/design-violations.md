# Design Principle Violations Analysis - Messy Prototype

## CS 396 Software Design - Milestone 2
## Personal Wardrobe Tracker - Intentionally Poor Design

---

## Executive Summary

This document catalogs the **intentional design violations** in the messy prototype implementation of the Personal Wardrobe Tracker. The prototype is functional but demonstrates poor software architecture that will be refactored in Milestone 3.

The primary anti-pattern is a **God Class** (`WardrobeApp`) that contains over 1000 lines of code and violates all five SOLID principles.

---

## 1. SOLID Principles Violations

### 1.1 Single Responsibility Principle (SRP)

**Principle:** A class should have only one reason to change.

**Violations in WardrobeApp:**

The `WardrobeApp` class has **at least 8 distinct responsibilities**:

1. **UI Rendering** - Creates and manages Swing components
   ```java
   private JPanel createItemsPanel()
   private JPanel createOutfitsPanel()
   private JPanel createSearchPanel()
   ```

2. **Event Handling** - Processes user interactions
   ```java
   addButton.addActionListener(e -> addItemDialog());
   searchButton.addActionListener(e -> { /* inline logic */ });
   ```

3. **Data Storage** - Manages in-memory data structures
   ```java
   private ArrayList<String[]> items = new ArrayList<>();
   private ArrayList<String[]> outfits = new ArrayList<>();
   private ArrayList<String[]> usageLogs = new ArrayList<>();
   ```

4. **Business Logic Validation** - Validates user input
   ```java
   if (name.isEmpty()) {
       JOptionPane.showMessageDialog(dialog, "Name is required!");
       return;
   }
   ```

5. **Data Persistence** - Reads/writes files
   ```java
   private void saveDataToFile()
   private void loadDataFromFile()
   ```

6. **Analytics Calculations** - Computes statistics
   ```java
   private void calculateAndDisplayAnalytics()
   // Contains 100+ lines of calculation logic
   ```

7. **Search/Filter Logic** - Implements filtering
   ```java
   // Giant nested if statements in search button handler
   for (String[] item : items) {
       if (!searchText.isEmpty() && !item[0].contains(searchText)) {
           matches = false;
       }
       // ... more conditions
   }
   ```

8. **Data Transformation** - Converts between formats
   ```java
   String indicesStr = outfit[1].replace("[", "").replace("]", "");
   String[] indices = indicesStr.split(", ");
   ```

**Impact:**
- Any change to UI requires touching the same class that handles business logic
- Testing is nearly impossible - can't test business logic without launching UI
- Multiple developers can't work on different features simultaneously
- High risk of introducing bugs when making changes

---

### 1.2 Open/Closed Principle (OCP)

**Principle:** Software entities should be open for extension but closed for modification.

**Violations:**

#### Violation 1: Hardcoded Category Types
```java
JComboBox<String> categoryBox = new JComboBox<>(
    new String[]{"Top", "Bottom", "Outerwear", "Shoes", "Accessory"}
);
```

**Problem:** To add a new category (e.g., "Swimwear"), you must:
1. Modify the array in `addItemDialog()`
2. Modify the array in `editItemDialog()` (duplicated code!)
3. Modify the array in `createSearchPanel()`
4. Recompile the entire application

**Better Design:** Use Strategy Pattern with CategoryType interface

#### Violation 2: Hardcoded Filter Logic
```java
// In search button action listener
if (!searchText.isEmpty() && !item[0].toLowerCase().contains(searchText)) {
    matches = false;
}
if (!category.equals("All") && !item[1].equals(category)) {
    matches = false;
}
if (!season.equals("All") && !item[3].equals(season)) {
    matches = false;
}
// ... more hardcoded conditions
```

**Problem:** 
- Can't add new filter types (e.g., "Brand filter", "Price range") without modifying this method
- Each filter type is hardcoded with index access to String arrays
- No way to compose filters dynamically

**Better Design:** Use Strategy Pattern or Chain of Responsibility for filters

#### Violation 3: Hardcoded Analytics Calculations
```java
private void calculateAndDisplayAnalytics() {
    // 100+ lines of hardcoded calculation logic
    // Color distribution
    HashMap<String, Integer> colorCount = new HashMap<>();
    for (String[] item : items) {
        String color = item[2];
        colorCount.put(color, colorCount.getOrDefault(color, 0) + 1);
    }
    
    // Season distribution
    HashMap<String, Integer> seasonCount = new HashMap<>();
    // ... more hardcoded calculations
}
```

**Problem:**
- To add new analytics (e.g., "Most expensive items"), must modify this massive method
- Can't let users choose which analytics to display
- All calculations run every time, no optimization

**Better Design:** Use Strategy Pattern with AnalyticsCalculator interface

---

### 1.3 Liskov Substitution Principle (LSP)

**Principle:** Objects of a superclass should be replaceable with objects of a subclass without breaking the application.

**Violations:**

This prototype has **NO inheritance hierarchy at all**, so LSP doesn't directly apply. However, this is itself a violation of good OO design:

- No abstract `Item` base class with subclasses like `ClothingItem`, `Shoe`, `Accessory`
- No polymorphic behavior
- Can't leverage inheritance for code reuse
- Everything is a String array - no type safety

**Example of Missing Hierarchy:**
```java
// Current: Everything is String[]
private ArrayList<String[]> items = new ArrayList<>();

// Should be: Polymorphic types
private ArrayList<Item> items = new ArrayList<>();
// where Item is abstract, with subclasses Top, Bottom, Outerwear, etc.
```

**Impact:**
- Can't add item-specific behavior (e.g., shoes have size, tops have sleeve length)
- No compile-time type checking
- Runtime errors from array index mistakes

---

### 1.4 Interface Segregation Principle (ISP)

**Principle:** Clients should not be forced to depend on interfaces they do not use.

**Violations:**

This prototype has **ZERO interfaces defined**. Everything is a concrete class.

**Missing Interfaces:**

1. **ItemRepository Interface**
   - Should abstract data storage
   - Current code directly calls `FileWriter` and `FileReader`
   - Can't swap implementations (file → database → cloud)

2. **ItemFilter Interface**
   - Should allow pluggable filters
   - Current code has hardcoded if-else chains

3. **AnalyticsCalculator Interface**
   - Should allow different analytics algorithms
   - Current code hardcodes all calculations in one method

4. **ItemValidator Interface**
   - Should abstract validation rules
   - Current code duplicates validation in multiple dialogs

5. **View Interfaces**
   - Should separate view from controller
   - Current code mixes Swing components with business logic

**Impact:**
- Tight coupling to concrete implementations
- Can't mock dependencies for testing
- Can't provide alternative implementations
- Violates Dependency Inversion Principle (see below)

---

### 1.5 Dependency Inversion Principle (DIP)

**Principle:** High-level modules should not depend on low-level modules. Both should depend on abstractions.

**Violations:**

#### Violation 1: Direct File System Dependency
```java
private void saveDataToFile() {
    try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE));
        // ... write data
    } catch (IOException ex) {
        ex.printStackTrace();
    }
}
```

**Problem:**
- `WardrobeApp` (high-level) directly depends on `FileWriter` (low-level)
- Can't swap to database, cloud storage, or in-memory storage
- Hardcoded file format
- Can't test without file system access

**Better Design:**
```java
interface ItemRepository {
    void saveItems(List<Item> items);
    List<Item> loadItems();
}

class FileItemRepository implements ItemRepository { ... }
class DatabaseItemRepository implements ItemRepository { ... }
```

#### Violation 2: Direct Swing Dependency
```java
public class WardrobeApp extends JFrame {
    private JTable itemTable;  // Concrete Swing class
    private DefaultTableModel itemTableModel;  // Concrete Swing class
    // ... business logic mixed with Swing components
}
```

**Problem:**
- Business logic depends on Swing UI framework
- Can't reuse logic in different UI (JavaFX, web, CLI)
- Can't test business logic without Swing

**Better Design:** Use MVC with view interfaces

#### Violation 3: String Array Data Structure
```java
private ArrayList<String[]> items = new ArrayList<>();
// items stored as: [name, category, color, season, occasion, brand, price, date]
```

**Problem:**
- All code depends on this low-level representation
- Index-based access is fragile: `item[2]` (what is index 2?)
- Can't add fields without breaking everything
- No type safety

**Better Design:**
```java
class ClothingItem {
    private String name;
    private Category category;
    private Color color;
    // ... proper encapsulation
}
```

---

## 2. Additional Design Anti-Patterns

### 2.1 God Class Anti-Pattern

**Description:** A class that knows too much or does too much.

**Evidence:** `WardrobeApp` class:
- **1000+ lines of code** in a single class
- **40+ methods** all in one class
- **Multiple responsibilities** (see SRP violations above)
- **No helper classes** or delegation

**Consequence:**
- Extremely difficult to understand
- Maintenance nightmare
- High coupling throughout
- Can't be tested in isolation

---

### 2.2 Code Duplication (DRY Violation)

**Don't Repeat Yourself principle:** Every piece of knowledge should have a single, authoritative representation.

#### Duplication 1: Add vs Edit Item Dialogs

`addItemDialog()` and `editItemDialog()` share 90% of the same code:

```java
// In addItemDialog():
JTextField nameField = new JTextField();
JComboBox<String> categoryBox = new JComboBox<>(
    new String[]{"Top", "Bottom", "Outerwear", "Shoes", "Accessory"}
);
JTextField colorField = new JTextField();
// ... 50 more lines

// In editItemDialog():
JTextField nameField = new JTextField(item[0]);  // Only difference: pre-filled
JComboBox<String> categoryBox = new JComboBox<>(
    new String[]{"Top", "Bottom", "Outerwear", "Shoes", "Accessory"}
);
categoryBox.setSelectedItem(item[1]);
JTextField colorField = new JTextField(item[2]);
// ... 50 more lines (DUPLICATED!)
```

**Impact:**
- Bug fixes must be applied twice
- UI changes must be made in two places
- Inconsistencies creep in over time

#### Duplication 2: Validation Logic

Input validation is copy-pasted in multiple places:

```java
// In addItemDialog():
if (name.isEmpty()) {
    JOptionPane.showMessageDialog(dialog, "Name is required!", "Error", ...);
    return;
}

// In editItemDialog():
if (name.isEmpty()) {
    JOptionPane.showMessageDialog(dialog, "Name is required!", "Error", ...);
    return;
}

// Same validation, different locations!
```

#### Duplication 3: Category/Season/Occasion Arrays

The same arrays are defined in 3+ places:
- `addItemDialog()`
- `editItemDialog()`
- `createSearchPanel()`

**Better Design:** Extract to constants or enum

---

### 2.3 Magic Numbers and Magic Strings

**Problem:** Hardcoded values scattered throughout code with no explanation.

#### Array Index Magic Numbers
```java
String name = item[0];      // What is index 0?
String category = item[1];  // What is index 1?
String color = item[2];     // What is index 2?
// ... and so on
```

**Better Design:** Use constants or proper objects
```java
private static final int INDEX_NAME = 0;
private static final int INDEX_CATEGORY = 1;
// OR better: use ClothingItem class with getters
```

#### String Parsing Magic
```java
if (log[1].startsWith("items:")) {
    String indicesStr = log[1].substring(6);  // Why 6?
    // ...
} else if (log[1].startsWith("outfit:")) {
    int outfitIdx = Integer.parseInt(log[1].substring(7));  // Why 7?
}
```

**Problem:** Fragile string parsing, no validation, unclear intent

---

### 2.4 Long Method Anti-Pattern

Several methods are excessively long:

1. **`calculateAndDisplayAnalytics()`** - 150+ lines
   - Multiple responsibilities
   - Multiple levels of nesting
   - Should be split into separate methods

2. **`addItemDialog()`** - 100+ lines
   - UI creation + validation + business logic
   - Should be split into UI creation, validation, and save logic

3. **`createOutfitDialog()`** - 80+ lines
   - Similar issues

**Impact:**
- Hard to understand
- Hard to test
- Hard to maintain
- Easy to introduce bugs

---

### 2.5 Primitive Obsession

**Problem:** Using primitive types (String, int) instead of small objects for domain concepts.

**Examples:**

1. **String Arrays for Items**
   ```java
   String[] item = {name, category, color, season, occasion, brand, price, date};
   ```
   Should be: `ClothingItem` class

2. **String Manipulation for Lists**
   ```java
   String indicesStr = selectedIndices.toString();  // "[1, 3, 5]"
   // Later: parse this string back to integers
   ```
   Should be: Proper collection serialization

3. **No Value Objects**
   - No `Color` class (just String)
   - No `Category` enum
   - No `Season` enum
   - No `Money` class for prices

**Impact:**
- No type safety
- Runtime errors from parsing
- No domain-specific behavior
- Unclear what values are valid

---

### 2.6 Poor Error Handling

**Problems:**

1. **Silent Failures**
   ```java
   try {
       int i = Integer.parseInt(idx);
   } catch (NumberFormatException ex) {
       // Ignore - just skip this item
   }
   ```

2. **Stack Trace Dumping**
   ```java
   catch (IOException ex) {
       ex.printStackTrace();  // Prints to console, user doesn't see it
       JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
   }
   ```

3. **No Recovery Mechanisms**
   - If file is corrupted, all data is lost
   - No backup strategy
   - No transaction support

---

### 2.7 Tight Coupling

**Examples:**

1. **UI Directly Manipulates Data**
   ```java
   saveButton.addActionListener(e -> {
       // Business logic directly in UI event handler
       items.add(item);
       saveDataToFile();
       refreshItemTable();
   });
   ```

2. **Business Logic Depends on UI Components**
   ```java
   private void logItemUsage(JTextArea usageArea) {
       // Business logic method takes UI component as parameter!
   }
   ```

3. **No Layer Separation**
   - Presentation layer can directly access data structures
   - Business logic calls UI update methods
   - Data layer doesn't exist - file I/O is inline

---

## 3. Architectural Issues

### 3.1 No MVC Separation

Despite proposing MVC architecture in Milestone 1, this prototype:
- Has no separate Model classes
- Has no separate View classes  
- Has no separate Controller classes
- Everything is in one monolithic class

### 3.2 No Layered Architecture

No separation between:
- **Presentation Layer** - UI rendering
- **Business Logic Layer** - domain rules and calculations
- **Data Access Layer** - persistence

All three are mixed together throughout `WardrobeApp`.

### 3.3 Fragile Data Storage

Custom text format:
```
ITEMS
Blue Jeans|Bottom|Blue|All-Season|Casual|Levi's|50.00|2024-01-15
OUTFITS
Casual Friday|[0, 2, 5]|casual,friday
USAGE
2024-11-23|items:0,2,
```

**Problems:**
- No schema version
- Pipe character `|` in data would break parsing
- No validation on load
- No migration strategy
- Inefficient (loads entire file every time)

---

## 4. Testing Challenges

This design makes testing nearly impossible:

### 4.1 Can't Unit Test Business Logic
- Business logic is embedded in UI event handlers
- Must launch full Swing UI to test anything
- Can't test analytics without rendering UI

### 4.2 Can't Mock Dependencies
- No interfaces to mock
- Direct file system access
- Direct Swing dependencies

### 4.3 Can't Test Individual Components
- Everything is in one class
- Can't test ItemManager separately from OutfitManager
- Can't test validation separately from UI

### 4.4 Integration Tests Only
- Only option is full end-to-end GUI tests
- Slow and brittle
- Hard to automate

---

## 5. Maintainability Issues

### 5.1 Change Impact Analysis

**Scenario:** Add a new item field "Size"

Required changes:
1. Modify String array size in `items` ArrayList
2. Update `addItemDialog()` to add UI field (2 places)
3. Update `editItemDialog()` to add UI field (duplicated code)
4. Update `refreshItemTable()` column count
5. Update `saveDataToFile()` format
6. Update `loadDataToFile()` parsing
7. Update search panel if filtering by size
8. Risk breaking existing data files

**Estimated effort:** 4-6 hours, high risk of bugs

**With clean design:** 30 minutes, low risk

### 5.2 Code Navigation

- Hard to find where specific features are implemented
- Ctrl+F is the only way to navigate
- No logical grouping of related functionality
- IDE code folding helps but isn't enough

### 5.3 Onboarding New Developers

- Would take hours to understand the 1000-line class
- No documentation of data structures
- No separation of concerns to guide learning
- High risk of breaking something when making changes

---

## 6. Performance Issues

### 6.1 Inefficient Operations

```java
// Refreshes entire table on every change
private void refreshItemTable() {
    itemTableModel.setRowCount(0);
    for (String[] item : items) {
        itemTableModel.addRow(item);
    }
}
```

**Problem:** O(n) operation for adding one item

### 6.2 Redundant Calculations

```java
// Recalculates ALL analytics every time
private void calculateAndDisplayAnalytics() {
    // No caching, no incremental updates
}
```

### 6.3 No Lazy Loading

- Loads all data on startup
- Can't scale to thousands of items

---

## 7. Summary of Violations

| Principle/Pattern | Violation Severity | Impact |
|-------------------|-------------------|--------|
| Single Responsibility | **SEVERE** | God class with 8+ responsibilities |
| Open/Closed | **HIGH** | Hardcoded logic throughout |
| Liskov Substitution | **MEDIUM** | No inheritance, but also not applicable |
| Interface Segregation | **HIGH** | Zero interfaces defined |
| Dependency Inversion | **SEVERE** | Direct dependencies on file system, Swing |
| DRY | **HIGH** | Massive code duplication |
| God Class | **SEVERE** | 1000+ lines in one class |
| Long Method | **HIGH** | 150+ line methods |
| Primitive Obsession | **HIGH** | String arrays everywhere |
| Tight Coupling | **SEVERE** | No separation of concerns |
| MVC Architecture | **SEVERE** | No MVC separation at all |

---

## 8. Refactoring Plan Preview

In Milestone 3, we will refactor this into a clean design:

### 8.1 Apply Design Patterns
- **Strategy Pattern** for filters and analytics
- **Factory Pattern** for creating items
- **Observer Pattern** for UI updates
- **Repository Pattern** for data access
- **MVC Pattern** for overall architecture

### 8.2 Create Proper Domain Models
- `ClothingItem` class hierarchy
- `Outfit` class
- `UsageLog` class
- Value objects for `Color`, `Category`, `Season`

### 8.3 Separate Layers
- **Model Layer:** Domain classes and business logic
- **View Layer:** UI components only
- **Controller Layer:** Mediates between model and view
- **Repository Layer:** Data persistence abstraction

### 8.4 Introduce Interfaces
- `ItemRepository` for data access
- `ItemFilter` for filtering
- `AnalyticsCalculator` for analytics
- `ItemValidator` for validation

### 8.5 Improve Code Quality
- Extract duplicated code
- Split long methods
- Use proper data structures
- Add comprehensive error handling

---

## Conclusion

This messy prototype successfully demonstrates **how NOT to design software**. It violates every major design principle and contains numerous anti-patterns. While functionally complete, it is:

- ✗ Not maintainable
- ✗ Not testable
- ✗ Not extensible
- ✗ Not scalable
- ✗ Not understandable (without significant effort)

This serves as the perfect baseline for demonstrating the value of good software design in Milestone 3's refactored version.


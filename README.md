# Personal Wardrobe Tracker

## Contributors
Anthony Wang

## Project Overview
A digital wardrobe management system for CS 396 Software Design that helps users organize their clothing collection, plan outfits, and make informed decisions about their wardrobe.

This repository contains:
- A functional prototype implementation
- Design documentation demonstrating SOLID principles and design patterns
- Analysis of the current design and refactoring plan

## Dependencies
- Java 8 or higher (JDK)
- No external libraries required (uses Swing for GUI)

## Build Instructions

### On macOS/Linux:
```bash
./compile.sh
./run.sh
```

### On Windows:
```bash
compile.bat
run.bat
```

### Manual Compilation:
```bash
mkdir -p bin
javac -d bin src/main/java/wardrobe/WardrobeApp.java
cd bin
java wardrobe.WardrobeApp
```

## Features (All 5 Use Cases)

1. **Item Management** - Add, edit, delete clothing items with attributes
2. **Outfit Creation** - Combine items into outfit combinations  
3. **Search & Filter** - Find items by name, category, color, season, occasion
4. **Usage Tracking** - Track when you wear items or outfits
5. **Analytics** - View wardrobe statistics and usage patterns

## Project Structure

```
├── src/main/java/wardrobe/
│   └── WardrobeApp.java          # Current implementation
├── docs/design/
│   ├── clean-class-diagram.puml  # Clean design with SOLID principles
│   ├── messy-class-diagram.puml  # Current implementation diagram
│   ├── design-principles-analysis.md
│   ├── anticipated-changes.md
│   ├── refactoring-plan.md
│   ├── design-violations.md
│   ├── requirements.md
│   └── architecture.md
└── milestone3                     # Final report
```

## Documentation

The `docs/design/` folder contains comprehensive design documentation including:
- Class diagrams (current and proposed clean design)
- SOLID principles analysis
- Design pattern applications
- Anticipated requirement changes analysis
- Detailed refactoring plan

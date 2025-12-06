#!/bin/bash
# Simple compilation script for the Wardrobe Tracker

echo "Compiling Wardrobe Tracker..."

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile the Java file
javac -d bin src/main/java/wardrobe/WardrobeApp.java

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    echo ""
    echo "To run the application, execute:"
    echo "  ./run.sh"
else
    echo "✗ Compilation failed!"
    exit 1
fi


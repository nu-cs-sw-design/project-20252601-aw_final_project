package wardrobe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * INTENTIONALLY MESSY CODE FOR MILESTONE 2
 * 
 * This is a GOD CLASS that violates multiple design principles:
 * - Single Responsibility Principle: Handles UI, business logic, data storage, and calculations
 * - Open/Closed Principle: Hardcoded logic that requires modification for extensions
 * - Dependency Inversion Principle: Depends directly on concrete implementations
 * - Interface Segregation: No interfaces at all
 * 
 * This class does EVERYTHING - manages UI, stores data, handles file I/O, 
 * performs calculations, and contains all business logic.
 */
public class WardrobeApp extends JFrame {
    
    // All data stored directly in the god class - no separation!
    private ArrayList<String[]> items = new ArrayList<>();  // [name, category, color, season, occasion, brand, price, purchaseDate]
    private ArrayList<String[]> outfits = new ArrayList<>();  // [name, itemIndices, tags]
    private ArrayList<String[]> usageLogs = new ArrayList<>();  // [date, itemIndices or outfitIndex]
    
    // UI components mixed with business logic - tight coupling!
    private JTabbedPane tabbedPane;
    private JTable itemTable;
    private DefaultTableModel itemTableModel;
    private JTable outfitTable;
    private DefaultTableModel outfitTableModel;
    private JTextArea analyticsArea;
    
    // Hardcoded file path - no configuration abstraction
    private static final String DATA_FILE = "wardrobe_data.txt";
    
    public WardrobeApp() {
        // UI setup mixed with data loading
        setTitle("Personal Wardrobe Tracker - Messy Prototype");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        loadDataFromFile();  // Business logic in constructor!
        
        initUI();  // More business logic in constructor
    }
    
    /**
     * Massive UI initialization method - does too many things!
     * Another violation of Single Responsibility Principle
     */
    private void initUI() {
        tabbedPane = new JTabbedPane();
        
        // Create all tabs
        tabbedPane.addTab("Items", createItemsPanel());
        tabbedPane.addTab("Outfits", createOutfitsPanel());
        tabbedPane.addTab("Usage Log", createUsagePanel());
        tabbedPane.addTab("Analytics", createAnalyticsPanel());
        tabbedPane.addTab("Search & Filter", createSearchPanel());
        
        add(tabbedPane);
    }
    
    /**
     * Creates items panel - UI code mixed with event handlers containing business logic
     */
    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table to display items
        String[] columnNames = {"Name", "Category", "Color", "Season", "Occasion", "Brand", "Price", "Purchase Date"};
        itemTableModel = new DefaultTableModel(columnNames, 0);
        itemTable = new JTable(itemTableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);
        
        refreshItemTable();  // Business logic call in UI setup
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Item");
        JButton editButton = new JButton("Edit Item");
        JButton deleteButton = new JButton("Delete Item");
        
        // Event handlers with inline business logic - violates separation of concerns!
        addButton.addActionListener(e -> addItemDialog());
        editButton.addActionListener(e -> editItemDialog());
        deleteButton.addActionListener(e -> deleteItem());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Massive dialog method with validation logic, UI code, and data manipulation all mixed together!
     * This method does WAY too many things - perfect example of messy code
     */
    private void addItemDialog() {
        JDialog dialog = new JDialog(this, "Add New Item", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));
        
        JTextField nameField = new JTextField();
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Top", "Bottom", "Outerwear", "Shoes", "Accessory"});
        JTextField colorField = new JTextField();
        JComboBox<String> seasonBox = new JComboBox<>(new String[]{"Spring", "Summer", "Fall", "Winter", "All-Season"});
        JComboBox<String> occasionBox = new JComboBox<>(new String[]{"Casual", "Business", "Formal", "Athletic"});
        JTextField brandField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        
        panel.add(new JLabel("Name*:"));
        panel.add(nameField);
        panel.add(new JLabel("Category*:"));
        panel.add(categoryBox);
        panel.add(new JLabel("Color*:"));
        panel.add(colorField);
        panel.add(new JLabel("Season:"));
        panel.add(seasonBox);
        panel.add(new JLabel("Occasion:"));
        panel.add(occasionBox);
        panel.add(new JLabel("Brand:"));
        panel.add(brandField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Purchase Date:"));
        panel.add(dateField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        // Inline validation and business logic - no separate validation class!
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String category = (String) categoryBox.getSelectedItem();
            String color = colorField.getText().trim();
            String season = (String) seasonBox.getSelectedItem();
            String occasion = (String) occasionBox.getSelectedItem();
            String brand = brandField.getText().trim();
            String price = priceField.getText().trim();
            String date = dateField.getText().trim();
            
            // Validation logic mixed with event handling - messy!
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (color.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Color is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Direct data manipulation - no abstraction layer!
            String[] item = {name, category, color, season, occasion, brand, price, date};
            items.add(item);
            
            // File I/O mixed with UI logic!
            saveDataToFile();
            refreshItemTable();
            
            JOptionPane.showMessageDialog(dialog, "Item added successfully!");
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Edit dialog - duplicated code from addItemDialog! 
     * Violates DRY (Don't Repeat Yourself) principle
     */
    private void editItemDialog() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] item = items.get(selectedRow);
        
        JDialog dialog = new JDialog(this, "Edit Item", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));
        
        // Duplicated UI setup code!
        JTextField nameField = new JTextField(item[0]);
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Top", "Bottom", "Outerwear", "Shoes", "Accessory"});
        categoryBox.setSelectedItem(item[1]);
        JTextField colorField = new JTextField(item[2]);
        JComboBox<String> seasonBox = new JComboBox<>(new String[]{"Spring", "Summer", "Fall", "Winter", "All-Season"});
        seasonBox.setSelectedItem(item[3]);
        JComboBox<String> occasionBox = new JComboBox<>(new String[]{"Casual", "Business", "Formal", "Athletic"});
        occasionBox.setSelectedItem(item[4]);
        JTextField brandField = new JTextField(item[5]);
        JTextField priceField = new JTextField(item[6]);
        JTextField dateField = new JTextField(item[7]);
        
        panel.add(new JLabel("Name*:"));
        panel.add(nameField);
        panel.add(new JLabel("Category*:"));
        panel.add(categoryBox);
        panel.add(new JLabel("Color*:"));
        panel.add(colorField);
        panel.add(new JLabel("Season:"));
        panel.add(seasonBox);
        panel.add(new JLabel("Occasion:"));
        panel.add(occasionBox);
        panel.add(new JLabel("Brand:"));
        panel.add(brandField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Purchase Date:"));
        panel.add(dateField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String category = (String) categoryBox.getSelectedItem();
            String color = colorField.getText().trim();
            String season = (String) seasonBox.getSelectedItem();
            String occasion = (String) occasionBox.getSelectedItem();
            String brand = brandField.getText().trim();
            String price = priceField.getText().trim();
            String date = dateField.getText().trim();
            
            // Duplicated validation logic!
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (color.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Color is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Direct array manipulation
            items.set(selectedRow, new String[]{name, category, color, season, occasion, brand, price, date});
            
            saveDataToFile();
            refreshItemTable();
            
            JOptionPane.showMessageDialog(dialog, "Item updated successfully!");
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Simple delete with direct data manipulation
     */
    private void deleteItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", 
                                                      "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            items.remove(selectedRow);
            saveDataToFile();
            refreshItemTable();
            JOptionPane.showMessageDialog(this, "Item deleted successfully!");
        }
    }
    
    /**
     * Refresh table - UI update logic
     */
    private void refreshItemTable() {
        itemTableModel.setRowCount(0);
        for (String[] item : items) {
            itemTableModel.addRow(item);
        }
    }
    
    /**
     * Creates outfits panel - more mixed responsibilities
     */
    private JPanel createOutfitsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"Outfit Name", "Items", "Tags"};
        outfitTableModel = new DefaultTableModel(columnNames, 0);
        outfitTable = new JTable(outfitTableModel);
        JScrollPane scrollPane = new JScrollPane(outfitTable);
        
        refreshOutfitTable();
        
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create Outfit");
        JButton deleteButton = new JButton("Delete Outfit");
        
        createButton.addActionListener(e -> createOutfitDialog());
        deleteButton.addActionListener(e -> deleteOutfit());
        
        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create outfit dialog - business logic mixed with UI
     */
    private void createOutfitDialog() {
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add items to your wardrobe first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Create Outfit", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        JTextField outfitNameField = new JTextField();
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Outfit Name: "), BorderLayout.WEST);
        namePanel.add(outfitNameField, BorderLayout.CENTER);
        
        // Item selection with checkboxes
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
        
        for (int i = 0; i < items.size(); i++) {
            String[] item = items.get(i);
            JCheckBox cb = new JCheckBox(item[0] + " (" + item[1] + " - " + item[2] + ")");
            checkBoxes.add(cb);
            itemsPanel.add(cb);
        }
        
        JScrollPane itemScrollPane = new JScrollPane(itemsPanel);
        
        JTextField tagsField = new JTextField();
        JPanel tagsPanel = new JPanel(new BorderLayout());
        tagsPanel.add(new JLabel("Tags (comma-separated): "), BorderLayout.WEST);
        tagsPanel.add(tagsField, BorderLayout.CENTER);
        
        JButton saveButton = new JButton("Save Outfit");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String outfitName = outfitNameField.getText().trim();
            if (outfitName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Outfit name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Collect selected items - inline logic
            ArrayList<Integer> selectedIndices = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    selectedIndices.add(i);
                }
            }
            
            if (selectedIndices.size() < 2) {
                JOptionPane.showMessageDialog(dialog, "Please select at least 2 items for an outfit!", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Store outfit data as string array - poor data structure choice!
            String indicesStr = selectedIndices.toString();  // Convert to string - bad design!
            String tags = tagsField.getText().trim();
            String[] outfit = {outfitName, indicesStr, tags};
            outfits.add(outfit);
            
            saveDataToFile();
            refreshOutfitTable();
            
            JOptionPane.showMessageDialog(dialog, "Outfit created successfully!");
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        panel.add(namePanel, BorderLayout.NORTH);
        panel.add(itemScrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(tagsPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void deleteOutfit() {
        int selectedRow = outfitTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an outfit to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this outfit?", 
                                                      "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            outfits.remove(selectedRow);
            saveDataToFile();
            refreshOutfitTable();
            JOptionPane.showMessageDialog(this, "Outfit deleted successfully!");
        }
    }
    
    private void refreshOutfitTable() {
        outfitTableModel.setRowCount(0);
        for (String[] outfit : outfits) {
            // Parse indices string back - inefficient and messy!
            String indicesStr = outfit[1].replace("[", "").replace("]", "");
            String[] indices = indicesStr.split(", ");
            
            StringBuilder itemNames = new StringBuilder();
            for (String idx : indices) {
                try {
                    int i = Integer.parseInt(idx);
                    if (i < items.size()) {
                        itemNames.append(items.get(i)[0]).append(", ");
                    }
                } catch (NumberFormatException ex) {
                    // Ignore parsing errors - poor error handling!
                }
            }
            
            String itemsDisplay = itemNames.length() > 0 ? itemNames.substring(0, itemNames.length() - 2) : "";
            outfitTableModel.addRow(new Object[]{outfit[0], itemsDisplay, outfit[2]});
        }
    }
    
    /**
     * Usage tracking panel - more god class behavior
     */
    private JPanel createUsagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JTextArea usageArea = new JTextArea();
        usageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(usageArea);
        
        JPanel buttonPanel = new JPanel();
        JButton logItemButton = new JButton("Log Item Usage Today");
        JButton logOutfitButton = new JButton("Log Outfit Usage Today");
        JButton viewHistoryButton = new JButton("View History");
        
        logItemButton.addActionListener(e -> logItemUsage(usageArea));
        logOutfitButton.addActionListener(e -> logOutfitUsage(usageArea));
        viewHistoryButton.addActionListener(e -> displayUsageHistory(usageArea));
        
        buttonPanel.add(logItemButton);
        buttonPanel.add(logOutfitButton);
        buttonPanel.add(viewHistoryButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Log item usage - business logic mixed with UI
     */
    private void logItemUsage(JTextArea usageArea) {
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items to log!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create selection dialog
        String[] itemNames = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            itemNames[i] = items.get(i)[0] + " (" + items.get(i)[1] + ")";
        }
        
        JList<String> list = new JList<>(itemNames);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        
        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Select Items Worn Today", 
                                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            int[] selectedIndices = list.getSelectedIndices();
            if (selectedIndices.length == 0) {
                JOptionPane.showMessageDialog(this, "Please select at least one item!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Store usage log - string manipulation again!
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            StringBuilder indicesStr = new StringBuilder();
            for (int idx : selectedIndices) {
                indicesStr.append(idx).append(",");
            }
            String[] log = {date, "items:" + indicesStr.toString()};
            usageLogs.add(log);
            
            saveDataToFile();
            JOptionPane.showMessageDialog(this, "Usage logged successfully!");
            displayUsageHistory(usageArea);
        }
    }
    
    private void logOutfitUsage(JTextArea usageArea) {
        if (outfits.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No outfits to log!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] outfitNames = new String[outfits.size()];
        for (int i = 0; i < outfits.size(); i++) {
            outfitNames[i] = outfits.get(i)[0];
        }
        
        String selected = (String) JOptionPane.showInputDialog(this, "Select Outfit Worn Today:", 
                                                                "Log Outfit Usage", JOptionPane.QUESTION_MESSAGE, 
                                                                null, outfitNames, outfitNames[0]);
        
        if (selected != null) {
            int outfitIndex = -1;
            for (int i = 0; i < outfits.size(); i++) {
                if (outfits.get(i)[0].equals(selected)) {
                    outfitIndex = i;
                    break;
                }
            }
            
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String[] log = {date, "outfit:" + outfitIndex};
            usageLogs.add(log);
            
            saveDataToFile();
            JOptionPane.showMessageDialog(this, "Usage logged successfully!");
            displayUsageHistory(usageArea);
        }
    }
    
    private void displayUsageHistory(JTextArea usageArea) {
        usageArea.setText("");
        usageArea.append("=== USAGE HISTORY ===\n\n");
        
        for (String[] log : usageLogs) {
            usageArea.append("Date: " + log[0] + "\n");
            
            if (log[1].startsWith("items:")) {
                String indicesStr = log[1].substring(6);
                String[] indices = indicesStr.split(",");
                usageArea.append("Items worn: ");
                for (String idx : indices) {
                    try {
                        int i = Integer.parseInt(idx.trim());
                        if (i < items.size()) {
                            usageArea.append(items.get(i)[0] + ", ");
                        }
                    } catch (NumberFormatException ex) {
                        // Ignore
                    }
                }
                usageArea.append("\n");
            } else if (log[1].startsWith("outfit:")) {
                int outfitIdx = Integer.parseInt(log[1].substring(7));
                if (outfitIdx < outfits.size()) {
                    usageArea.append("Outfit worn: " + outfits.get(outfitIdx)[0] + "\n");
                }
            }
            usageArea.append("\n");
        }
    }
    
    /**
     * Analytics panel - calculation logic mixed with UI
     */
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        analyticsArea = new JTextArea();
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(analyticsArea);
        
        JButton refreshButton = new JButton("Refresh Analytics");
        refreshButton.addActionListener(e -> calculateAndDisplayAnalytics());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        calculateAndDisplayAnalytics();
        
        return panel;
    }
    
    /**
     * HUGE method that calculates all analytics - violates Single Responsibility!
     * This method is over 100 lines and does multiple things
     */
    private void calculateAndDisplayAnalytics() {
        analyticsArea.setText("");
        analyticsArea.append("=== WARDROBE ANALYTICS ===\n\n");
        
        if (items.isEmpty()) {
            analyticsArea.append("No items in wardrobe yet. Add some items to see analytics!\n");
            return;
        }
        
        // Calculate total items by category - inline calculation logic
        analyticsArea.append("--- Items by Category ---\n");
        HashMap<String, Integer> categoryCount = new HashMap<>();
        for (String[] item : items) {
            String category = item[1];
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        }
        for (String category : categoryCount.keySet()) {
            analyticsArea.append(category + ": " + categoryCount.get(category) + "\n");
        }
        analyticsArea.append("Total Items: " + items.size() + "\n\n");
        
        // Calculate color distribution
        analyticsArea.append("--- Color Distribution ---\n");
        HashMap<String, Integer> colorCount = new HashMap<>();
        for (String[] item : items) {
            String color = item[2];
            colorCount.put(color, colorCount.getOrDefault(color, 0) + 1);
        }
        for (String color : colorCount.keySet()) {
            analyticsArea.append(color + ": " + colorCount.get(color) + "\n");
        }
        analyticsArea.append("\n");
        
        // Calculate season distribution
        analyticsArea.append("--- Season Distribution ---\n");
        HashMap<String, Integer> seasonCount = new HashMap<>();
        for (String[] item : items) {
            String season = item[3];
            seasonCount.put(season, seasonCount.getOrDefault(season, 0) + 1);
        }
        for (String season : seasonCount.keySet()) {
            analyticsArea.append(season + ": " + seasonCount.get(season) + "\n");
        }
        analyticsArea.append("\n");
        
        // Calculate usage statistics - complex logic mixed in
        if (!usageLogs.isEmpty()) {
            analyticsArea.append("--- Usage Statistics ---\n");
            HashMap<Integer, Integer> itemUsageCount = new HashMap<>();
            
            // Count how many times each item was worn
            for (String[] log : usageLogs) {
                if (log[1].startsWith("items:")) {
                    String indicesStr = log[1].substring(6);
                    String[] indices = indicesStr.split(",");
                    for (String idx : indices) {
                        try {
                            int i = Integer.parseInt(idx.trim());
                            itemUsageCount.put(i, itemUsageCount.getOrDefault(i, 0) + 1);
                        } catch (NumberFormatException ex) {
                            // Ignore
                        }
                    }
                } else if (log[1].startsWith("outfit:")) {
                    int outfitIdx = Integer.parseInt(log[1].substring(7));
                    if (outfitIdx < outfits.size()) {
                        String indicesStr = outfits.get(outfitIdx)[1].replace("[", "").replace("]", "");
                        String[] indices = indicesStr.split(", ");
                        for (String idx : indices) {
                            try {
                                int i = Integer.parseInt(idx);
                                itemUsageCount.put(i, itemUsageCount.getOrDefault(i, 0) + 1);
                            } catch (NumberFormatException ex) {
                                // Ignore
                            }
                        }
                    }
                }
            }
            
            // Find most worn items - sorting logic inline
            ArrayList<Map.Entry<Integer, Integer>> sortedUsage = new ArrayList<>(itemUsageCount.entrySet());
            sortedUsage.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            
            analyticsArea.append("Most Worn Items (Top 5):\n");
            int count = 0;
            for (Map.Entry<Integer, Integer> entry : sortedUsage) {
                if (count >= 5) break;
                int itemIdx = entry.getKey();
                if (itemIdx < items.size()) {
                    analyticsArea.append("  " + items.get(itemIdx)[0] + " - worn " + entry.getValue() + " times\n");
                }
                count++;
            }
            
            // Find least worn items
            analyticsArea.append("\nLeast Worn Items:\n");
            for (int i = 0; i < items.size(); i++) {
                if (!itemUsageCount.containsKey(i)) {
                    analyticsArea.append("  " + items.get(i)[0] + " - never worn\n");
                }
            }
            
            analyticsArea.append("\nTotal Usage Logs: " + usageLogs.size() + "\n");
        } else {
            analyticsArea.append("--- Usage Statistics ---\n");
            analyticsArea.append("No usage data yet. Start logging what you wear!\n");
        }
        
        // Calculate cost per wear for items with prices
        analyticsArea.append("\n--- Cost Analysis ---\n");
        double totalValue = 0;
        int itemsWithPrice = 0;
        for (String[] item : items) {
            String priceStr = item[6];
            if (!priceStr.isEmpty()) {
                try {
                    double price = Double.parseDouble(priceStr);
                    totalValue += price;
                    itemsWithPrice++;
                } catch (NumberFormatException ex) {
                    // Ignore invalid prices
                }
            }
        }
        
        if (itemsWithPrice > 0) {
            analyticsArea.append("Total Wardrobe Value: $" + String.format("%.2f", totalValue) + "\n");
            analyticsArea.append("Items with Price Info: " + itemsWithPrice + " / " + items.size() + "\n");
            analyticsArea.append("Average Item Price: $" + String.format("%.2f", totalValue / itemsWithPrice) + "\n");
        } else {
            analyticsArea.append("No price information available.\n");
        }
    }
    
    /**
     * Search and filter panel - hardcoded filter types violate Open/Closed Principle
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Filter panel with hardcoded options
        JPanel filterPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        
        JTextField searchField = new JTextField();
        JComboBox<String> categoryFilter = new JComboBox<>(new String[]{"All", "Top", "Bottom", "Outerwear", "Shoes", "Accessory"});
        JComboBox<String> seasonFilter = new JComboBox<>(new String[]{"All", "Spring", "Summer", "Fall", "Winter", "All-Season"});
        JComboBox<String> occasionFilter = new JComboBox<>(new String[]{"All", "Casual", "Business", "Formal", "Athletic"});
        JTextField colorFilter = new JTextField();
        
        filterPanel.add(new JLabel("Search by Name:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);
        filterPanel.add(new JLabel("Season:"));
        filterPanel.add(seasonFilter);
        filterPanel.add(new JLabel("Occasion:"));
        filterPanel.add(occasionFilter);
        filterPanel.add(new JLabel("Color:"));
        filterPanel.add(colorFilter);
        
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear Filters");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        
        filterPanel.add(new JLabel(""));
        filterPanel.add(buttonPanel);
        
        // Results table
        String[] columnNames = {"Name", "Category", "Color", "Season", "Occasion"};
        DefaultTableModel searchTableModel = new DefaultTableModel(columnNames, 0);
        JTable searchTable = new JTable(searchTableModel);
        JScrollPane scrollPane = new JScrollPane(searchTable);
        
        // Massive inline filter logic - violates Open/Closed Principle!
        searchButton.addActionListener(e -> {
            searchTableModel.setRowCount(0);
            
            String searchText = searchField.getText().toLowerCase().trim();
            String category = (String) categoryFilter.getSelectedItem();
            String season = (String) seasonFilter.getSelectedItem();
            String occasion = (String) occasionFilter.getSelectedItem();
            String color = colorFilter.getText().toLowerCase().trim();
            
            int matchCount = 0;
            
            // Giant nested if statements - terrible design!
            for (String[] item : items) {
                boolean matches = true;
                
                // Hardcoded filter logic for each type
                if (!searchText.isEmpty() && !item[0].toLowerCase().contains(searchText)) {
                    matches = false;
                }
                
                if (!category.equals("All") && !item[1].equals(category)) {
                    matches = false;
                }
                
                if (!season.equals("All") && !item[3].equals(season)) {
                    matches = false;
                }
                
                if (!occasion.equals("All") && !item[4].equals(occasion)) {
                    matches = false;
                }
                
                if (!color.isEmpty() && !item[2].toLowerCase().contains(color)) {
                    matches = false;
                }
                
                if (matches) {
                    searchTableModel.addRow(new Object[]{item[0], item[1], item[2], item[3], item[4]});
                    matchCount++;
                }
            }
            
            JOptionPane.showMessageDialog(panel, "Found " + matchCount + " matching items.");
        });
        
        clearButton.addActionListener(e -> {
            searchField.setText("");
            categoryFilter.setSelectedIndex(0);
            seasonFilter.setSelectedIndex(0);
            occasionFilter.setSelectedIndex(0);
            colorFilter.setText("");
            searchTableModel.setRowCount(0);
        });
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * File I/O methods - no abstraction, direct file access, terrible error handling
     * Violates Dependency Inversion Principle - depends on concrete file system
     */
    private void saveDataToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE));
            
            // Terrible file format - just dump everything as strings!
            writer.write("ITEMS\n");
            for (String[] item : items) {
                writer.write(String.join("|", item) + "\n");
            }
            
            writer.write("OUTFITS\n");
            for (String[] outfit : outfits) {
                writer.write(String.join("|", outfit) + "\n");
            }
            
            writer.write("USAGE\n");
            for (String[] log : usageLogs) {
                writer.write(String.join("|", log) + "\n");
            }
            
            writer.close();
        } catch (IOException ex) {
            // Terrible error handling - just print to console!
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Load data - string parsing nightmare, no error recovery
     */
    private void loadDataFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;  // No file yet, that's ok
        }
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE));
            String line;
            String section = "";
            
            while ((line = reader.readLine()) != null) {
                if (line.equals("ITEMS")) {
                    section = "ITEMS";
                    continue;
                } else if (line.equals("OUTFITS")) {
                    section = "OUTFITS";
                    continue;
                } else if (line.equals("USAGE")) {
                    section = "USAGE";
                    continue;
                }
                
                // Parse based on section - fragile string manipulation!
                if (section.equals("ITEMS") && !line.isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 8) {
                        items.add(parts);
                    }
                } else if (section.equals("OUTFITS") && !line.isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        outfits.add(parts);
                    }
                } else if (section.equals("USAGE") && !line.isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 2) {
                        usageLogs.add(parts);
                    }
                }
            }
            
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch on EDT
        SwingUtilities.invokeLater(() -> {
            WardrobeApp app = new WardrobeApp();
            app.setVisible(true);
        });
    }
}


import java.util.*;
import java.sql.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static List<Category> catalog = new ArrayList<>();
    static List<OrderItem> cart = new ArrayList<>();
    static UserDetails user = null;
    static final List<String> allowedCities = Arrays.asList("Chennai", "Chengalpattu", "Kancheepuram");
    static final List<Double> qtyOptions = Arrays.asList(0.25, 0.5, 1.0);

    public static void main(String[] args) {
        initCatalog();
        System.out.println("Welcome to SpringGrocery-Organic Store");
        System.out.println("Good Day, Please place your Order and Enter your Details for Home Delivery");
        System.out.println("Free home delivery on orders above Rs.500");
        System.out.println("Currently serving: Chennai, Chengalpattu, Kancheepuram");
        mainMenu();
    }

    static void initCatalog() {
        List<Double> qtyKg = Arrays.asList(0.25, 0.5, 1.0);
        List<Double> qtyLitre = Arrays.asList(0.25, 0.5, 1.0);

        // Clear existing catalog to prevent duplicates
        catalog.clear();

        // Try loading from database first
        try {
            List<Category> dbCatalog = Category.loadAllCategories();
            if (dbCatalog != null && !dbCatalog.isEmpty()) {
                catalog = dbCatalog;
                System.out.println("Loaded catalog from database successfully");
                // Remove any empty categories
                catalog.removeIf(category -> category.items == null || category.items.isEmpty());
                return;
            }
        } catch (SQLException e) {
            System.out.println("Database warning: " + e.getMessage());
        }

        // Fallback to hardcoded catalog
        System.out.println("Initializing default product catalog...");
        
        // Fruits
        Category fruits = new Category("Fruits");
        fruits.items.add(new Item("Banana - Yelakki", "kg", 10, qtyKg, 90));
        fruits.items.add(new Item("Banana - Red Banana", "kg", 10, qtyKg, 100));
        fruits.items.add(new Item("Mango - Banganapalli", "kg", 10, qtyKg, 60));
        fruits.items.add(new Item("Papaya", "kg", 10, qtyKg, 30));
        fruits.items.add(new Item("Guava", "kg", 10, qtyKg, 70));
        catalog.add(fruits);

        // Vegetables
        Category vegetables = new Category("Vegetables");
        vegetables.items.add(new Item("Onion", "kg", 10, qtyKg, 30));
        vegetables.items.add(new Item("Tomato", "kg", 10, qtyKg, 25));
        vegetables.items.add(new Item("Potato", "kg", 10, qtyKg, 40));
        vegetables.items.add(new Item("Carrot", "kg", 10, qtyKg, 70));
        vegetables.items.add(new Item("Brinjal", "kg", 10, qtyKg, 50));
        catalog.add(vegetables);

        // Rice & Cereals
        Category riceCereals = new Category("Rice & Cereals");
        riceCereals.items.add(new Item("Basmati Rice", "kg", 10, qtyKg, 120));
        riceCereals.items.add(new Item("Idli Rice", "kg", 10, qtyKg, 50));
        riceCereals.items.add(new Item("Ragi", "kg", 10, qtyKg, 70));
        riceCereals.items.add(new Item("Wheat", "kg", 10, qtyKg, 35));
        catalog.add(riceCereals);

        // Oils & Ghee
        Category oilsGhee = new Category("Oils & Ghee");
        oilsGhee.items.add(new Item("Sunflower Oil", "litre", 10, qtyLitre, 180));
        oilsGhee.items.add(new Item("Coconut Oil", "litre", 10, qtyLitre, 250));
        oilsGhee.items.add(new Item("Ghee", "litre", 10, qtyLitre, 300));
        catalog.add(oilsGhee);

        // Ensure no duplicate categories
        Set<String> categoryNames = new HashSet<>();
        catalog.removeIf(category -> !categoryNames.add(category.name));
    }

    static void mainMenu() {
        while(true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Enter User Details");
            System.out.println("2. View Products");
            System.out.println("3. View Bill and Delivery Details");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();

            switch(input) {
                case "1": 
                    enterUserDetails(); 
                    break;
                case "2":
                    if(user == null) {
                        System.out.println("Please enter user details first (option 1)");
                    } else {
                        viewCategories();
                    }
                    break;
                case "3":
                    if(cart.isEmpty()) {
                        System.out.println("No items ordered yet.");
                    } else if(user == null) {
                        System.out.println("Please enter user details first (option 1)");
                    } else {
                        displayBillAndDetails();
                    }
                    break;
                case "4":
                    System.out.println("Thank you for shopping with SpringGrocery!");
                    System.exit(0);
                    break;
                default: 
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    static void enterUserDetails() {
        UserDetails tempUser = new UserDetails();

        System.out.print("\nEnter your Name: ");
        tempUser.name = scanner.nextLine().trim();

        while(true) {
            System.out.print("Enter Phone Number (10 digits): ");
            tempUser.phone = scanner.nextLine().trim();
            if(tempUser.phone.matches("\\d{10}")) break;
            System.out.println("Invalid! Please enter exactly 10 digits.");
        }

        while(true) {
            System.out.print("Enter Alternative Phone (10 digits): ");
            tempUser.altPhone = scanner.nextLine().trim();
            if(tempUser.altPhone.matches("\\d{10}")) break;
            System.out.println("Invalid! Please enter exactly 10 digits.");
        }

        while(true) {
            System.out.print("Enter Pincode (6 digits): ");
            tempUser.pincode = scanner.nextLine().trim();
            if(tempUser.pincode.matches("\\d{6}")) break;
            System.out.println("Invalid! Please enter exactly 6 digits.");
        }

        tempUser.state = "TamilNadu";

        System.out.println("\nChoose your City:");
        for(int i = 0; i < allowedCities.size(); i++) {
            System.out.println((i+1) + ". " + allowedCities.get(i));
        }
        
        int cityChoice;
        while(true) {
            System.out.print("Enter choice (1-" + allowedCities.size() + "): ");
            try {
                cityChoice = Integer.parseInt(scanner.nextLine().trim());
                if(cityChoice >= 1 && cityChoice <= allowedCities.size()) {
                    tempUser.city = allowedCities.get(cityChoice-1);
                    break;
                }
                System.out.println("Invalid choice. Please enter between 1 and " + allowedCities.size());
            } catch(NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        System.out.print("Enter Address Line 1: ");
        tempUser.addressLine1 = scanner.nextLine().trim();

        System.out.print("Enter Address Line 2: ");
        tempUser.addressLine2 = scanner.nextLine().trim();

        System.out.println("\nChoose Delivery Date:");
        System.out.println("1. Today");
        System.out.println("2. Tomorrow");
        System.out.print("Enter choice (1-2): ");
        
        String dateChoice = scanner.nextLine().trim();
        tempUser.deliveryDate = dateChoice.equals("2") ? "Tomorrow" : "Today";

        user = tempUser;
        try {
            user.saveToDatabase();
            System.out.println("\nUser details saved successfully!");
        } catch (SQLException e) {
            System.out.println("\nNote: User details saved locally (database unavailable)");
        }
    }

    static void viewCategories() {
        while(true) {
            System.out.println("\nProduct Categories:");
            for(int i = 0; i < catalog.size(); i++) {
                System.out.println((i+1) + ". " + catalog.get(i).name + 
                                 " (" + catalog.get(i).items.size() + " products)");
            }
            System.out.println("-1. Back to Main Menu");
            System.out.print("Choose category: ");
            String input = scanner.nextLine().trim();

            if(input.equals("-1")) return;

            try {
                int choice = Integer.parseInt(input);
                if(choice >= 1 && choice <= catalog.size()) {
                    viewProducts(catalog.get(choice - 1));
                } else {
                    System.out.println("Please enter a number between 1 and " + catalog.size());
                }
            } catch(NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    static void viewProducts(Category category) {
        while(true) {
            System.out.println("\n" + category.name + " Products:");
            for(int i = 0; i < category.items.size(); i++) {
                Item item = category.items.get(i);
                System.out.printf("%d. %-20s %5.2f %-5s Rs.%6.2f%n",
                    (i+1), item.name, item.availableStock, item.unit, item.pricePerUnit);
            }
            System.out.println("-1. Back to Categories");
            System.out.print("Choose product: ");
            String input = scanner.nextLine().trim();

            if(input.equals("-1")) return;

            try {
                int choice = Integer.parseInt(input);
                if(choice >= 1 && choice <= category.items.size()) {
                    productMenu(category.items.get(choice-1));
                } else {
                    System.out.println("Please enter a number between 1 and " + category.items.size());
                }
            } catch(NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    static void productMenu(Item item) {
        while(true) {
            System.out.println("\nProduct: " + item.name);
            System.out.println("Available: " + item.availableStock + " " + item.unit);
            System.out.println("Price: Rs." + item.pricePerUnit + " per " + item.unit);
            System.out.println("Available Quantities:");
            
            for(int i = 0; i < item.allowedQuantities.size(); i++) {
                System.out.println((i+1) + ". " + item.allowedQuantities.get(i) + " " + item.unit);
            }
            System.out.println((item.allowedQuantities.size()+1) + ". Back to Products");
            
            System.out.print("Choose quantity: ");
            String input = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);
                if(choice == item.allowedQuantities.size()+1) return;
                
                if(choice >= 1 && choice <= item.allowedQuantities.size()) {
                    double qty = item.allowedQuantities.get(choice-1);
                    if(qty > item.availableStock) {
                        System.out.println("Only " + item.availableStock + " " + item.unit + " available.");
                        continue;
                    }
                    item.availableStock -= qty;
                    cart.add(new OrderItem(item, qty));
                    try {
                        item.updateStock();
                    } catch (SQLException e) {
                        System.out.println("Note: Stock updated locally");
                    }
                    System.out.printf("\nAdded to cart: %.2f %s of %s%n", qty, item.unit, item.name);
                } else {
                    System.out.println("Invalid choice");
                }
            } catch(Exception e) {
                System.out.println("Invalid input");
            }
        }
    }

    static void displayBillAndDetails() {
        System.out.println("\n=== Order Summary ===");
        System.out.println("Items Ordered:");
        double subtotal = 0;
        
        for(OrderItem item : cart) {
            System.out.printf("- %-20s %4.2f %-5s Rs.%6.2f%n",
                item.item.name, item.quantityOrdered, item.item.unit, item.getTotalPrice());
            subtotal += item.getTotalPrice();
        }
        
        System.out.printf("\nSubtotal: Rs.%.2f%n", subtotal);
        
        double delivery = subtotal >= 500 ? 0 : 100;
        if(delivery > 0) {
            System.out.println("Delivery Charge: Rs.100.00");
        } else {
            System.out.println("Delivery: FREE (Order above Rs.500)");
        }
        
        System.out.printf("Total Amount: Rs.%.2f%n", subtotal + delivery);
        
        System.out.println("\n=== Delivery Details ===");
        System.out.println(user);
        
        // Save order to database
        if(user != null && user.id > 0) {
            try {
                for(OrderItem item : cart) {
                    item.saveToDatabase(user.id);
                }
                System.out.println("\nOrder saved to database successfully!");
            } catch(SQLException e) {
                System.out.println("\nWarning: Could not save order to database: " + e.getMessage());
            }
        }
        
        System.out.println("\nThank you for your order!");
    }
}
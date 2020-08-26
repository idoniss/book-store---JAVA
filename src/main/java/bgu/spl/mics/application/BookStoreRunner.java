package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import com.google.gson.annotations.SerializedName;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static JsonAnalyzer analyzer;
    public static Inventory inventory = Inventory.getInstance();
    public static ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
    public static LinkedList<Thread> threads = new LinkedList<>();
    public static HashMap<Integer, Customer> customersToPrint = new HashMap<>();
    public static MoneyRegister register = MoneyRegister.getInstance();
    public static CountDownLatch latch;

    /**
     * Object that obtain all Json data.
     */
    private class JsonAnalyzer {
        @SerializedName("initialInventory")
        MockBook[] booksToLoad;
        public class MockBook {
            private String bookTitle;
            private int amount;
            private int price;
        }
        @SerializedName("initialResources")
        Car[] vehiclesToLoad;
        public class Car {
            Vehicle[] vehicles;
        }
        public class Vehicle {
            private int license;
            private int speed;
        }
        @SerializedName("services")
        ServicesInit servicesToLoad;

        public class ServicesInit {
            TimeServiceToLoad time;

            public class TimeServiceToLoad {
                private int speed;
                private int duration;
            }
            @SerializedName("selling")
            int sellingServiceToLoad;
            @SerializedName("inventoryService")
            int inventoryServiceToLoad;
            @SerializedName("logistics")
            int logisticsServiceToLoad;
            @SerializedName("resourcesService")
            int resourcesServiceToLoad;
            @SerializedName("customers")
            CustomerToLoad[] customers;

            public class CustomerToLoad {
                @SerializedName("id")
                private int id;
                @SerializedName("name")
                private String name;
                @SerializedName("address")
                private String address;
                @SerializedName("distance")
                private int distance;
                @SerializedName("creditCard")
                CreditCardToLoad creditCardToLoad;

                public class CreditCardToLoad {
                    private int number;
                    private int amount;
                }

                OrdersLoad[] orderSchedule;

                public class OrdersLoad {
                    private String bookTitle;
                    private int tick;
                }
            }
        }
    }

    private static void initBooks(){
        ArrayList<BookInventoryInfo> booksToInit = new ArrayList<>();
        for (JsonAnalyzer.MockBook tmp : analyzer.booksToLoad) {
            booksToInit.add(new BookInventoryInfo(tmp.bookTitle, tmp.amount, tmp.price));
        }
        BookInventoryInfo[] booksToInitAsArray = new BookInventoryInfo[booksToInit.size()];
        for (int i = 0; i < booksToInitAsArray.length; i++) {
            booksToInitAsArray[i] = booksToInit.get(i);
        }
        inventory.load(booksToInitAsArray);
    }

    private static void initVehicles(){
        ArrayList<DeliveryVehicle> vehiclesToInit = new ArrayList<>();
        for (JsonAnalyzer.Vehicle tmp : analyzer.vehiclesToLoad[0].vehicles) {
            vehiclesToInit.add(new DeliveryVehicle(tmp.license, tmp.speed));
        }
        DeliveryVehicle[] vehiclesToInitAsArray = new DeliveryVehicle[vehiclesToInit.size()];
        for (int i = 0; i < vehiclesToInitAsArray.length; i++) {
            vehiclesToInitAsArray[i] = vehiclesToInit.get(i);
        }
        resourcesHolder.load(vehiclesToInitAsArray);
    }
    private static void start() {
        int size = analyzer.servicesToLoad.sellingServiceToLoad + analyzer.servicesToLoad.resourcesServiceToLoad
                +analyzer.servicesToLoad.logisticsServiceToLoad + analyzer.servicesToLoad.inventoryServiceToLoad
                +analyzer.servicesToLoad.customers.length;
        latch = new CountDownLatch(size);
        initBooks();
        initVehicles();
        ArrayList<MicroService> servicesToInit = new ArrayList<>();
        TimeService timeService = new TimeService(analyzer.servicesToLoad.time.speed, analyzer.servicesToLoad.time.duration);
        servicesToInit.add(timeService);
        for (int i = 0; i < analyzer.servicesToLoad.sellingServiceToLoad; i++) {
            servicesToInit.add(new SellingService());
        }
        for (int i = 0; i < analyzer.servicesToLoad.inventoryServiceToLoad; i++) {
            servicesToInit.add(new InventoryService());
        }
        for (int i = 0; i < analyzer.servicesToLoad.logisticsServiceToLoad; i++) {
            servicesToInit.add(new LogisticsService());
        }
        for (int i = 0; i < analyzer.servicesToLoad.resourcesServiceToLoad; i++) {
            servicesToInit.add(new ResourceService());
        }

        for (JsonAnalyzer.ServicesInit.CustomerToLoad tmp : analyzer.servicesToLoad.customers) {
            Customer customerToAdd = new Customer(tmp.id, tmp.name, tmp.address, tmp.distance, new LinkedList<OrderReceipt>(),
                    tmp.creditCardToLoad.number, tmp.creditCardToLoad.amount);
            customersToPrint.put(customerToAdd.getId(), customerToAdd);
            LinkedList<BookOrderInfo> ordersToLoad = new LinkedList<>();
            JsonAnalyzer.ServicesInit.CustomerToLoad.OrdersLoad[] customerOrders = tmp.orderSchedule;
            for (int j = 0; j < customerOrders.length; j++) {
                ordersToLoad.add(new BookOrderInfo(customerOrders[j].bookTitle, customerOrders[j].tick));
            }
            servicesToInit.add(new APIService(customerToAdd, ordersToLoad));
        }
        for(int i=0; i<servicesToInit.size(); i++){
            Thread newThread = new Thread(servicesToInit.get(i));
            threads.add(newThread);
        }
    }

    public static void print(String filename, Object toPrint) {
        try (FileOutputStream fout = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(toPrint);
        } catch (Exception e) {}
    }

    private static void generateOutput(String[] args){
        print(args[1],customersToPrint);
        inventory.printInventoryToFile(args[2]);
        register.printOrderReceipts(args[3]);
        print(args[4],register);
    }

    public static void main(String[] args) {
        try {
            byte[] inputAsBytes = Files.readAllBytes(Paths.get(args[0]));
            String inputAsString = new String(inputAsBytes);
            Gson g = new Gson();
            analyzer = g.fromJson(inputAsString, JsonAnalyzer.class);
            start();
            for(int i=0; i<threads.size(); i++) {
               threads.get(i).start();
            }
            for(int i=0; i<threads.size(); i++){
                    threads.get(i).join();
            }
            threads.clear();
            generateOutput(args);
        }
        catch (Exception e){}
    }
}
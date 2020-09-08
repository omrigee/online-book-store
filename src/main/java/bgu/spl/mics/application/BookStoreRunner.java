package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static AtomicInteger initializeCount = new AtomicInteger(0);


    public static void main(String[] args) {

        String inputFilePath = args[0];

        Gson Gson = new Gson();
        HashMap<Integer, Customer> CustomerPrintMap = null;
        try {
            JsonReader reader = new JsonReader(new FileReader(inputFilePath));
            JsonObject object = Gson.fromJson(reader, JsonObject.class);


            //JSON: Initializing the Inventory:
            Inventory inventory = Inventory.getInstance();
            BookInventoryInfo[] books = initializingInventory(object);
            inventory.load(books);


            //JSON: Initializing the ResourcesHolder:
            ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
            DeliveryVehicle[] vehicles = initializingVehicles(object);
            resourcesHolder.load(vehicles);

            //JSON: Initializing the Services:
            LinkedList<MicroService> servicesThreads = initializingServices(object);
            LinkedList<Thread> Threads = new LinkedList<>();
            LinkedList<MicroService> SERVICES= new LinkedList<>();

            //JSON: Initializing the Customers and their APIService:
            CustomerPrintMap = initializingAPIandCustomerMap(object, servicesThreads);


            int queue_size = servicesThreads.size();
            while (initializeCount.get() < queue_size) {
                MicroService serviceToActivate = servicesThreads.poll();
                Thread thread = new Thread(serviceToActivate);
                Threads.addLast(thread);
                SERVICES.addLast(serviceToActivate);
                thread.start();
            }



            //JSON: Time Service:
            initializingTimeService(object);



            for (Thread thread : Threads) {
                thread.join();
            }
        } catch (FileNotFoundException e) {
        } catch (InterruptedException e) { }


        //Printing Customers HashMap:
        printCustomerMapToFile(CustomerPrintMap, args[1]);

        //Printing Books HashMap:
        Inventory.getInstance().printInventoryToFile(args[2]);

        //Printing Order Receipts:
        MoneyRegister.getInstance().printOrderReceipts(args[3]);

        //Printing MoneyRegister:
        printMoneyRegisterToFile(args[4]);



    } // end of main.





// **************************************************************************************************************************


    //JSON: Initializing the Inventory:
    private static BookInventoryInfo[] initializingInventory(JsonObject object) {
        JsonArray inventoryFromJson = object.getAsJsonArray("initialInventory");
        BookInventoryInfo[] books = new BookInventoryInfo[inventoryFromJson.size()];
        for (int i = 0; i < inventoryFromJson.size(); i++) {
            JsonElement element = inventoryFromJson.get(i);
            String bookTitle = element.getAsJsonObject().get("bookTitle").getAsString();
            int amountInInventory = element.getAsJsonObject().get("amount").getAsInt();
            int price = element.getAsJsonObject().get("price").getAsInt();
            BookInventoryInfo bookToAdd = new BookInventoryInfo(bookTitle, amountInInventory, price);
            books[i] = bookToAdd;
        }
        return books;
    }

    //JSON: Initializing the ResourcesHolder method:
    private static DeliveryVehicle[] initializingVehicles(JsonObject object) {
        JsonArray ResourceFromJson = object.getAsJsonArray("initialResources");
        JsonObject Object = (JsonObject) ResourceFromJson.get(0);
        JsonArray VehiclesFromJson = Object.get("vehicles").getAsJsonArray();
        DeliveryVehicle[] vehicles = new DeliveryVehicle[VehiclesFromJson.size()];
        for (int i = 0; i < VehiclesFromJson.size(); i++) {
            JsonElement element = VehiclesFromJson.get(i);
            int vehicleLicense = element.getAsJsonObject().get("license").getAsInt();
            int speed = element.getAsJsonObject().get("speed").getAsInt();
            DeliveryVehicle vehicle = new DeliveryVehicle(vehicleLicense, speed);
            vehicles[i] = vehicle;
        }
        return vehicles;
    }

    //JSON: Initializing services method:
    private static LinkedList<MicroService> initializingServices(JsonObject object) {
        LinkedList<MicroService> servicesThreads = new LinkedList<MicroService>();
        JsonObject ServicesFromJson = object.get("services").getAsJsonObject();


        //JSON: Selling Service:
        int SellingsToInitialize = ServicesFromJson.get("selling").getAsInt();
        for (int i = 0; i < SellingsToInitialize; i++) {
            SellingService sellingService = new SellingService(i);
            servicesThreads.add(sellingService);
        }

        //JSON: Inventory Service:
        int InventoryServiceToInitialize = ServicesFromJson.get("inventoryService").getAsInt();
        for (int i = 0; i < InventoryServiceToInitialize; i++) {
            InventoryService inventoryService = new InventoryService(i);
            servicesThreads.add(inventoryService);
        }

        //JSON: Logistics Service:
        int LogisticsServiceToInitialize = ServicesFromJson.get("logistics").getAsInt();
        for (int i = 0; i < LogisticsServiceToInitialize; i++) {
            LogisticsService logisticsService = new LogisticsService(i);
            servicesThreads.add(logisticsService);
        }

        //JSON: Resources Service:
        int ResourcesServiceToInitialize = ServicesFromJson.get("resourcesService").getAsInt();
        for (int i = 0; i < ResourcesServiceToInitialize; i++) {
            ResourceService resourceService = new ResourceService(i);
            servicesThreads.add(resourceService);
        }
        return servicesThreads;
    }

    //JSON: Initializing the Customers and their APIService method:
    private static HashMap<Integer, Customer> initializingAPIandCustomerMap(JsonObject object, LinkedList<MicroService> servicesThreads) {
        HashMap<Integer, Customer> CustomerPrintMap = new HashMap<>(); //map that stores the customers and should be serialized
        LinkedList<MicroService> servicesThreadsWithAPI = servicesThreads;
        JsonObject ServicesFromJson = object.get("services").getAsJsonObject();

        JsonArray CustomersFromJson = ServicesFromJson.get("customers").getAsJsonArray();
        //  System.out.println("CustomersFromJson.size() "+ CustomersFromJson.size());
        for (int i = 0; i < CustomersFromJson.size(); i++) {
            JsonElement element = CustomersFromJson.get(i);
            int customerID = element.getAsJsonObject().get("id").getAsInt();
            String customerName = element.getAsJsonObject().get("name").getAsString();
            String customerAddress = element.getAsJsonObject().get("address").getAsString();
            int customerDistance = element.getAsJsonObject().get("distance").getAsInt();
            JsonObject creditCard = element.getAsJsonObject().get("creditCard").getAsJsonObject();
            int customerCreditCard = creditCard.get("number").getAsInt();
            AtomicInteger customerAmountInCard = new AtomicInteger();
            customerAmountInCard.set(creditCard.get("amount").getAsInt());
            ConcurrentHashMap<Integer,CopyOnWriteArrayList<String>> customerOrderSchedueleMap = new ConcurrentHashMap<>();
            JsonArray booksOfCustomer = element.getAsJsonObject().get("orderSchedule").getAsJsonArray();
            for (int j = 0; j < booksOfCustomer.size(); j++) {
                int tick = booksOfCustomer.get(j).getAsJsonObject().get("tick").getAsInt();
                if (!customerOrderSchedueleMap.containsKey(tick)) {
                   CopyOnWriteArrayList<String> booksToAdd = new CopyOnWriteArrayList<>(); // Order ID is value, receipt itself is the key.
                    String bookTitle = booksOfCustomer.get(j).getAsJsonObject().get("bookTitle").getAsString();
                    booksToAdd.add(bookTitle);

                    customerOrderSchedueleMap.put(tick, booksToAdd);
                } else {
                    CopyOnWriteArrayList<String> bookOfCurrentTick = customerOrderSchedueleMap.get(tick);
                    String bookTitle = booksOfCustomer.get(j).getAsJsonObject().get("bookTitle").getAsString();
                    bookOfCurrentTick.add(bookTitle);
                    customerOrderSchedueleMap.put(tick, bookOfCurrentTick);
                }
            }

            Customer customer = new Customer(customerID, customerName, customerAddress, customerDistance, customerCreditCard, customerAmountInCard, customerOrderSchedueleMap);
            CustomerPrintMap.put(customerID, customer); //initialize the customer map that should be printed.
            APIService apiService = new APIService(customer,i);
            servicesThreadsWithAPI.add(apiService);
        }

        return CustomerPrintMap;
            }

    private static void initializingTimeService(JsonObject object) {
        JsonObject ServicesFromJson = object.get("services").getAsJsonObject();
        JsonObject timeServiceJson = ServicesFromJson.get("time").getAsJsonObject();
        int speedOfTimeService = timeServiceJson.get("speed").getAsInt();
        int durationofTimeService = timeServiceJson.get("duration").getAsInt();
        TimeService timeService = new TimeService(speedOfTimeService, durationofTimeService);
        Thread timeServiceThread = new Thread(timeService);
        timeServiceThread.start();
    }

    private static void printMoneyRegisterToFile(String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(MoneyRegister.getInstance());
            out.close();
            fileOut.close();
        } catch (IOException i) {

        }
    }

    private static void printCustomerMapToFile(HashMap customersMap, String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(customersMap);
            out.close();
            fileOut.close();
        } catch (IOException i) {

        }
    }

    //FOR TESTING
    public static Object deSerialization(String file) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return object;

    }
} //end of BookStoreRunner



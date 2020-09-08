package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {


  private static class InventoryHolder{
  	private static Inventory instance = new Inventory();
  }

  private ConcurrentHashMap<String,BookInventoryInfo> InventoryMap; // HashMap of Inventory Books. key is book title, value is the book itself.

	// Private Constructor
  private Inventory(){
  	InventoryMap = new ConcurrentHashMap<>();
  }

	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {return InventoryHolder.instance;}

	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) { // Loads the book once in initialization

		InventoryMap.clear();

		for (BookInventoryInfo book : inventory)
			InventoryMap.put(book.getBookTitle(),book);
	}
	
	/**
	 * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) { // Omri: should be synchronized although InventoryMap is a concurrentHashmap?
		BookInventoryInfo RequiredBook = InventoryMap.get(book);

		//TODO: CHECK! :) (: :)
		synchronized (RequiredBook) {
			if (RequiredBook.getAmountInInventory() <= 0) // book is unavailable in stock.
				return OrderResult.NOT_IN_STOCK;

			else RequiredBook.setAmountInInventory(RequiredBook.getAmountInInventory() - 1);
			return OrderResult.SUCCESSFULLY_TAKEN;
		}
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		BookInventoryInfo RequiredBook = InventoryMap.get(book);

		if(RequiredBook == null ||  RequiredBook.getAmountInInventory()<=0) // if book is unavailable returns -1
			return -1;

		return RequiredBook.getPrice(); // book is available. returns books price

	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename) {
		HashMap<String, Integer> toPrint = new HashMap<>();

		synchronized (this) { // while copying the relevant fields from InventoryMap, we lock the entire InventoryMap.
			for (Map.Entry<String, BookInventoryInfo> book : InventoryMap.entrySet()) {
				toPrint.put(book.getKey(), book.getValue().getAmountInInventory());
			}
		}

		synchronized (toPrint) { // while printing we lock the copied Map, the original InventoryMap is available.
			try {
				FileOutputStream fileOut = new FileOutputStream(filename);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(toPrint);
				out.close();
				fileOut.close();
			} catch (IOException i) {
				// i.printStackTrace(); - check if necessary
			}
		}
	}


	public ConcurrentHashMap<String, BookInventoryInfo> getInventoryMap() {
		return InventoryMap;
	}
} // end of Inventory class


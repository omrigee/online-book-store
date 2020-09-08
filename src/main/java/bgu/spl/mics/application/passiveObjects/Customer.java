package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class
Customer implements Serializable {


	//FIELDS:
	private int id;
	private String name;
	private String address;
	private int distance;
	private List<OrderReceipt> receiptsList;
	private int creditCard;
	private AtomicInteger AvailableCreditAmount;
	private ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>> orderScheduleMap; // tick need to be ordered is key, value is bookTitle by string.


	// Constructor
	public Customer(int id, String name, String address, int distance, int creditCard, AtomicInteger availableCreditAmount,ConcurrentHashMap<Integer,CopyOnWriteArrayList<String>> orderScheduleMap) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.distance = distance;
		this.creditCard = creditCard;
		this.AvailableCreditAmount = availableCreditAmount;
		this.orderScheduleMap = orderScheduleMap;
		this.receiptsList = new LinkedList<>();
	}


	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return this.name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return this.id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return this.address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return this.distance;
	}

	public ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>> getOrderScheduleMap() {
		return orderScheduleMap;
	}

	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return this.receiptsList;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return this.AvailableCreditAmount.get();
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return this.creditCard;
	}

	public void setAvailableCreditAmount(int availableCreditAmount) {
		AvailableCreditAmount.set(availableCreditAmount);
	}

	@Override
	public String toString() {
		return "Customer{" +
				"name='" + name + '\'' +
				", AvailableCreditAmount=" + AvailableCreditAmount +
				'}';
	}
}

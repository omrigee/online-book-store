package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {

	// Fields
	private int orderID;
	private String seller;
	private int customerID;
	private String bookTitle;
	private int price;
	private int issuedTick;
	private int orderTick;
	private int proccessTick;


	// Constructor
	public OrderReceipt(int orderID, String seller, int customerID, String bookTitle, int price, int issuedTick, int orderTick, int proccessTick) {
		this.orderID = orderID;
		this.seller = seller;
		this.customerID = customerID;
		this.bookTitle = bookTitle;
		this.price = price;
		this.issuedTick = issuedTick;
		this.orderTick = orderTick;
		this.proccessTick = proccessTick;
	}

	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {
		return orderID;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return customerID;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return bookTitle;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {

		return issuedTick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase request.
     */
	public int getOrderTick() {
		return orderTick;
	}


	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {
		return proccessTick;
	}

	@Override
	public String toString() {
		return "Order ID: " + getOrderId() + ", Seller: " + getSeller() +
				", Customer ID: " + getCustomerId() + ", Book Title: " + getBookTitle() + ", Price: " + getPrice() + ", Issued Tick: " + getIssuedTick()+
				", Order Tick: " + getOrderTick() + ", Proccess Tick: " +getProcessTick();
	}





} //end of OrderReceipt

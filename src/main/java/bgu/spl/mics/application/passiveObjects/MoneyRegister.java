package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {


	private static class MoneyRegisterHolder{
		private static MoneyRegister instance = new MoneyRegister();
	}

	private CopyOnWriteArrayList<OrderReceipt> orderReceiptList;

	private  MoneyRegister() {

		orderReceiptList = new CopyOnWriteArrayList<>(); // Order ID is value, receipt itself is the key.
	}


	/**
	 * Retrieves the single instance of this class.
	 */
	public static MoneyRegister getInstance() {
		return MoneyRegisterHolder.instance;
	}

	/**
	 * Saves an order receipt in the money register.
	 * <p>
	 * @param r		The receipt to save in the money register.
	 */
	public void file (OrderReceipt r) {
		//maybe we need to add "if (!orderReceiptList.contains(r))"
		orderReceiptList.add(r);
	}


	/**
	 * Retrieves the current total earnings of the store.
	 */
	public int getTotalEarnings() {
		int totalEarnings = 0;
		for (OrderReceipt r : orderReceiptList){
			totalEarnings =  totalEarnings + r.getPrice();
		}
		return totalEarnings;
	}

	/**
	 * Charges the credit card of the customer a certain amount of money.
	 * <p>
	 * @param amount 	amount to charge
	 */

	public void chargeCreditCard(Customer c, int amount) { // In case 2 threads will try to charge the same costumer simultaneity
		int newAmount =  c.getAvailableCreditAmount() - amount; // check if there is enough money in the account for the specific book.
		if( newAmount >= 0){
			c.setAvailableCreditAmount(newAmount);
		}
	}

	/**
	 * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts
	 * currently in the MoneyRegister
	 * This method is called by the main method in order to generate the output..
	 */
	public void printOrderReceipts(String filename) {

		List<OrderReceipt> toPrint = new LinkedList<>();


			for (OrderReceipt r : orderReceiptList){
				toPrint.add(r);
			}


			try {
				FileOutputStream file = new FileOutputStream(filename);
				ObjectOutputStream out = new ObjectOutputStream(file);
				out.writeObject(toPrint);
				out.close();
				file.close();
			} catch (IOException i) {
				// i.printStackTrace(); - check if necessary
			}

	}

	@Override
	public String toString() {
		String out = "MoneyRegister{\n" + "orderReceiptList= " + orderReceiptList.size() + "\n";
		for (OrderReceipt receipt : orderReceiptList)
			out += receipt.toString() + "\n";
		return out	+ '}';
	}
}




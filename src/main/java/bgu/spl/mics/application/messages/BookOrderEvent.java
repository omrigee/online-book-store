package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/** An event that is sent when a client of the store wishes to buy a book.
 * Its expected response type is an OrderReceipt.
 * IN the case that the order was not completed successfully, null should be returened as the event result.
 */

public class BookOrderEvent implements Event {

    //FIELDS:
    private Customer customer;
    private String bookTitle;
    private int tickIssued;


    //CONSTRUCTOR:
    public BookOrderEvent(Customer customer,String bookTitle,int tickIssued) {
        this.customer = customer;
        this.bookTitle = bookTitle;
        this.tickIssued = tickIssued;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getTickIssued() {
        return tickIssued;
    }
}


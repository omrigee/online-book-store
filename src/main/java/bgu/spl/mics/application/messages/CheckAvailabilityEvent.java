package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailabilityEvent implements Event {

    //FIELDS:
    private String bookTitle;
    private int creditAmount;

    //CONSTRUCTOR:
    public CheckAvailabilityEvent(String bookTitle ) {
        this.bookTitle = bookTitle;
    }

    public String getBookTitle() {
        return bookTitle;
    }



}

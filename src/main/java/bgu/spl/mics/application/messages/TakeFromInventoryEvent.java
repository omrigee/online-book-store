package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class TakeFromInventoryEvent implements Event {

    //FIELDS:
    private String bookTitle;

    //CONSTRUCTOR:

    public TakeFromInventoryEvent(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookTitle() {
        return bookTitle;
    }
}

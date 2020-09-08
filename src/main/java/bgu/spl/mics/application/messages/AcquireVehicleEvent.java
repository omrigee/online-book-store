package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class AcquireVehicleEvent implements Event {

    //FIELDS:
    private Customer customer;

    //CONSTRUCTOR:
    public AcquireVehicleEvent(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}

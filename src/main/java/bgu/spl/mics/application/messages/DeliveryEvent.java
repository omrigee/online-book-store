package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/** An event that is sent when the BookOrderEvent is successfully completed and a delivery is required.
 *
 */

public class DeliveryEvent implements Event {

    //FIELDS:
    Customer customer;


    //CONSTRUCTOR:
    public DeliveryEvent(Customer customer) {
        this.customer = customer;
    }


    public Customer getCustomer() {
        return customer;
    }
}

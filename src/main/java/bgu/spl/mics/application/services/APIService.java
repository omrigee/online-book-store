package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private Customer currentCustomer;
	private int CurrentTick;// needed for the selling receipt to get the order Tick


	public APIService(Customer c,int serviceID) {
		super("APIService "+serviceID);
		currentCustomer = c;
		CurrentTick = 0;
	}

	public int getCurrentTick() {
		return CurrentTick;
	}

	@Override
	protected void initialize() {



		subscribeBroadcast(TickBroadcast.class, cb ->
		{


			CurrentTick = cb.getTick();
			if(currentCustomer.getOrderScheduleMap().get(cb.getTick()) != null) { //if the current tick has orders, it will get in and perform those orders
				CopyOnWriteArrayList<String> BookTitlesToOrder = currentCustomer.getOrderScheduleMap().get(cb.getTick());// all the orders in the tick
				ConcurrentLinkedQueue<Future<OrderReceipt>> ordersFuturesQueue = new ConcurrentLinkedQueue<>(); //creates the queue for the future.

				//send every title book as an order.

				for (int i = 0; i < BookTitlesToOrder.size(); i++) {
					String currentBookTitle = BookTitlesToOrder.get(i);
					Future<OrderReceipt> future = sendEvent(new BookOrderEvent(currentCustomer, currentBookTitle, getCurrentTick()));
					if (future != null) {
						ordersFuturesQueue.add(future);
					}
				}

				while(!ordersFuturesQueue.isEmpty()){
					Future<OrderReceipt> futureToGet = ordersFuturesQueue.poll();
					if (futureToGet != null) {
						OrderReceipt receipt = futureToGet.get();
						if (receipt.getOrderId() == -1) { // in case order didnt happend because customer has no money or book is unavailable.
						}
						else if (receipt.getOrderId() == 0) {
							currentCustomer.getCustomerReceiptList().add(receipt);
							sendEvent(new DeliveryEvent(currentCustomer));
						}
					}
				}
			}
		});


		//Terminate Broadcast Callback
		subscribeBroadcast(TerminateBroadcast.class, cb -> {
			terminate();
		});
	}

}

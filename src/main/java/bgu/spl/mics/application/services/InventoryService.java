package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	//FIELDS:
	private Inventory inventory;


	public InventoryService(int serviceID) {
		super("InventoryService "+serviceID);
		this.inventory = Inventory.getInstance();
	}

	@Override
	protected void initialize() {

		subscribeEvent(CheckAvailabilityEvent.class, ev -> {
			Integer bookPrice = inventory.checkAvailabiltyAndGetPrice(ev.getBookTitle());
			if (bookPrice == -2) {
				complete(ev, null);
			}
			complete(ev,bookPrice); //resolve the result to 'bookExistFuture' from SellingService.
		});

		subscribeEvent(TakeFromInventoryEvent.class, ev ->{
			synchronized (inventory.getInventoryMap().get(ev.getBookTitle())) {
				OrderResult result = inventory.take(ev.getBookTitle());
				complete(ev, result); //resolve the result to 'bookTaken' from SellingService.
			}
		});

		//Terminate Broadcast Callback
		subscribeBroadcast(TerminateBroadcast.class, cb->{
			terminate();
		});


	}

}

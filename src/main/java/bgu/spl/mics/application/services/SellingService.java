package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	//FIELDS:
	private MoneyRegister moneyRegister;
	private int currentTick;


	public SellingService(int serviceID) {
		super("SellingService "+serviceID);

		moneyRegister = MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {

		//subscribing for TickBroadcast:
		subscribeBroadcast(TickBroadcast.class,cb-> {
			currentTick = cb.getTick();
		} );


		//Callback for 'BookOrderEvent' that sends CheckAvailabilityEvent to inventory service to check if book exist and returns his price
		subscribeEvent(BookOrderEvent.class,event->{


				int processTick = currentTick; // for the receipt

				Future<Integer> bookExistFuture = sendEvent(new CheckAvailabilityEvent(event.getBookTitle()));

				//if Book is available
				if (bookExistFuture != null && bookExistFuture.get() != -1) { //price of the book
					int bookPrice = bookExistFuture.get();
					int creditBeforeCharge;
					int creditAfterCharge;
					synchronized (event.getCustomer()) {
						creditBeforeCharge = event.getCustomer().getAvailableCreditAmount();
						moneyRegister.chargeCreditCard(event.getCustomer(), bookPrice);
						creditAfterCharge = event.getCustomer().getAvailableCreditAmount();
					}
					if (creditBeforeCharge == creditAfterCharge) {// customer was not charged - doesnt have enough money.
						OrderReceipt orderNotHappend = new OrderReceipt(-1, this.getName() + " " + this.getId(), event.getCustomer().getId(), event.getBookTitle(), bookPrice, currentTick, event.getTickIssued(), processTick);
						complete(event, orderNotHappend); // No enough money, resolves the 'BookOrderEvent' with null back to 'APIService' no receipt will be done.
					}
					//customer has been charged, needs to take the book
					else {
						Future<OrderResult> bookFuture = sendEvent(new TakeFromInventoryEvent(event.getBookTitle()));
						if (bookFuture == null){
							complete(event, new OrderReceipt(-1, this.getName() + " " + this.getId(), event.getCustomer().getId(), event.getBookTitle(), bookPrice, currentTick, event.getTickIssued(), processTick));
							return;
						}

						OrderResult bookTaken = bookFuture.get();
						if (bookTaken != null) {
							// customer has been charged, but no available book
							if (bookTaken == OrderResult.NOT_IN_STOCK) {
								event.getCustomer().setAvailableCreditAmount(creditBeforeCharge); //return the money.
								OrderReceipt orderCanceled = new OrderReceipt(-1, this.getName() + " " + this.getId(), event.getCustomer().getId(), event.getBookTitle(), bookPrice, currentTick, event.getTickIssued(), processTick);
								complete(event, orderCanceled); //Customer didn't received the book, so there is no receipt.
							} else { //creating the receipt
								event.getCustomer().setAvailableCreditAmount(creditAfterCharge); //return the money. " book order was  taken - OrderResult = SUCCESFULY TAKEN");
								int customerID = event.getCustomer().getId();
								OrderReceipt receipt = new OrderReceipt(0, this.getName() + " " + this.getId(), customerID, event.getBookTitle(), bookPrice, currentTick, event.getTickIssued(), processTick);
								moneyRegister.file(receipt);
								complete(event, receipt); // resolves the 'BookOrderEvent' back to APIService.

							}
						}
						else {
							OrderReceipt orderCanceled = new OrderReceipt(-1, this.getName() + " " + this.getId(), event.getCustomer().getId(), event.getBookTitle(), bookPrice, currentTick, event.getTickIssued(), processTick);
							complete(event, orderCanceled); //Customer didn't received the book, so there is no receipt.

						}
					}

				}
				// in case book is not in stock (price = -1)
				else {
					OrderReceipt orderCanceled = new OrderReceipt(-1, this.getName() + " " + this.getId(), event.getCustomer().getId(), event.getBookTitle(), 0, currentTick, event.getTickIssued(), processTick);
					complete(event, orderCanceled); //Customer didn't received the book, so there is no receipt.
				}
		});


//Terminate Broadcast Callback
		subscribeBroadcast(TerminateBroadcast.class,cb->{
			terminate();
		});

	}

}

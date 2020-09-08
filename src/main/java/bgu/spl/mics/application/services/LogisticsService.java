package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {


	public LogisticsService(int serviceID) {
		super("LogisticService "+serviceID);
	}

	@Override
	protected void initialize() {

		subscribeEvent(DeliveryEvent.class,event-> {

			Future<Future<DeliveryVehicle>> vehicleFuture = sendEvent(new AcquireVehicleEvent(event.getCustomer()));
			if (vehicleFuture != null && vehicleFuture.get()!=null){
				DeliveryVehicle vehicle = vehicleFuture.get().get();// got a vehicle
				vehicle.deliver(event.getCustomer().getAddress(),event.getCustomer().getDistance()); // went for drive
				sendEvent( new ReleaseVehicleEvent(vehicle)); //releases when finish the drive
			}

			});

		//Terminate Broadcast Callback
		subscribeBroadcast(TerminateBroadcast.class, cb->{
			terminate();

		});


	}

}
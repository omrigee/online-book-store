package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private ResourcesHolder resourcesHolder;
	private BlockingQueue<Future<DeliveryVehicle>> futureVehiclesQueueInService;

	public ResourceService(int serviceID) {
		super("ResourceService "+serviceID);
		this.resourcesHolder = ResourcesHolder.getInstance();
		futureVehiclesQueueInService = new LinkedBlockingQueue<>();
	}

	@Override
	protected void initialize() {

		subscribeEvent(AcquireVehicleEvent.class, event ->{

			Future<DeliveryVehicle> futureVehicle = resourcesHolder.acquireVehicle();
			if( ! futureVehicle.isDone()){
				futureVehiclesQueueInService.add(futureVehicle); // add to the queue the unresolved future, in order to resolve to null if terminated

			}

		});

		subscribeEvent(ReleaseVehicleEvent.class, ev -> {
			resourcesHolder.releaseVehicle(ev.getVehicle());
		});

		//Terminate Broadcast Callback
		subscribeBroadcast(TerminateBroadcast.class, cb->{
			for (Future<DeliveryVehicle> future : futureVehiclesQueueInService){
				future.resolve(null);
			}
			terminate();
		});



	}

}
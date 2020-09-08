package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private BlockingQueue<DeliveryVehicle> VehiclesQueue;
	private BlockingQueue<Future<DeliveryVehicle>> futureVehicleQueue;


	private static class ResourcesHolderHolder{
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	private ResourcesHolder() {
		VehiclesQueue = new LinkedBlockingDeque<>();
		futureVehicleQueue = new LinkedBlockingQueue<>();

	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static ResourcesHolder getInstance() {
		return ResourcesHolderHolder.instance;
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */



	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> acquiredVehicleFuture = new Future<>();
		DeliveryVehicle acquiredVehicle;
		acquiredVehicle = VehiclesQueue.poll();
		if(acquiredVehicle == null)
			futureVehicleQueue.add(acquiredVehicleFuture);
		else
			acquiredVehicleFuture.resolve(acquiredVehicle);

		return  acquiredVehicleFuture;

	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 *
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
	//	System.out.println( "vehicle: #"+vehicle.getLicense() + ", back to available queue");

		Future<DeliveryVehicle> vehicleFuture =futureVehicleQueue.poll();
		if(vehicleFuture != null)// someone is waiting for a available vehicle
			vehicleFuture.resolve(vehicle);
		else VehiclesQueue.add(vehicle); // no one is waiting for a vehicle , so we return the vehicle to the  available vehicle queue
	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) { // Happends once, in the system initialization

		VehiclesQueue.clear();

		for (int i=0;i<vehicles.length;i++) // Adds vehicles to Vehicles Queue
			VehiclesQueue.add(vehicles[i]);




	}



	public BlockingQueue<Future<DeliveryVehicle>> getFutureVehicleQueue() {
		return futureVehicleQueue;
	}



}


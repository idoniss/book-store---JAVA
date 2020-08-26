package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import java.util.ArrayList;
import java.util.concurrent.*;

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
	private ArrayList<DeliveryVehicle> deliveryVehicles;
	private LinkedBlockingQueue<DeliveryVehicle> freeVehicles = new LinkedBlockingQueue<>();
	private Semaphore vehiclesSemaphore;

	private static class ResourcesHolderHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}
	private ResourcesHolder()
	{
		deliveryVehicles=new ArrayList<DeliveryVehicle>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static ResourcesHolder getInstance() {

		return ResourcesHolder.ResourcesHolderHolder.instance;
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future f = new Future();
		try{
			vehiclesSemaphore.acquire();
			DeliveryVehicle vehicle = freeVehicles.take();
			f.resolve(vehicle);
			return f;
		}
		catch (InterruptedException e){}
		return f;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if (vehicle != null) {
			freeVehicles.add(vehicle);
			vehiclesSemaphore.release();
		}
	}


	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		for (DeliveryVehicle tmp : vehicles) {
			deliveryVehicles.add(tmp);
			freeVehicles.add(tmp);
		}
		setVehiclesSemaphore(vehicles.length);
	}

	private void setVehiclesSemaphore(int numOfVehicles){
		this.vehiclesSemaphore = new Semaphore(numOfVehicles);
	}

}
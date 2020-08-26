package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.Future;
import java.util.concurrent.CountDownLatch;


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
	private ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
	static private int i = 1;
	private CountDownLatch latch = BookStoreRunner.latch;

	public ResourceService() {
		super("ResourceService" + i);
		i++;
	}

	@Override
	protected void initialize() {
		subscribeEvent(AcquireVehicle.class, (AcquireVehicle event) -> {
			complete(event, true);
			Future<DeliveryVehicle> vehicleFuture = resourcesHolder.acquireVehicle();
			DeliveryVehicle vehicle1 = vehicleFuture.get();
			ActualDelivery actualDelivery = new ActualDelivery(event.getCustomer(), vehicle1);
			Future<DeliveryVehicle> vehicleReturnFuture = sendEvent(actualDelivery);
			if(vehicleReturnFuture != null) {
				DeliveryVehicle vehicle2 = vehicleReturnFuture.get();
				if(vehicle2 != null)
					resourcesHolder.releaseVehicle(vehicle2);
				else
					resourcesHolder.releaseVehicle(vehicle1);
			}
			else {
				resourcesHolder.releaseVehicle(vehicle1);
			}
		});

		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminateTick) -> {
			terminate();
		});
		latch.countDown();
	}
}

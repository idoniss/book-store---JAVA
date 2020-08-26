package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;

import java.util.concurrent.CountDownLatch;

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
	private static int i = 1;
	private CountDownLatch latch = BookStoreRunner.latch;

	public LogisticsService() {
		super("LogisticsService" + i);
		i++;
	}

	@Override
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class, (DeliveryEvent event) -> {
			AcquireVehicle acquireEvent = new AcquireVehicle(event.getCustomer());
			acquireEvent.setFuture(sendEvent(acquireEvent));
		});
		subscribeEvent(ActualDelivery.class, (ActualDelivery event) -> {
			DeliveryVehicle vehicle = event.getVehicle();
				vehicle.deliver(event.getCustomer().getAddress(), event.getCustomer().getDistance());
				complete(event, vehicle);
		});
		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminateTick) -> {
			terminate();
		});
		latch.countDown();
	}
}

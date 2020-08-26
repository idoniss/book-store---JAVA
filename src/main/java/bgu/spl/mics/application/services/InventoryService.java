package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.CheckAvailability;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.Future;

import java.util.concurrent.CountDownLatch;

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
	private Inventory inventory = Inventory.getInstance();
	static private int i = 1;
	private CountDownLatch latch = BookStoreRunner.latch;

	public InventoryService() {
		super("InventoryService" + i);
		i++;
	}

	@Override
	protected void initialize() {
		subscribeEvent(CheckAvailability.class,(CheckAvailability check) -> {
			int price = inventory.checkAvailabiltyAndGetPrice(check.getBookTitle());
			complete(check, price);
		});

		subscribeEvent(TakeBookEvent.class,(TakeBookEvent event) -> {
			complete(event, inventory.take(event.getBookTitle()));
		});

		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminateTick) -> {
			terminate();
		});
		latch.countDown();
	}

}

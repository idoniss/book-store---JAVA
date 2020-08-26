package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import static java.lang.Math.toIntExact;

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
public class SellingService extends MicroService {
	static private int i = 1;
	private MoneyRegister register = MoneyRegister.getInstance();
	static private AtomicInteger orderId = new AtomicInteger(0);
	private CountDownLatch latch = BookStoreRunner.latch;

	public SellingService() {
		super("SellingService" + i);
		i++;
	}

	@Override
	protected void initialize() {
		subscribeEvent(BookOrderEvent.class, (BookOrderEvent event) -> {
			int processtick = toIntExact((System.currentTimeMillis()-event.getStart()) / event.getSpeed());
			int orderTick = toIntExact(event.getTick());
			CheckAvailability check = new CheckAvailability(event.getBookTitle());
			Future<Integer> checkFuture = sendEvent(check);
			int price;
			if(checkFuture != null) {
				price = checkFuture.get();
				if (price != -1 && event.getCustomer().getAvailableCreditAmount() >= price) {
					TakeBookEvent take = new TakeBookEvent(event.getBookTitle());
					take.setFuture(sendEvent(take));
					if(take.getFuture() != null) {
						if (take.getFuture().get().equals(OrderResult.SUCCESSFULLY_TAKEN)) {
							OrderReceipt receipt = new OrderReceipt(orderId.get()
									, getName(), event.getCustomer().getId(), event.getBookTitle(), price,
									toIntExact((System.currentTimeMillis() - event.getStart()) / event.getSpeed()),
									orderTick, processtick);
							boolean chargeCompleted = event.getCustomer().chargeCreditByAmount(price);
							if (chargeCompleted) {
								event.getCustomer().addReceipet(receipt);
								register.file(receipt);
								complete(event,receipt);
								DeliveryEvent deliveryEvent = new DeliveryEvent(event.getCustomer(), event.getBookTitle());
								deliveryEvent.setFuture(sendEvent(deliveryEvent));
							}
						}
					}
				}
				else
					complete(event,null);
			}
		});

		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminateTick) -> {
			terminate();
		});
		latch.countDown();
	}
}

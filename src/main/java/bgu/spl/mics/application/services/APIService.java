package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private static int i = 1;
	private List<BookOrderInfo> orderSchedule = new LinkedList<>();
	private Customer customer;
	private CountDownLatch latch = BookStoreRunner.latch;


	public APIService(Customer c, List<BookOrderInfo> ordersList) {
		super("APIService"+i);
		i++;
		for(BookOrderInfo info: ordersList){
			orderSchedule.add(info);
		}
		customer=c;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick)->{
			for(BookOrderInfo tmp: orderSchedule){
				if(tmp.getTick() == tick.getCurrentTick()) {
					BookOrderEvent orderEvent = new BookOrderEvent(tmp,
							customer,tick.getCurrentTick(),tick.getSpeed(),tick.getStart());
					orderEvent.setFuture(sendEvent(orderEvent));
				}
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminateTick) -> {
			terminate();
		});
		latch.countDown();
	}
}
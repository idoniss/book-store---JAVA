package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import com.google.gson.annotations.SerializedName;

import java.util.concurrent.CountDownLatch;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	@SerializedName("speed")
	private int speed;
	@SerializedName("duration")
	private int duration;
	long start;
	long end;
	private CountDownLatch latch;

	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed=speed;
		this.duration=duration;
		start = System.currentTimeMillis()-1000;
		latch = BookStoreRunner.latch;
	}


	@Override
	protected void initialize() {
		try{
			latch.await();
		}
		catch(InterruptedException e){}
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tick)-> {
			if(duration>0) {
				try {
					Thread.currentThread().sleep((long)(speed));
					end = System.currentTimeMillis();
					sendBroadcast(new TickBroadcast((end-start)/(long)speed, speed, start));
				} catch (InterruptedException e) {}
				duration = duration - 1;
			}
			if(duration<=0){
				sendBroadcast(new TerminateBroadcast());
				terminate();
			}
		});
		sendBroadcast(new TickBroadcast(1,speed,start));
	}

	public int getSpeed(){
		return speed;
	}
}

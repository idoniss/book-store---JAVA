package bgu.spl.mics;

import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements bgu.spl.mics.MessageBus {

	private AtomicInteger servicesCounter = new AtomicInteger(0);
	private ConcurrentHashMap<Integer, LinkedBlockingQueue<Message>> microServicesQueues = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Class, LinkedBlockingQueue<Integer>> registersMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Event, Future> futures = new ConcurrentHashMap<>();

	private static class MessageBusImplHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl(){}

	public static MessageBusImpl getInstance() {
		return MessageBusImplHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subscribe(type,m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subscribe(type,m);
	}

	public void subscribe(Class<? extends Message> type, MicroService m) {
		synchronized (registersMap) {
			if(!registersMap.containsKey(type)){
				registersMap.putIfAbsent(type, new LinkedBlockingQueue<>());
				registersMap.get(type).add(m.getID());
			}
			else{
				registersMap.get(type).add(m.getID());
			}
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
			if (futures.containsKey(e)) {
				futures.get(e).resolve(result);
			}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (registersMap) {
			if (registersMap.containsKey(b.getClass())) {
				if (registersMap.get(b.getClass()).size() > 0) {
					LinkedBlockingQueue<Integer> broadcastRegistered = registersMap.get(b.getClass());
					for (Integer id : broadcastRegistered) {
						try {
							microServicesQueues.get(id).put(b);
						} catch (InterruptedException e1) {
						}
					}
				}
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future f = null;
		synchronized (registersMap) {
			synchronized (microServicesQueues) {
				if (registersMap.containsKey(e.getClass())) {
					if (registersMap.get(e.getClass()).size() > 0) {
						int id = registersMap.get(e.getClass()).remove();
						registersMap.get(e.getClass()).add(id);
						f = new Future();
						futures.put(e, f);
						microServicesQueues.get(id).add(e);
						microServicesQueues.notifyAll();
						return f;
					}
				}
				return f;
			}
		}
	}

	@Override
	public void register(MicroService m) {
		synchronized (microServicesQueues) {
			int id;
			do {
				id = servicesCounter.get();
				m.setID(id);
			}
			while(!(servicesCounter.compareAndSet(id,id+1)));
			LinkedBlockingQueue<Message> tmp = new LinkedBlockingQueue<>();
			microServicesQueues.put(m.getID(), tmp);
			microServicesQueues.notifyAll();
		}
	}

	@Override
	public void unregister(MicroService m) {
		synchronized (registersMap) {
			synchronized (microServicesQueues) {
				if ((microServicesQueues.get(m.getID())).size() > 0) {
					Iterator eventsIterator = microServicesQueues.get(m.getID()).iterator();
					while (eventsIterator.hasNext()) {
						Message e = (Message) eventsIterator.next();
						if (e instanceof Event) {
							complete((Event) e, null);
						}
					}
				}
					microServicesQueues.remove(m.getID());
					for (LinkedBlockingQueue tmp : registersMap.values()) {
						tmp.remove(m.getID());
					}
				}
			}
		}


	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
			Message tmp = null;
			int microServiceID = m.getID();
			tmp = microServicesQueues.get(microServiceID).take();
			if (!m.getTypesToRegister().contains(tmp.getClass())) {
				throw new IllegalStateException();
			}
		return tmp;
	}

	public ConcurrentHashMap<Integer, LinkedBlockingQueue<Message>> getMicroServicesQueues() {
		return microServicesQueues;
	}

	public Future getFuture(Event event) {
		return futures.get(event);
	}
}
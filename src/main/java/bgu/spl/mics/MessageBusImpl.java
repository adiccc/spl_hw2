package bgu.spl.mics;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<Class<? extends Event>,BlockingQueue<MicroService>> eventsMapping;
	private ConcurrentHashMap<MicroService,BlockingQueue<Message>> microQueues;
	private ConcurrentHashMap<Class<? extends Broadcast>,BlockingQueue<MicroService>> broadcasts;
	private ConcurrentHashMap<Event,Future> eventsFuture;
	private static MessageBusImpl INSTANCE =null;

	private MessageBusImpl(){
		eventsMapping = new ConcurrentHashMap<>();
		microQueues = new ConcurrentHashMap<>();
		broadcasts= new ConcurrentHashMap<>();
		eventsFuture= new ConcurrentHashMap<>();
	}
	public static MessageBus getInstance(){
		if(INSTANCE==null){
			INSTANCE=new MessageBusImpl();
		}
		return INSTANCE;
	}
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (eventsMapping.containsKey(type)) {
			eventsMapping.get(type).add(m);
		}
		else{
			eventsMapping.put(type,new LinkedBlockingQueue<>());
			eventsMapping.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (broadcasts.containsKey(type)) {
			broadcasts.get(type).add(m);
		}
		else{
			broadcasts.put(type,new LinkedBlockingQueue<>());
			broadcasts.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if (eventsFuture.containsKey(e)){
			eventsFuture.get(e).resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> queue = broadcasts.get(b);
		if (queue != null) {
			for (MicroService t : queue) {
				try {
					if(microQueues.containsKey(t))
            			microQueues.get(t).put(b);
       		 	} catch (InterruptedException e) {}
			}
		}

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (!eventsMapping.containsKey(e)) {
			return null;
		}
		else{
			BlockingQueue<MicroService> t=eventsMapping.get(e);
			MicroService temp=t.poll();
			BlockingQueue<Message> q=microQueues.get(temp);
			q.add(e);
			q.notifyAll();
			Future<T> f=new Future<T>();
			eventsFuture.put(e,f);
			return f;
		}
	}

	@Override
	public void register(MicroService m) {
		if(!microQueues.containsKey(m)){
			microQueues.put(m,new LinkedBlockingQueue<>());
		}
	}

	@Override
	public void unregister(MicroService m) {
		if(microQueues.containsKey(m)) {
			synchronized (microQueues.get(m)) {
				microQueues.remove(m);
				microQueues.notifyAll();
			}
				for (BlockingQueue<MicroService> microServices : eventsMapping.values()) {
					for (MicroService b : microServices) {
						if (b.equals(m)) {
							microServices.remove(b);
						}
					}
				}
				for (BlockingQueue<MicroService> microServices : broadcasts.values()) {
					for (MicroService b : microServices) {
						if (b.equals(m)) {
							microServices.remove(b);
						}
					}
				}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
			BlockingQueue<Message> t=microQueues.get(m);
			if(t!=null)
				return t.take();
			return null;
		}

	//for test use
	public  boolean isRegisterToBrodcast(MicroService m, Class<? extends Broadcast> b){
		if (broadcasts.containsKey(b)){
			if(broadcasts.get(b).contains(m))
				return true;
		}
		return false;
	}
}


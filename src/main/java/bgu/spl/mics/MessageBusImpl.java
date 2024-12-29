package bgu.spl.mics;

import com.google.gson.stream.JsonReader;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<Class<? extends Event>,ConcurrentLinkedQueue<MicroService>> eventsMapping;
	private ConcurrentHashMap<MicroService,ConcurrentLinkedQueue<Message>> microQueues;
	private ConcurrentHashMap<Class<? extends Broadcast>,ConcurrentLinkedQueue<MicroService>> broadcasts;//check if to convert  the queue to Concurrent link list
	private ConcurrentHashMap<Event,Future> eventsFuture;

	private static class MessageBusHolder{
		private static final MessageBusImpl INSTANCE = new MessageBusImpl();
	}

	private MessageBusImpl(){
		eventsMapping = new ConcurrentHashMap<>();
		microQueues = new ConcurrentHashMap<>();
		broadcasts= new ConcurrentHashMap<>();
		eventsFuture= new ConcurrentHashMap<>();
	}
	public static MessageBus getInstance(){
		return MessageBusHolder.INSTANCE;
	}
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventsMapping.computeIfAbsent(type, key -> new ConcurrentLinkedQueue<>()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcasts.computeIfAbsent(type, key -> new ConcurrentLinkedQueue<>()).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if (eventsFuture.containsKey(e)){
			eventsFuture.get(e).resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		ConcurrentLinkedQueue<MicroService> queue = broadcasts.get(b.getClass());
		if (queue != null) {
			for (MicroService t : queue) {
					synchronized (t){
						if(microQueues.containsKey(t))
            				microQueues.get(t).add(b);
						t.notifyAll();
					}
			}
		}

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (!eventsMapping.containsKey(e)) {
			return null;
		}
		else{
			ConcurrentLinkedQueue<MicroService> t=eventsMapping.get(e);
			boolean ex=false;
			while(!ex && !t.isEmpty()){
				MicroService temp=t.poll();
				synchronized (temp){
				if(microQueues.containsKey(temp)) {
					microQueues.get(temp).add(e);
					ex=true;
				}
				temp.notifyAll();
			}
			}
			Future<T> f=new Future<T>();
			eventsFuture.put(e,f);
			return f;
		}
	}

	@Override
	public void register(MicroService m) {
		microQueues.computeIfAbsent(m, key -> new ConcurrentLinkedQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
			synchronized (m) {
				if(microQueues.containsKey(m)) {
					microQueues.remove(m);
					for (ConcurrentLinkedQueue<MicroService> microServices : eventsMapping.values()) {
						for (MicroService b : microServices) {
							if (b.equals(m)) {
								microServices.remove(b);
							}
						}
					}
					for (ConcurrentLinkedQueue<MicroService> microServices : broadcasts.values()) {
						for (MicroService b : microServices) {
							if (b.equals(m)) {
								microServices.remove(b);
							}
						}
					}
			}
				m.notifyAll();
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
			ConcurrentLinkedQueue<Message> t=microQueues.get(m);
			if(t!=null)
				return t.poll();
			return null;
		}

	//for test use
	public boolean isRegisterToBrodcast(MicroService m, Class<? extends Broadcast> b){
		if (broadcasts.containsKey(b)){
			if(broadcasts.get(b).contains(m))
				return true;
		}
		return false;
	}
	public boolean isRegisterToEvent(MicroService m, Class<? extends Event> e){
		if (eventsMapping.containsKey(e)){
			if(eventsMapping.get(e).contains(m))
				return true;
		}
		return false;
	}


}


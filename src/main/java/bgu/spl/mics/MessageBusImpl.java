package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminatedBroadcast;
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
	private ConcurrentHashMap<Class<? extends Event>,BlockingQueue<MicroService>> eventsMapping;
	private ConcurrentHashMap<MicroService,BlockingQueue<Message>> microQueues;
	private ConcurrentHashMap<Class<? extends Broadcast>,BlockingQueue<MicroService>> broadcasts;//check if to convert  the queue to Concurrent link list
	private ConcurrentHashMap<Event,Future> eventsFuture;
	public static CountDownLatch latch;
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
		System.out.println("subscribeEvent "+m.getClass());
		eventsMapping.computeIfAbsent(type, key -> new LinkedBlockingQueue<>()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		System.out.println("subscribeBroadcast "+m.getClass());
		broadcasts.computeIfAbsent(type, key -> new LinkedBlockingQueue<>()).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if (eventsFuture.containsKey(e)){
			eventsFuture.get(e).resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		System.out.println("sendBroadcast");
		BlockingQueue<MicroService> queue = broadcasts.get(b.getClass());
		if (queue != null) {
			for (MicroService t : queue) {
					synchronized (t){
						if(microQueues.containsKey(t)){
            				microQueues.get(t).add(b);
							System.out.println("^send broadcast to "+t.getClass() +" at Q size : "+microQueues.get(t).size());}
						t.notifyAll();
					}
			}
		}

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		System.out.println("Sending event: " + e);
		if (!eventsMapping.containsKey(e.getClass())) {
			return null;
		}
		else{
			BlockingQueue<MicroService> t=eventsMapping.get(e.getClass());
			boolean ex=false;
			MicroService temp=null;
			while(!ex && !t.isEmpty()){
				temp=t.poll();
				synchronized (temp){
				if(microQueues.containsKey(temp)) {
					microQueues.get(temp).add(e);
					ex=true;
				}
				temp.notifyAll();
			}
			}
			if(temp!=null){
				t.add(temp);
			}
			Future<T> f=new Future<T>();
			eventsFuture.put(e,f);
			return f;
		}
	}

	@Override
	public void register(MicroService m) {
		System.out.println("Registering MicroService: " + m);
		microQueues.computeIfAbsent(m, key -> new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		System.out.println("Unregistering microservice "+m);
			synchronized (m) {
				if(microQueues.containsKey(m)) {
					microQueues.remove(m);
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
				m.notifyAll();
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
			BlockingQueue<Message> t=microQueues.get(m);
			if(t!=null) {
				System.out.println("Waiting for message, Q size : "+t.size()+" - "+ m.getClass().getName());
				return t.take();
			}
			return null;
		}
	public boolean stopTicks(){
		for(MicroService m : microQueues.keySet()){
			if(m.getName()=="timer"){
				for(Message mes:microQueues.get(m)){
					if(mes.getClass().equals(TerminatedBroadcast.class)){
						return ((TerminatedBroadcast) mes).getSender().equals("timer");
					}
				}
			}
		}
		return false;
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
	public boolean isMicroServiceRegistered(MicroService m){
		if(microQueues.containsKey(m)){
			return true;
		}
		return false;
	}


}


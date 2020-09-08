package bgu.spl.mics;

import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

//TODO: CHECK SYNCHRONIZED!!!

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

public class MessageBusImpl implements MessageBus {

	ConcurrentHashMap<MicroService, BlockingQueue<Message>> ServicesMap; // holds active services as keys and the value is a
	//queue of active messages to perform
	ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> EventMap;//holds event type as keys and the value is services that can preform the event
	//can be more than one from the same service type
	ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> BroadcastMap;//holds broadcast type as keys and the value is the services that can preform the broadcast
	//can be more than one from the same service type
	ConcurrentHashMap<Event, Future> FutureMap;//holds event as keys and future as value.
	ConcurrentHashMap<MicroService,ConcurrentLinkedQueue<Event>> ActiveEventsMap;

	private static class MessageBusHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl() {
		ServicesMap = new ConcurrentHashMap<>();
		EventMap = new ConcurrentHashMap<>();
		BroadcastMap = new ConcurrentHashMap<>();
		FutureMap = new ConcurrentHashMap<>();
		ActiveEventsMap = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusHolder.instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

		EventMap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		EventMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {

		BroadcastMap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		BroadcastMap.get(type).add(m);

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		FutureMap.get(e).resolve(result);
		FutureMap.remove(e);

	}


	@Override
	public void sendBroadcast(Broadcast b) {
		//we will insert the broadcast message into all the services in the queue which is in the broadcastMap
		ConcurrentLinkedQueue<MicroService> broadQueue = BroadcastMap.get(b.getClass());//the queue of the relevant services that get the broadcast message
		if( broadQueue != null) {
			for (MicroService m : broadQueue) {
				try {

					if(ServicesMap.get(m) != null)
						ServicesMap.get(m).put(b);
				} catch (InterruptedException e) {
				}
			}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		//we will take the service from the head of the queue which is in the eventMap,
		//and insert the message(event) into the service messages queue which is in the serviceMap.
		//when a service get a message it will go to the end of the queue

		if (!EventMap.isEmpty() && EventMap.keySet().contains(e.getClass()) && !EventMap.get(e.getClass()).isEmpty()) {
			Future<T> future = new Future<>();

			MicroService activeService;
			synchronized (EventMap.get(e.getClass())) {

				activeService = EventMap.get(e.getClass()).poll();//take the service from the head of the queue

				if (activeService != null)
					EventMap.get(e.getClass()).add(activeService);//insert the service to the end of the queue - Makes the Round Robin fashion.
				else return null;
			}
			BlockingQueue messageQueue = ServicesMap.get(activeService);
			if( messageQueue!= null) {
				FutureMap.put(e, future);  //initialize the FutureMap
				ConcurrentLinkedQueue<Event> activeEventsQueue = ActiveEventsMap.get(activeService);
			if( activeEventsQueue != null)
				activeEventsQueue.add(e);
			else return null;
					messageQueue.add(e);
			}
			else return null;
			return future; //TODO check if someone else can do the 'while' with wait because this method is non-blocking.
		}

		return null;
	}

	@Override
	public void register(MicroService m) {

		//Creating messages queue for the new service that registers, and adds him to the ServicesMap.
		BlockingQueue<Message> MessagesQueue = new LinkedBlockingQueue<>();
		if (!ServicesMap.containsKey(m))
		ServicesMap.put(m, MessagesQueue);

		ConcurrentLinkedQueue<Event> activeEventsQueue = new ConcurrentLinkedQueue<>();
		ActiveEventsMap.put(m,activeEventsQueue);





	}

	@Override
	public void unregister(MicroService m) {

		if (ServicesMap.keySet().contains(m)) { // Check if ServiceMap has 'm'. if true, removes his queue and references.

			if (!m.CallbacksMap.isEmpty()) {
				for (Class<? extends Message> message : m.CallbacksMap.keySet()) {
					if (EventMap.keySet().contains(message)) {
						EventMap.get(message).remove(m);
					}

					else if (BroadcastMap.keySet().contains(message)) {
						BroadcastMap.get(message).remove(m);

					}
				}

			}

			while (!ActiveEventsMap.get(m).isEmpty()){
				Event eventToHandle = ActiveEventsMap.get(m).poll();
				if (FutureMap.containsKey(eventToHandle))
					complete(eventToHandle,null); // will resolve the event with 'null' and remove it from FutureMap.
			}

			ServicesMap.remove(m); // removes the service from the Services map
		}
	} //end of unregister

	//********************************
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {

		if (!ServicesMap.keySet().contains(m))
			throw new IllegalStateException("This MicroService was never registered.");

		BlockingQueue<Message> messageQueue = ServicesMap.get(m);

		return messageQueue.take();
	}



} // end of MessageBusImpl

package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.actor.AbstractActor;
import akka.actor.AbstractActor.Receive;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.HashMap;

public class Driver extends AbstractActor{
	
	public static class StartProcess {
		public final int numProducers; 
		public final int numConsumers; 
		public StartProcess(int p, int c) {
			this.numProducers = p;
			this.numConsumers = c; 
		}
	} 
	
	public static Props props() {
		return Props.create(Driver.class);
	}
	
	public Receive createReceive() {
		return receiveBuilder()
				.match(StartProcess.class, r -> {
					//create buffer
					//create hashmap of producers
					int producers = r.numProducers;
					int consumers = r.numConsumers;
					HashMap<Integer, akka.actor.ActorRef> p = new HashMap<>();
					HashMap<Integer, akka.actor.ActorRef> c = new HashMap<>();
					for (int i = 1; i <= producers; i++) {
						Integer newInt = new Integer(i);
						p.put(newInt, getContext().actorOf(Producer.props(i), Integer.toString(i)));
					}
					for (int i = 1; i <= consumers; i++) {
						Integer newInt = new Integer(i);
						c.put(newInt, getContext().actorOf(Consumer.props(i), Integer.toString(i)));
					}
					//create buffer with hashmaps as arguments
					akka.actor.ActorRef buffer = getContext().actorOf(Buffer.props(p,c), "buffer");
					//have buffer start requesting data
					for (Integer i : p.keySet()) {
						akka.actor.ActorRef producer = p.get(i);
						producer.tell(new Buffer.Request(), buffer);
					}
				})
				.build();
	}
	

}

package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.actor.AbstractActor;
import akka.actor.AbstractActor.Receive;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.LinkedList;
import java.util.Queue;

import com.example.Producer.Request;

import java.util.HashMap;

import java.util.Objects;

public class Buffer extends AbstractActor{
	//Buffer needs a state representing if it wants to request for more items
	//how should buffer request for more items? 
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	//hashmap to hold producer ids and array to hold consumer ids. key is id, value is actorRef?
	//when you create the Buffer actor from a driver or main actor, it will know the producer and consumer actorRefs. in fact, it can create the hashmaps for us. 
	private HashMap<Integer, akka.actor.ActorRef> p;
	private HashMap<Integer, akka.actor.ActorRef> c;
	//need a queue to represent buffer, this should be public so that the driver/main actor can access it and request for items through the Buffer actor 
	public Queue<Integer> q = new LinkedList<>();
	public int currentProducer; 
	//buffer also needs to send empty message to consumer if buffer is empty AND there are no more active producers (producer hashmap is empty)
	
	public Buffer(HashMap<Integer, akka.actor.ActorRef> produce, HashMap<Integer, akka.actor.ActorRef> consume) {
		this.p = produce;
		this.c = consume;
		this.currentProducer = 1; 
	}
	
	public static Props props(HashMap<Integer, akka.actor.ActorRef> produce, HashMap<Integer, akka.actor.ActorRef> consume) {
		return Props.create(Producer.class, produce, consume);
	}

	public static final class Request {
		//what info does the request need?
		//public final int requestID;
		public Request () {
			//this.requestID = rID;
		}
	}
	
	public static final class Remove {
		public Remove() {
		}
	}
	
	public static final class Ready {
		public int consumerID;
		public Ready(int cID) {
			this.consumerID = cID;
		}
		@Override
	    public boolean equals(Object o) {
	      if (this == o) return true;
	      if (o == null || getClass() != o.getClass()) return false;
	      Ready ready = (Ready) o;
	      return Objects.equals(consumerID, ready.consumerID);
	    }
	}
	
	public static final class Insert {
		public final int producerID;
		//what info does insert need?: producer id and buffer's actorRef
		public Insert (int pID) { 
			this.producerID = pID;
		}
		@Override
	    public boolean equals(Object o) {
	      if (this == o) return true;
	      if (o == null || getClass() != o.getClass()) return false;
	      Insert insert = (Insert) o;
	      return Objects.equals(producerID, insert.producerID);
	    }
	}
	
	public static final class EmptyProducer {
		public final int producerID;
		public EmptyProducer(int pID) {
			this.producerID = pID;
		}
		@Override
	    public boolean equals(Object o) {
	      if (this == o) return true;
	      if (o == null || getClass() != o.getClass()) return false;
	      EmptyProducer emptyProducer = (EmptyProducer) o;
	      return Objects.equals(producerID, emptyProducer.producerID);
	    }
	}
	
	public static final class EmptyBuffer {
		public EmptyBuffer() {
			
		}
	}
	
	public Receive createReceive() {
		return receiveBuilder()
				.match(Insert.class, r -> {
					//enqueue and check size. if there's still space, send another request 
					q.add(1); 
					if (q.size() < 10) {
						Integer newInt = new Integer(this.currentProducer);
						akka.actor.ActorRef produce = p.get(newInt);
						produce.tell(new Request(), getSelf());
						if (this.currentProducer == p.size()) {
							this.currentProducer = 1;
						} else {
							this.currentProducer++;
						}
					}
				})
				.match(Ready.class, r -> {
					//dequeue and send remove message to consumer
					q.remove(); 
					getSender().tell(new Remove(), getSelf());
				})
				.match(EmptyProducer.class, r -> {
					//remove producer from hashmap 
					p.remove(r.producerID); 
				})
				.build();
	}
}

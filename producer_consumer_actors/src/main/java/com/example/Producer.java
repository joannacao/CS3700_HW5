package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.actor.AbstractActor;
import akka.actor.AbstractActor.Receive;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Objects;

public class Producer extends AbstractActor{
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	//int for how many items left to send
	private int items;
	//int for producer id (2 or 5 producers) 
	private final int id;
	
	public Producer(int ID) {
		this.items = 100;
		this.id = ID;
	}
	
	//Request Message
	public static final class Request {
		//what info does the request need?
		public final int requestID;
		public Request (int rID) {
			this.requestID = rID;
		}
		@Override
	    public boolean equals(Object o) {
	      if (this == o) return true;
	      if (o == null || getClass() != o.getClass()) return false;
	      Request requested = (Request) o;
	      return Objects.equals(requestID, requested.requestID);
	    }
	}
	
	//Insert Message to send to Buffer
	public static final class Insert {
		public final int producerID;
		//what info does insert need?: producer id and buffer's actorRef
		public Insert (int pID) { 
			this.producerID = pID;
		}
	}
	
	//Empty Message if there's no more items
	public static final class EmptyProducer {
		public final int producerID;
		public EmptyProducer(int pID) {
			this.producerID = pID;
		}
	}
	
	public static Props props(int ID) {
		return Props.create(Producer.class, ID);
	}

	
	//behavior: if no more items, don't send message. if there are items, send insert message
	public Receive createReceive() {
		return receiveBuilder()
				.match(Request.class, r -> {
					//what do we do when we receive this message
					//if items left, send insert message
					//if empty, send empty message 
					if (this.items != 0) {
						getSender().tell(new Insert(this.id), getSelf());
						log.info("Producer {} inserted item {}", this.id, this.items);
						this.items--;
					} else {
						getSender().tell(new EmptyProducer(this.id), getSelf());
						log.info("Producer {} ran out of items.", this.id);
					}
				})
				.build();
	}
}

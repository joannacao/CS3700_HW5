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

import com.example.Buffer.Insert;

public class Consumer extends AbstractActor{
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private int id;
	
	public Consumer(int ID) {
		this.id = ID;
	}
	
	public static final class Ready {
		private int consumerID;
		public Ready(int cID) {
			this.consumerID = cID;
		}
	}
	
	public static final class Remove {
		public Remove() {
			
		}
		@Override
	    public boolean equals(Object o) {
	      if (this == o) return true;
	      if (o == null || getClass() != o.getClass()) return false;
	      Remove remove = (Remove) o;
	      return true;
	    }
	}
	
	public static final class EmptyBuffer {
		public EmptyBuffer() {
			
		}
		@Override
	    public boolean equals(Object o) {
	      if (this == o) return true;
	      if (o == null || getClass() != o.getClass()) return false;
	      EmptyBuffer emptyBuffer = (EmptyBuffer) o;
	      return true;
	    }
	}
	
	public static Props props(int ID) {
		return Props.create(Consumer.class, ID);
	}
	
	public Receive createReceive() {
		return receiveBuilder()
				.match(Remove.class, r -> {
					//what do we do when we receive remove message
					//consume an item
					this.wait(10); //wait to consume
					log.info("Consumer {} consumed item.", this.id);
					//send ready message
					getSender().tell(new Ready(this.id), getSelf());
				})
				.match(EmptyBuffer.class, r -> {
					//if buffer is empty
					log.info("Consuming completed.");
					Behaviors.stopped();
				})
				.build();
	}

}

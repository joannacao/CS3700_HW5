package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.actor.AbstractActor;
import akka.actor.AbstractActor.Receive;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.ArrayList;

import java.util.Objects;

public class FindingPrime extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	public int currentPrime; 
	public ArrayList<Integer> list;
	
	public FindingPrime(int prime, ArrayList<Integer> aList) {
		this.currentPrime = prime;
		this.list = aList;
	}
	//have a designated int as our prime number. we need a list of integers left as well. 
	//need to find nonmultiples of that prime number with a list of leftover nonmultiples as input
	//nonmultiple closest to current prime (first/smallest number in list) is new prime
	//create new actor with this prime, and the new list
	
	//what messages do we need? 
	public static final class FindNonMultiples {
		public FindNonMultiples() {
			
		}
	}
	
	public static Props props(int prime, ArrayList<Integer> list) {
		return Props.create(FindingPrime.class, prime, list);
	}
	
	public Receive createReceive() {
		return receiveBuilder()
				.match(FindNonMultiples.class, r -> {
					//iterate through whole list
					int i = 0; 
					while ( i < this.list.size()) {
						if (this.list.get(i) % this.currentPrime == 0) { //if this number is a multiple
							//remove
							this.list.remove(i);
						}
						i++; 
					}
					akka.actor.ActorRef newActor = getContext().actorOf(props(this.list.get(0), this.list));
					newActor.tell(new FindNonMultiples(), getSelf());
				})
				.build();
	}

}

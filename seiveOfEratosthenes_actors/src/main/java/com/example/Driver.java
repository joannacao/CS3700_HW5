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

public class Driver extends AbstractActor{
	
	public static class StartProcess {
		public StartProcess() {
			
		}
	} 
	
	public static Props props() {
		return Props.create(Driver.class);
	}
	
	public Receive createReceive() {
		return receiveBuilder()
				.match(StartProcess.class, r -> {
					//create first FindingPrime actor
					ArrayList<Integer> arr = new ArrayList<>(1000000);
					for (int i = 2; i <= 1000000; i++) {
						arr.add(i); 
					}
					akka.actor.ActorRef findPrime = getContext().actorOf(FindingPrime.props(2,arr), "findPrime");
				})
				.build();
	}
	

}

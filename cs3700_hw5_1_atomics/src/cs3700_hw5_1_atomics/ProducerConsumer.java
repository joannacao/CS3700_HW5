package cs3700_hw5_1_atomics;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.*;


class Produce_Consume implements Runnable {
	private AtomicIntegerArray queue; 
	private String type; 
	private int id;
	private static Object marker = new Object();  
	private int numProducers; 
	private int numConsumers; 
	private static boolean finish = false; 
	private static AtomicInteger producerFinishCount = new AtomicInteger();
	private static AtomicInteger consumerFinishCount = new AtomicInteger();
	
	public Produce_Consume(AtomicIntegerArray a, String t, int i, int num, int num2) {
		this.queue = a; 
		this.type = t; 
		this.id = i; 
		this.numProducers = num; 
		this.numConsumers = num2; 
	}
	
	public void run() {
		if (this.type == "producer") {
			producerFinishCount.set(0);;
			//create counter representing how many items were put in queue 
			AtomicInteger counter = new AtomicInteger(1); 
			//while counter is less than 100 
			AtomicInteger index = new AtomicInteger(0); //to keep track of place in queue
			while (counter.get() <= 100) {
				//if queue is full, do nothing. else, lock queue, add item, then unlock and increment counter
				synchronized(marker) {
					if (index.get() == 9) {
						//if there is still item at index 0, sleep 
						//else, put item there and set index to 1
						if (queue.get(9)!=0) {
							continue;
						} else {
							queue.set(9, counter.get());
							System.out.println("Producer " + this.id + " added item " + counter.get()); 
							counter.getAndIncrement(); //counter needs to be atomic as well
							index.set(0);; 
						}
					} else {
						if (queue.get(index.get())!=0) {
							continue;
						} else {
							queue.set(index.get(), counter.get());
							System.out.println("Producer " + this.id + " added item " + counter.get()); 
							counter.getAndIncrement(); //counter needs to be atomic as well 
							index.getAndIncrement();
						}
					}
				}
			}
			System.out.println("Producer " + this.id + " is done."); 
			producerFinishCount.getAndIncrement();
			if (producerFinishCount.get() >= this.numProducers) {
				finish = true;
			}
		} else if (this.type == "consumer"){
			long startTime = System.currentTimeMillis();
			consumerFinishCount.set(0);; 
			AtomicInteger index = new AtomicInteger(0);
			while (true) {
				if (finish) {
					break;
				} else {
					//always start by checking the first index, then go up
					try {
						synchronized(marker) {
							if (index.get() == 9) {
								//check, then change index to 0 again
								if (queue.get(9)!=0) {
									//change to 0
									Thread.sleep(10);
									queue.set(9, 0);
									System.out.println("Consumer " + this.id + " consumed one item");
									index.set(0);;
								}
							} else {
								Thread.sleep(10);
								queue.set(index.get(), 0);
								System.out.println("Consumer " + this.id + " consumed one item");
								index.getAndIncrement();
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("Done consuming!");
			consumerFinishCount.getAndIncrement();
			if (consumerFinishCount.get() >= this.numConsumers) {
				long endTime = System.currentTimeMillis();
				long time = endTime - startTime;
				System.out.println("Execution time: " + time + " ms");
			}
		}
		
	}
}

public class ProducerConsumer {
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		//create array blocking queue with max size of 10 
		AtomicIntegerArray abq = new AtomicIntegerArray(10);
		for (int i = 0; i < 10; i++) {
			abq.set(i, 0);
		}
		//still need to use locks  
		
		//uncomment to test 5 consumers, 2 producers
		/*
		new Thread(new Produce_Consume(abq, "producer", 1, 2, 5)).start(); 
		new Thread(new Produce_Consume(abq, "consumer", 1, 2, 5)).start(); 
		new Thread(new Produce_Consume(abq, "producer", 2, 2, 5)).start(); 
		new Thread(new Produce_Consume(abq, "consumer", 2, 2, 5)).start(); 
		new Thread(new Produce_Consume(abq, "consumer", 3, 2, 5)).start(); 
		new Thread(new Produce_Consume(abq, "consumer", 4, 2, 5)).start(); 
		new Thread(new Produce_Consume(abq, "consumer", 5, 2, 5)).start();
		*/
		
		//uncomment to test 5 producers, 2 consumers
		
		new Thread(new Produce_Consume(abq, "producer", 1, 5, 2)).start(); 
		new Thread(new Produce_Consume(abq, "consumer", 1, 5, 2)).start(); 
		new Thread(new Produce_Consume(abq, "producer", 2, 5, 2)).start(); 
		new Thread(new Produce_Consume(abq, "consumer", 2, 5, 2)).start(); 
		new Thread(new Produce_Consume(abq, "producer", 3, 5 ,2)).start(); 
		new Thread(new Produce_Consume(abq, "producer", 4, 5 ,2)).start(); 
		new Thread(new Produce_Consume(abq, "producer", 5, 5, 2)).start(); 
		
		executorService.shutdown(); 
	}

}

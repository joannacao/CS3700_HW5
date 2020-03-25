package cs3700_hw5_1_isolated_sections;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Produce_Consume implements Runnable {
	private BlockingQueue queue; 
	private String type; 
	private int id;
	private static Object marker = new Object(); 
	private static Object marker2 = new Object(); 
	private static Object marker3 = new Object(); 
	private int numProducers; 
	private int numConsumers; 
	private static boolean finish = false; 
	private static int producerFinishCount;
	private static int consumerFinishCount;
	
	public Produce_Consume(BlockingQueue a, String t, int i, int num, int num2) {
		this.queue = a; 
		this.type = t; 
		this.id = i; 
		this.numProducers = num; 
		this.numConsumers = num2; 
	}
	
	public void run() {
		if (this.type == "producer") {
			producerFinishCount = 0;
			try {
				//create counter representing how many items were put in queue 
				int counter = 1; 
				//while counter is less than 100 
				while (counter <= 100) {
					//if queue is full, do nothing. else, lock queue, add item, then unlock and increment counter
					synchronized(marker) {
						if (queue.remainingCapacity() > 0) {
							int initSize = queue.size(); 
							queue.put(counter); 
							int newSize = queue.size(); 
							if (initSize != newSize) {
								System.out.println("Producer " + this.id + " added item " + counter); 
								counter++; 
							}
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Producer " + this.id + " is done."); 
			synchronized(marker2) {
				producerFinishCount++; 
				if (producerFinishCount >= this.numProducers) {
					finish = true; 
				}
			}
		} else {
			long startTime = System.currentTimeMillis();
			consumerFinishCount = 0; 
			while (true) {
				if (finish && queue.isEmpty()) {
					break;
				} else {
					synchronized(marker) {
						if (!queue.isEmpty()) {
							try {
								Thread.sleep(10);
								queue.take();
								System.out.println("Consumer " + this.id + " consumed one item");
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			System.out.println("Done consuming!");
			synchronized(marker3) {
				consumerFinishCount++;
				if (consumerFinishCount >= this.numConsumers) {
					long endTime = System.currentTimeMillis();
					long time = endTime - startTime;
					System.out.println("Execution time: " + time + " ms");
				} 
			}
		}
	}
}

public class ProducerConsumer {
	//create producers. each one will produce 100 items
		//create bounded buffer (blocking array queue?) that can hold max 10 items
		//create consumers. takes 1 second (might use ms) to consumer an item. 
		
		public static void main(String[] args) {
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			//create array blocking queue with max size of 10 
			BlockingQueue abq = new ArrayBlockingQueue<Integer>(10); 
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

package cs3700_hw5_2;

import java.util.*;

public class Sieve_of_Eratosthenes {
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		boolean mark[] = new boolean[1000000+1];
		for (int i = 0; i < 1000000; i++) {
			mark[i] = true;
		}
		for (int p=2; p*p<1000000;p++) {
			if (mark[p] == true) {
				for (int i = p*p; i < 1000000; i+=p) {
					mark[i] = false;
				}
			}
		}
		for (int i = 2; i <= 1000000; i++) {
			if(mark[i] == true) {
				System.out.println(i + " ");
			}
		}
		
		//starting from p*p, change the second p in increments of p. p(p+1), p(p+2) all the way until you reach the end of the list. mark the numbers greater than or equal to p*p
		//find the first number greater than p not marked. this is the next prime. if there's no number, we're at the end. 
		//make p now equal to this number, and repeat. 
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime; 
		System.out.println("Execution time: " + time + " ms");
	}

}

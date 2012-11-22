package com.next.infotech.concurrent;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;


@Component
public class CounterManager {

	private final ConcurrentHashMap<String,AtomicLong> counterMap = new ConcurrentHashMap<String,AtomicLong>();
	
	/**
	 * Increment by 1 a given counter by name, if not exists then create it and set its value to 1
	 * @param counterName
	 */
	public void incrementCounter(String counterName){
		incrementCounter(counterName, 1L);
	}

	/**
	 * Increment by delta a given counter by name, if not exists then create it and set its value to delta
	 * @param counterName
	 */
	public void incrementCounter(String counterName,long delta){
		getOrCreateCounter(counterName).addAndGet(delta);
	}
	/**
	 * reset a given counter by name to value =0, if not exists then create it and set its value to 0
	 * @param counterName
	 */
	public void resetCounter(String counterName){
		getOrCreateCounter(counterName).set(0);
	}
	
	private AtomicLong getOrCreateCounter(String counterName){
		AtomicLong counter = counterMap.get(counterName);
		if(counter == null){
			counter = new AtomicLong(0L);
			AtomicLong existingCounter = counterMap.putIfAbsent(counterName, counter);
			if(existingCounter != null){
				counter = existingCounter;
			}
		}
		return counter;
	}
	
	public Map<String, Long> getCounters(){
		Map<String, Long> returnMap = new TreeMap<String, Long>();
		for(Entry<String,AtomicLong> oneEntry:counterMap.entrySet()){
			returnMap.put(oneEntry.getKey(), oneEntry.getValue().get());
		}
		return returnMap;
	}

}

package pagerank;

import java.util.HashMap;
import java.util.PriorityQueue;

public class cache {
	private HashMap<String, Byte[]> invertedlist;
	private int cache_size;
	private int maximum_cache_size;
	PriorityQueue<queue> q; 
	
	public cache(){
		this.cache_size=0;
		this.invertedlist=new HashMap<String, Byte[]>();
		this.maximum_cache_size = 100000000;
		this.q = new  PriorityQueue<queue> ();
	}
	
	public Byte[] get(String word){
		Byte[] temp = invertedlist.get(word);
		return temp;
	}
	
	public boolean contains(String word){
		return this.invertedlist.containsKey(word);
	}

	public void put(String word, Byte[] df, int size){
		while(this.cache_size + size > this.maximum_cache_size){
			System.out.println("cache is full:" + this.cache_size);
			queue temp = this.q.poll();
			this.cache_size = this.cache_size - invertedlist.get(temp.getword()).length;
			this.invertedlist.remove(temp.getword());
			temp = null;
		}
		queue q= new queue(word, 1,size);
		this.q.add(q);
		this.cache_size = this.cache_size + size;
		this.invertedlist.put(word, df);
	}
}



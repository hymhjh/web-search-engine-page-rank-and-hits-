package pagerank;

import java.util.ArrayList;

public class results {
	ArrayList<tuple> h = new ArrayList<tuple>();
	
	public boolean isEmpty(){
		return h.size() == 0;
	}
	
	public void push(tuple a){
		if(h.size() == 0) h.add(a);//begin with 1 not 0
		else{
			int i = 0;
			while(i < h.size() && a.getScore() < h.get(i).getScore()){
				i++;
			}
			h.add(i, a);
			if(h.size() > 200) h.remove(200);
		}
	}
	public tuple pop(){
		tuple temp = h.get(0);
		h.remove(0);
		return temp;
	}
	public int size(){
		return this.h.size();
	}
}


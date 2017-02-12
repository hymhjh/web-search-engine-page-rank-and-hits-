package pagerank;

public class queue implements Comparable<queue>{
	private String word;
	private int visit_times;
	private int length;
	
	public queue(String word, int visit_times, int l){
		this.setword(word);
		this.setvisit_times(visit_times);
		this.length = l;
	}
	
	public void add_visit_times(){
		this.visit_times++;
	}

	public void setword(String word) {
		this.word = word;
	}

	public String getword() {
		return word;
	}

	public void setvisit_times(int visit_times) {
		this.visit_times = visit_times;
	}

	public int getvisit_times() {
		return visit_times;
	}
	
	public int getlength() {
		return this.length;
	}
	
	public int compareTo(queue item){
		return this.visit_times - item.visit_times;
	}
}



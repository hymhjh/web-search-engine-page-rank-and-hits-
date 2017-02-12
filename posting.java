package pagerank;

public class posting implements Comparable<posting> {
	
	private int f;
	private int did;
	private String term;
	
	public posting(int f, int docid, String term){//frequency, docid, position
		this.f = f;
		this.did = docid;
		this.term = term;
	}
	
	public int getDocumentId() {
		return did;
	}

	public void setDocumentId(int documentId) {
		this.did = documentId;
	}

	public int getFrequency() {
		return this.f;
	}
	
	public String getTerm() {
		return term;
	}
	
	public String toString() {
		return Integer.toString(getDocumentId()) + " " + Integer.toString(getFrequency());
	}
	
	public int compareTo (posting o) {
		int compareWord = this.term.compareTo(o.term);
		if(compareWord == 0){
			return this.did - o.did;
		}else{
			return compareWord;
		}
	}
}




package pagerank;

public class tuple {
	
	private int docid;
	private double score;
	
	public tuple(int d, double b){
		this.docid = d;
		this.score = b;
	}
	
	public int getDocumentId() {
		return docid;
	}
	public double getScore() {
		return score;
	}
}

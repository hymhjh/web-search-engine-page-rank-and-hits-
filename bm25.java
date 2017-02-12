package pagerank;

import java.io.IOException;

public class bm25 {
	int f; //frequency of term in each document
	long num; //number of documents that contain term t
	int N; //total number of documents in the collection
	int dl; //length of document
	double avgdl; //the average length of documents in the collection
	
	public bm25(int f, long num, int N, int dl, double avgdl){
		this.f = f;
		this.num = num;
		this.N = N;
		this.dl = dl;
		this.avgdl = avgdl;
	}
	
	public double cal_bm25(){
	    double score = 0.0;
	    double k = 1.2, b = 0.75;
		double idf = Math.log(((N-num)+0.5)/(num+0.5));
		if (idf < 0) {
			idf = 0;
	    }
	    score = idf * (f*(k+1)/(f+k*(1-b+b*dl/avgdl)));
	    return score;
	}

}


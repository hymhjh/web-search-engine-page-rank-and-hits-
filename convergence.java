package pagerank;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class convergence {

	final static int max_docid = 2494475;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileInputStream is = new FileInputStream("pre_pr.txt");
		Reader decoder = new InputStreamReader(is, "utf-8");
		BufferedReader br = new BufferedReader(decoder);
		
		FileInputStream is1 = new FileInputStream("cur_pr.txt");
		Reader decoder1 = new InputStreamReader(is1, "utf-8");
		BufferedReader br1 = new BufferedReader(decoder1);
		
		String s = "";
		String s1 = "";
		int check = 0;
		while((s = br.readLine() )!= null) {
			if((s1 = br1.readLine() )!= null){
				String[] sp = s.split(" ");
				String[] sp1 = s1.split(" ");
				double pre_pr = Double.parseDouble(sp[sp.length-1]);
				double cur_pr = Double.parseDouble(sp1[sp1.length-1]);
				
				if(Math.abs(pre_pr - cur_pr) <= 0.001) check++;
			}
		}
		if (check == (max_docid + 1)) System.out.println("It is convergent!");
		else System.out.println("No convergence, calculate again.");
	}

}

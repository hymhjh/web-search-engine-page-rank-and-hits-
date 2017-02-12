package pagerank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class unixsort {
	
	public static void merge(int m, int n){
			String[] command = {"/bin/sh","-c", "sort -k 1,1 -k 2,2n -u /Users/huangyiman/workplace/pagerank/"+String.valueOf(m)+".txt /Users/huangyiman/workplace/pagerank/"+String.valueOf(n)+".txt"};
			Process pro = null;
			try {
				pro = Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			InputStream is = pro.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        File file = new File(String.valueOf(m+10)+".txt");
			PrintStream ps = null;
			try {
				ps = new PrintStream(new FileOutputStream(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        String oldw = "";
	        int flag = 1;
	        try {
				while ((line = br.readLine()) != null) {
					String[] neww = line.split("\\s+");
					//System.out.println(neww[0]);
					if(neww[0].equals(oldw)){
						String temp = line.substring(neww[0].length()+1);
						temp = temp.replaceAll(" +", " ");
						ps.append(temp+" ");
					}else{
						oldw = neww[0];
						if(flag == 1){
							line = line.replaceAll(" +", " ");
							ps.append(line + " ");
							flag = 0;
						}
						else{
							line = line.replaceAll(" +", " ");
							ps.append("\n" + line + " ");
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	        //ps.append(nsb.toString());   
		
	}

	public static void del(int m, int n){
		String[] delete = {"/bin/sh","-c", "rm -f /Users/huangyiman/workplace/pagerank/"+String.valueOf(m)+".txt /Users/huangyiman/workplace/pagerank/"+String.valueOf(n)+".txt"};
		Process pro_d = null;
		try {
			pro_d = Runtime.getRuntime().exec(delete);
			pro_d.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String[] cat = {"/bin/sh","-c", "cat /Users/huangyiman/workplace/pagerank/final.txt >> /Users/huangyiman/workplace/pagerank/"+String.valueOf(m+8)+".txt"};
		Process pro_c = null;
		try {
			pro_c = Runtime.getRuntime().exec(cat);
			pro_c.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		for(int i = 0; i < 2; i+=2){			
		int m=i;
			merge(m,m+1);
			del(m,m+1);
			try{
			    Thread.sleep(1000);
			}catch (InterruptedException e){
			    e.printStackTrace();
			}
		}
	}

}



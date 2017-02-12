package pagerank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.HashMap;

public class inlink {
	public static void main(String[] args) throws IOException {
		outToin();//convert outlink into the format: docID, docID(the webpage that points to document at first column)
		merge(); // merge the inlinks with same citation.
	}
	public static void outToin() throws IOException{
		File output = new File("inlink.txt");
		PrintStream citations = new PrintStream(new FileOutputStream(output));
		
		File f = new File("outlink.txt");
		InputStream is = new FileInputStream(f);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String s = "";
		
		while((s = br.readLine())!= null) {
			String[] sp = s.split("\\s+");
			String citation = sp[0];
			for(int i = 1; i < sp.length-1; i++){
				citations.append(sp[i] + " " + citation + "\n");
			}
		}
		citations.close();
	}
	
	public static void merge(){
		String[] command = {"/bin/sh","-c", "sort -k 1,1n -k 2,2n /Users/huangyiman/workplace/pagerank/inlink.txt"};
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
        File file = new File("finalinlink.txt");
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
				if(neww[0].equals(oldw)){
					ps.append(neww[1]+" ");
				}else{
					oldw = neww[0];
					if(flag == 1){
						ps.append(line + " ");
						flag = 0;
					}
					else{
						ps.append("\n" + line + " ");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}

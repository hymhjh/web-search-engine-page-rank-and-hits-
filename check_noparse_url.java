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
import java.util.HashSet;

public class check_noparse_url {

	public static void main(String[] args) throws IOException {
		
		HashSet<String> map = new HashSet<String>();
		
		File output2 = new File("urltable_noparse.txt");
		PrintStream ps = null;
		try {
			ps = new PrintStream(new FileOutputStream(output2));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("start convert");
		int docid = 44191039+1;
		File f = new File("outlink.txt");
		InputStream is = new FileInputStream(f);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String s = "";
			
		while((s = br.readLine())!= null) {
			if(docid > 44191039 + 3000000) break;
			String[] sp = s.split("\t+");
			for(int k=1; k<sp.length; k++){
				try{
					int temp = Integer.parseInt(sp[k]);
				}catch(Exception e){
					if(!map.contains(sp[k])){
						ps.append(docid+" "+sp[k]+"\n");//urltable_NoParse
						docid++;
					}
				}
			}
		}
	}

}

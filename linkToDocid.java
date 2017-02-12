package pagerank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.HashMap;

public class linkToDocid {
	public static void main(String[] args) throws IOException {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		try{
			System.out.println("load urltable");
			FileInputStream is = new FileInputStream("urltable16.txt");
			Reader decoder = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(decoder);
			String s = "";
			while((s = br.readLine() )!= null) {
				String[] sp = s.split(" ");
				if(sp.length > 1) map.put(sp[1], Integer.parseInt(sp[0]));
				//System.out.println(sp[0]);
			}
		}catch (Exception e){
			System.out.println(e);
		}
		System.out.println("start convert");
		File output = new File("outlink2.txt");
		PrintStream link_graph = new PrintStream(new FileOutputStream(output));
		
		File f = new File("outlink.txt");
		InputStream is = new FileInputStream(f);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String s = "";
			
		while((s = br.readLine())!= null) {
			String[] sp = s.split("\t+");
			StringBuilder linkto = new StringBuilder();
			linkto.append(sp[0]);
			for(int k = 1; k < sp.length; k++){
				try{
					int temp = Integer.parseInt(sp[k]);
					linkto.append("\t"+temp);
				}catch(Exception e){
					if(map.containsKey(sp[k])){
						linkto.append("\t"+map.get(sp[k]));
					}else{
						linkto.append("\t"+sp[k]);
					}
				}
					
			}
			link_graph.append(linkto.toString() + "\n");
		}
		
		link_graph.close();
	}
	
}

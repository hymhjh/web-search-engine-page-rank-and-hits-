package pagerank;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class pagerankquery {
	final static int max_docid = 2494475;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, int[]> map = new HashMap<String, int[]>();// lexicon: word,[length, pointer]
		try {
			//read the lexicon into main memory
			System.out.println("start loading lexicon...");
			FileInputStream is = new FileInputStream("lexicon");
			Reader decoder = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(decoder);
			String s = "";
			while((s = br.readLine() )!= null) {
				String[] ss = s.split(" ");
				if(ss.length>2){
					int[] temp = new int[2];
					temp[0] = Integer.parseInt(ss[1]);//length (number of pages)
					temp[1] = Integer.parseInt(ss[2]);//pointer
					map.put(ss[0], temp);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		ArrayList<String[]> url = new ArrayList<String[]>();//url Table: url, size
		long totalsize = 0;
		try {
			//read the url Table into main memory
			System.out.println("start loading urlTable...");
			FileInputStream is = new FileInputStream("urltable.txt");
			Reader decoder = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(decoder);
			String s = "";
			while((s = br.readLine() )!= null) {
				String[] ss = s.split("\\s+");
				String[] temp = new String[3];
				temp[0] = ss[1];//url
				temp[1] = ss[ss.length-2];//size
				temp[2] = String .format("%.5f",Double.parseDouble(ss[ss.length-1]));//pagerank scores
				totalsize += Integer.parseInt(ss[2]);
				url.add(temp);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int N = url.size();
		double avg_d = totalsize*1.0 / N;

		cache cache2 = new cache();
		HashMap<String, queue> visit_time = new HashMap<String, queue>();
		while(true){
			System.out.println("please input the search word: ");
			Scanner sc = new Scanner(System.in);
			String search = sc.nextLine();
			search = search.toLowerCase();
			results he = new results();
			//long start = System.nanoTime();   //start time
			
			if(search.contains(" or ")){ //disjunctive
				search = search.replaceAll(" or ", " ");
				String[] words = search.split("\\s+");
				int wl = words.length;
				ArrayList<String> final_w = new ArrayList<String>();
				ArrayList<Byte[]> lp = new ArrayList<Byte[]>();//docid and frequency list
				for(int i=0; i<words.length;i++){
					Byte[] temp;
					if(cache2.contains(words[i])) {
						temp = cache2.get(words[i]);
						queue time = visit_time.get(words[i]);//get the visit time
						queue q_temp = new queue(words[i], time.getvisit_times()+1, time.getlength());
						cache2.q.remove(time);
						cache2.q.add(q_temp);
						visit_time.put(words[i], q_temp);
					}
					else {
						if(!map.containsKey(words[i])) {
							System.out.println("no such word in lexicon:"+words[i]);
							wl--;
							continue;
						}
						temp = open(map.get(words[i])[1], map.get(words[i])[0]);
						cache2.put(words[i], temp, temp.length);
						queue q_temp = new queue(words[i], 1, temp.length);
						cache2.q.add(q_temp);
						visit_time.put(words[i], q_temp);//record the visit time
					}
					lp.add(temp);
					final_w.add(words[i]);//the word list exclude the word that is not in lexicon
				}
				int[] cursor = new int[wl];
				int[] last_doc = new int[wl];
				HashMap<Integer,Double> or = new HashMap<Integer,Double>();
				for(int i=0; i<wl; i++){
					for(; cursor[i]<lp.get(i).length;){
						int did = decode(lp.get(i), cursor, i) + last_doc[i];
						last_doc[i] = did;
						int fre = decode(lp.get(i), cursor, i);
						bm25 bm = new bm25(fre, map.get(final_w.get(i))[0], N, Integer.parseInt(url.get(did)[1]), avg_d);
						double sco = bm.cal_bm25();	
						double pagerank = Double.parseDouble(url.get(did)[2]);
						if(or.containsKey(did)){
							sco = or.get(did) + sco;
							or.put(did, sco);
						}
						else {
							sco = sco + 1.8*(Math.pow(pagerank, 0.6)/(Math.pow(1, 0.6)+Math.pow(pagerank, 0.6)));//bm25+pagerank
							or.put(did, sco);
						}
						tuple tu = new tuple(did, sco);
						he.push(tu);
					}
				}
			}else{ //conjunctive
				search = search.replaceAll(" and ", " ");
				String[] words = search.split("\\s+");
				ArrayList<String> sort_w = new ArrayList<String>();
				ArrayList<Byte[]> lp = new ArrayList<Byte[]>();//docid and frequency list
				int flags = 0;
				for(int i=0; i<words.length;i++){
					Byte[] temp;
					if(cache2.contains(words[i])) {
						temp = cache2.get(words[i]);
						queue time = visit_time.get(words[i]);//get the visit time
						queue q_temp = new queue(words[i], time.getvisit_times()+1, time.getlength());
						cache2.q.remove(time);
						cache2.q.add(q_temp);
						visit_time.put(words[i], q_temp);
					}
					else {
						if(!map.containsKey(words[i])) {
							flags =1;
							System.out.println("sorry, no such word:"+words[i]);
							break;
						}
						temp = open(map.get(words[i])[1], map.get(words[i])[0]);
						cache2.put(words[i], temp, temp.length);
						queue q_temp = new queue(words[i], 1, temp.length);
						cache2.q.add(q_temp);
						visit_time.put(words[i], q_temp);//record the visit time
					}
					if(i==0) {
						lp.add(temp);
						sort_w.add(words[i]);
					}
					else{//sort by length of term list	
						int j = 0;
						while(j < lp.size() && map.get(words[i])[0] > lp.get(j).length){
							j++;
						}
						lp.add(j, temp);
						sort_w.add(j, words[i]);
					}
				}
				if(flags == 1) continue;
				int[] cursor = new int[words.length];
				int did = 0;
				int[] last_doc = new int[words.length];

				while(did <= max_docid){//daat
					int[] fre = new int[words.length];
					did = nextGEQ(lp.get(0), did, 0, cursor, last_doc);
					fre[0] = getFRE(lp.get(0), 0, cursor);
					int d = did;
					for (int i=1; i<words.length; i++){
						d = nextGEQ(lp.get(i), did, i, cursor, last_doc);
						fre[i] = getFRE(lp.get(i), i, cursor);
						if(did != d) break;
					}
					if (did < d)  did = d;      /* not in intersection */
					else{
					    
					    /* compute BM25 score from frequencies and other data */
					    double scores = 0.0;
					    for(int k=0; k<words.length; k++){
					    	bm25 bm = new bm25(fre[k], map.get(sort_w.get(k))[0], N, Integer.parseInt(url.get(did)[1]), avg_d);
					    	scores += bm.cal_bm25();
					    	//System.out.println(scores+","+fre[k]+","+map.get(sort_w.get(k))[0]+","+N+","+Integer.parseInt(url.get(did)[1])+","+avg_d);
					    }
					    double pagerank = Double.parseDouble(url.get(did)[2]);
					    scores = scores + 1.8*(Math.pow(pagerank, 0.6)/(Math.pow(1, 0.6)+Math.pow(pagerank, 0.6)));//bm25+pagerank
					    tuple tu = new tuple(did, scores);
					    he.push(tu);
					    did++;    /* and increase did to search for next post */
					}
				}
			}
			JFrame frame = new JFrame();
			JPanel panel=new JPanel();
			frame.setVisible(true);
			frame.setSize(1100,400);
			frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
			frame.add(panel);
			panel.setBackground(Color.black);
			int results = 10;
			while(!he.isEmpty() && results > 0){
				tuple tem = he.pop();
				web label = new web(url.get(tem.getDocumentId())[0]);
				panel.setLayout(null);
				panel.add(label); 
				label.setBounds(100,75+(10-results)*20,1000,20); 
				label.setText(String.valueOf(tem.getScore())+" "+url.get(tem.getDocumentId())[0]);
				results--;
			}
		}
	}
	
	public static int decode(Byte[] in, int[] cursor, int i) throws IOException {
        int out = 0;
        int shift=0;
        byte readbyte = in[cursor[i]];
        cursor[i]++;
        while( (readbyte & 0x80)==0) {
            out |= (readbyte & 127) << shift;
            readbyte = in[cursor[i]];
            cursor[i]++;
            shift+=7;
        }
        out |= (readbyte & 127) << shift;
        return out;
    }
	public static Byte[] open(long pointer, long len) throws IOException{
		RandomAccessFile ff = new RandomAccessFile("invertedIndex", "rw");
		ArrayList<Byte> list = new ArrayList<Byte>();//a list for docid and frequency
		long i = pointer;
        int l = 0;
        while(l < len*2){
        	ff.seek(i);
        	byte readbyte = ff.readByte();
        	i++;
        	while((readbyte & 0x80)==0){
        		list.add(readbyte);
        		ff.seek(i);
        		readbyte = ff.readByte();
        		i++;
        	}
        	list.add(readbyte);
        	l++;
        }
        Byte[] a = (Byte[]) list.toArray(new Byte[list.size()]);
        return a;
	}
	public static void close(String word, HashMap<String, int[]> m){
		
	}
	public static int nextGEQ(Byte[] lp,int did, int i, int[] cursor, int[] last_doc) throws IOException{//pointer, the searching did
		int docid = 0;
		while(cursor[i] < lp.length){
			docid = decode(lp, cursor, i) + last_doc[i];//docid
			last_doc[i] = docid;
			if(docid >= did){
				return  docid;
			}
			getFRE(lp, i, cursor);//skip this frequency
		}
        return max_docid;
	}
	public static int getFRE(Byte[] lp, int i, int[] cursor) throws IOException{
		if(cursor[i] >= lp.length) return 0;
		int fre = decode(lp, cursor, i);
		return fre;
	}
}

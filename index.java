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
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class index {

	final static int number = 10000000;
	
	public static void clean(posting[] p, String name, int pointer) throws IOException{
		File file = new File(name+".txt");
		PrintStream ps = new PrintStream(new FileOutputStream(file));
		String oldw = "";
		for(int i = 0; i < pointer; i++){
			if(p[i].getTerm().equals(oldw)){
				ps.append(" "+p[i].toString());
			}else{
				oldw = p[i].getTerm();
				if(i != 0) ps.append("\n" + p[i].getTerm()+" "+p[i].toString());
				else ps.append(p[i].getTerm()+" "+p[i].toString());
			}
		}
		ps.append("\n");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File output = new File("link.txt");
		PrintStream link_graph = null;
		try {
			link_graph = new PrintStream(new FileOutputStream(output));
		} catch (FileNotFoundException e2) {
			System.out.println(e2);
		}
		File output1 = new File("urlTable.txt");
		PrintStream urltable = null;
		try {
			urltable = new PrintStream(new FileOutputStream(output1));
		} catch (FileNotFoundException e2) {
			System.out.println(e2);
		}
		
		int max = 0;
		int docid = 0;
		posting[] pos = new posting[number];
		int tempory = 0;//number of tempory file
		int pointer = 0;
		
		for(int datanum = 90; datanum < 100; datanum++){
			for(int num = 0; num < 100; num++){
				InputStream fs = null;
				try {
					fs = new FileInputStream("GX0"+String.valueOf(datanum)+"/"+String.valueOf(num)+".gz");
				} catch (FileNotFoundException e1) {
					continue;
				}
				InputStream gs = null;
				try {
					gs = new GZIPInputStream(fs);
				} catch (IOException e1) {
					continue;
				}
				Reader decoder = new InputStreamReader(gs);
				BufferedReader b = new BufferedReader(decoder);
				String ss= "";
				try {
					while((ss = b.readLine())!= null) {
						if(ss.equals("<DOCHDR>")){
							String homeurl = "";
							int length = -1, flag = 0;
							ss = b.readLine();
							homeurl = ss.substring(0,ss.length());//get the url of page
							while((ss = b.readLine())!= null) {
								
								if(ss.contains("Content-Length:")){
									try{
										length = Integer.parseInt(ss.substring(16,ss.length()).trim());//get the size of page
									}catch (Exception e){
										length = 0;
									}
								}
								if(length == 0) break;//no page
								//read the content
								if(length > 0 & ss.contains("<html>")){
									StringBuilder sb_page = new StringBuilder();
									String[] f_and_pos;
									sb_page.append(ss);
									sb_page.append(" ");
									while((ss = b.readLine())!= null) {
										if(ss.contains("<DOC>")) {
											flag = 2;//mark page
											break;
										}
										sb_page.append(ss);//record the content
										sb_page.append(" ");
									}
									String html = sb_page.toString();
									String url = "";
									String linkto = "";
									Document doc = new Document("");
									try{
										doc = Jsoup.parse(html,homeurl);
									}catch (Exception ed){
										break;
									}
									//parse hyperlink from webpages
									Elements e = doc.getElementsByTag("a");
									HashSet<String> hs = new HashSet<String>();
									for(Element link : e){
										url = link.attr("abs:href").trim(); 
										if(hs.contains(url)) continue;
										else{
											hs.add(url);
											linkto = linkto + "\t" + url;
										}
									}
									
									linkto = docid + "\t" + linkto;//homepageID + a set of linked pages
									link_graph.append(linkto + "\n");//write links graph
									
									urltable.append(docid+" "+homeurl+" "+length+"\n");//write url table into file
									String content = doc.text();
									Parser p = new Parser();
									sb_page.delete(0, sb_page.length());
									p.parsePage(homeurl, content, sb_page);
									f_and_pos = sb_page.toString().split("\n");//all frequency and positions of words of a page
									
									for(int k = 0;k < f_and_pos.length; k++){
										try{
											String[] unit = f_and_pos[k].split("\\s+");
											posting temp = new posting(Integer.parseInt(unit[1]), docid, unit[0]);
											if (max < number) {
												pos[max] = temp;
												max++;
												pointer++;
											}
											else{//the amount of postings in memeory is more than a certain number
												//merge  postings and write to a file		
												Arrays.sort(pos);
												clean(pos, String.valueOf(tempory), number);
												tempory++;
												max=0;
												pointer = 0;
												pos = new posting[number];
												pos[max] = temp;
											}
										}catch (Exception ee){
											//System.out.println(e);
										}
									}
									docid++;
								}
								if(flag == 2) break;//go to next webpage
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Arrays.sort(pos, 0, pointer);
		try {
			clean(pos, String.valueOf(tempory), pointer);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		link_graph.close();
		urltable.close();
	}

}

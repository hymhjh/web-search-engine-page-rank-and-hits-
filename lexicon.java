package pagerank;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

public class lexicon {
	//use vbyte to compress
	public static int encode(DataOutputStream out, int value) throws IOException {
		int re = 1;
        while( value > 127) {
        	re++;
            out.writeByte(value & 127);
            value>>>=7;
        }
        out.writeByte(value|0x80);
        return re;
    }
	
	//This class is to construct the lexicon structure and inverted list.
	@SuppressWarnings({ "resource" })
	public static void structure(File f) throws FileNotFoundException, IOException{
		
		BufferedWriter writerlexicon = null;
		//file for invertedindex
		DataOutputStream zipIndex = new DataOutputStream (new BufferedOutputStream(new FileOutputStream(new File("invertedIndex"))));	        
		
	    //file for lexicon
	    DataOutputStream ziplexicon = new DataOutputStream(new FileOutputStream(new File("lexicon")));	 
	    writerlexicon = new BufferedWriter(new OutputStreamWriter(ziplexicon, "UTF-8"));
		try {
            InputStream input = new DataInputStream(new FileInputStream( f ));
            Reader decoder = new InputStreamReader(input, "utf-8");
            BufferedReader br = new BufferedReader(decoder);
            String s = "";
            long pointer = 0;
            while((s = br.readLine()) != null) {
                String[] pos = s.split("\\s+");
                int length = (pos.length - 1)/2;      //number of pages
                //if(pos[0].length() > 24 || length == 1) continue;//if this word over 24 characters and occur only once, discard it
                int lastdoc = Integer.parseInt(pos[1]), l = 0;
                l = encode(zipIndex, Integer.parseInt(pos[1]));
                for(int k = 2; k < pos.length; k++){   //compress docid
                	if(k % 2 == 0) {
                		int re = encode(zipIndex,Integer.parseInt(pos[k]));//frequency binary format
                		l += re;
                		}
                	else {
                		int docid = Integer.parseInt(pos[k])-lastdoc;
                		int re = encode(zipIndex,docid);  //docid binary format
                		l += re;
                		lastdoc = Integer.parseInt(pos[k]);
                	}
                }
                System.out.println(pos[0]+" "+length);
                writerlexicon.write(pos[0] + " " + length + " " + pointer + "\n");
                pointer = pointer + (long)l;
             }
            writerlexicon.close();
            zipIndex.close();
        } catch( FileNotFoundException e2 ) {
        	System.out.println(e2);
        }
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException{
		File f = new File("/Users/huangyiman/workplace/pagerank/final.txt");
		structure(f);
	}
}


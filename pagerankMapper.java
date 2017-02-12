import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class pagerankMapper extends Mapper<LongWritable,Text,Text,Text>{
	private static final int MISSING = 9999;
	
	public void map(LongWritable key, Text value, Context context)throws IOException, InterruptedException{
		String line = value.toString();
		String[] link = line.split("\\s+");
		String page = link[0];
		String pr = link[link.length-1]; //get the pr value string
        try{
            if(link.length > 2){
                int temp = Integer.parseInt(link[1]);
                context.write(new Text(page), new Text(pr));
            }else{
                context.write(new Text(page), new Text(pr));
            }
        }catch(Exception e){
            context.write(new Text(page), new Text(link[1]+" "+link[2]));
        }
				
	}
}

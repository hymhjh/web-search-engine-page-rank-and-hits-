import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class pagerankReducer extends Reducer<Text,Text,Text,Text>{
	public void reduce(Text key, Iterable<Text> values, Context context)throws IOException, InterruptedException{
		String pr = "";
		String url = "";
		for(Text value : values){
			String s = value.toString();
            String[] sp = s.split("\\s+");
            if(sp.length == 1) {
                pr = sp[0];
            }else{
                url = s;
            }
			
		}
        if(!url.equals("")) {
            if(pr.equals("")){
                context.write(key, new Text(url+" "+0.15));
            }else{
                context.write(key, new Text(url+" "+pr));
            }
        }
	}
}

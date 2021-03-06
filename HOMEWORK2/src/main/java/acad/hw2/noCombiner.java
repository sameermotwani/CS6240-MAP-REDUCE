package acad.hw2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.BasicConfigurator;

public class noCombiner {

	public static class noCombinerMapper
	extends Mapper<Object, Text, Text, Text>{

		private final static IntWritable one = new IntWritable(1);
		private Text stationID = new Text();
		private Text values = new Text();

		public void map(Object key, Text value, Context context
				) throws IOException, InterruptedException {
			String line=value.toString();
			String[] vals=line.split(",");
			String type=vals[2];
			if(type.equals("TMAX")|| type.equals("TMIN"))
			{
				String valToReduce=type+","+vals[3];
				stationID.set(vals[0]);
				values.set(valToReduce);
				context.write(stationID, values);
			}
		}
	}

  public static class noCombinerReducer
       extends Reducer<Text,Text,Text,Text> {
    private Text result = new Text();

    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int countTmax = 0;
      int countTmin= 0;
      double sumTmax=0;
      double sumTmin=0;
      double meanTmax=0;
      double meanTmin=0;
      double random=11;
      
      
      for (Text val : values) {
    	  String[] vals=val.toString().split(",");
    	  String type=vals[0];
    	   Double tempVal=Double.parseDouble(vals[1]);
    	   System.out.println(type);
    	   System.out.println(tempVal);
    	  if(type.equals("TMAX"))
    	  {
    		  countTmax+=1;
    		  sumTmax+=tempVal;
    	  }
    	  else
    	  {
    		  countTmin+=1;
    		  sumTmin+=tempVal;
    	  }
        
      }
      
      if(countTmax==0 && sumTmax==0)
      {
    	  meanTmax=random;
      }
      else
      {
    	  meanTmax=sumTmax/countTmax;
      }
      
      
      if(countTmin==0 && sumTmin==0)
      {
    	  meanTmin=random;
      }
      else
      {
    	  meanTmin=sumTmin/countTmin;
      }

      String finalVal;
      
      if(meanTmax==random && meanTmin==random)
      {
    	  finalVal="NULL"+","+"NULL";
      }
      else if (meanTmin==random)
      {
    	  finalVal="NULL"+","+Double.toString(meanTmax);
      }
      else if (meanTmax==random)
      {
    	  finalVal=Double.toString(meanTmin)+","+"NULL";
      }
      else 
      {
    	  finalVal=Double.toString(meanTmin)+","+Double.toString(meanTmax);
      }
      
      
      result.set(finalVal);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
	  BasicConfigurator.configure();
    Configuration conf = new Configuration();
    conf.set("mapreduce.output.textoutputformat.separator", ",");
    Job job = Job.getInstance(conf, "noCombiner");
    job.setJarByClass(noCombiner.class);
    job.setMapperClass(noCombinerMapper.class);
    job.setReducerClass(noCombinerReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}

package acad.hw2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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

public class inMapCombiner {

	
	public static class tempData
	{
		int countTmax = 0;
		int countTmin= 0;
		double sumTmax=0;
		double sumTmin=0;
		
		public tempData(String type,double Val,double count) {
			  if(type.equals("TMAX"))
		 	  {
		 		  countTmax+=count;
		 		  sumTmax+=Val;
		 	  }
		 	  else
		 	  {
		 		  countTmin+=count;
		 		  sumTmin+=Val;
		 	  }
		}
		
		
	}

	public static class withCombinerMapper
	extends Mapper<Object, Text, Text, Text>{

		private Map<String,tempData> tMap;
		@Override
		protected void setup(Mapper<Object, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			 tMap=new HashMap<String,tempData>();
		}
		
		private final static IntWritable one = new IntWritable(1);
		private Text stationID = new Text();
		private Text values = new Text();

		
		
		protected void map(Object key, Text value, Context context
				) throws IOException, InterruptedException {
			String line=value.toString();
			String[] vals=line.split(",");
			String type=vals[2];
			double count=1;
			if(type.equals("TMAX")|| type.equals("TMIN"))
			{
				
				if(!tMap.containsKey(vals[0]))
				{
					tMap.put(vals[0],new tempData(type, Double.parseDouble(vals[3]), count));
				}
				else
				{
					tempData td=tMap.get(vals[0]);
					if(type.equals("TMAX"))
				 	  {
				 		  td.countTmax+=count;
				 		  td.sumTmax+=Double.parseDouble(vals[3]);
				 	  }
				 	  else
				 	  {
				 		  td.countTmin+=count;
				 		  td.sumTmin+=Double.parseDouble(vals[3]);
				 	  }
				}
			}
		}
		
		@Override
		protected void cleanup(Mapper<Object, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			Iterator itr=tMap.entrySet().iterator();
			
			while(itr.hasNext())
			{
				Map.Entry Pair=(Map.Entry) itr.next();
				String ID=Pair.getKey().toString();
				tempData res=(tempData) Pair.getValue();
				stationID.set(ID);
				String result=res.sumTmax+","+res.countTmax+","+res.sumTmin+","+res.countTmin;
				values.set(result);
				context.write(stationID, values);
			}
		}
		
	}
	

	
  public static class inMapperReducer
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
     	  
     	  sumTmax+=Double.parseDouble(vals[0]);
     	  countTmax+=Integer.parseInt(vals[1]);
     	  sumTmin+=Double.parseDouble(vals[2]);
    	  countTmin+=Integer.parseInt(vals[3]); 
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
    Job job = Job.getInstance(conf, "inMapCombiner");
    job.setJarByClass(inMapCombiner.class);
    job.setMapperClass(withCombinerMapper.class);
    //job.setCombinerClass(combinerClass.class);
    job.setReducerClass(inMapperReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}

package acad.hw2;

import java.io.DataInput;   
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.BasicConfigurator;

public class secondarySort {
	
	 public static void main(String[] args) throws Exception {
		  BasicConfigurator.configure();
	    Configuration conf = new Configuration();
	    //conf.set("mapreduce.output.textoutputformat.separator", ",");
	    Job job = Job.getInstance(conf, "secondarySort");
	    job.setJarByClass(secondarySort.class);
	    //job.setPartitionerClass(keyPartitioner.class);
	    //job.setSortComparatorClass(compositeKeyComparator.class);
	    job.setGroupingComparatorClass(KeyGroup.class);
	    job.setMapperClass(secondarySortMapper.class);
	    //job.setCombinerClass(combinerClass.class);
	    job.setReducerClass(secondarySortReducer.class);
	    job.setMapOutputKeyClass(compositeKey.class);
	    job.setMapOutputValueClass(Text.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(NullWritable.class);
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	  }
	}


class compositeKey implements WritableComparable<compositeKey>
	{
		public String stationID;
		public int year;
		public compositeKey() {
			// TODO Auto-generated constructor stub
		}

		public compositeKey(String stationID, int year) {
			super();
			this.stationID = stationID;
			this.year = year;
		}

		

		public String getStationID() {
			return stationID;
		}


		public int getYear() {
			return year;
		}


		
		
		public void write(DataOutput out) throws IOException {
			out.writeUTF(stationID);
			out.writeInt(year);
		}

		public void readFields(DataInput in) throws IOException {
			this.stationID = in.readUTF();
			this.year = in.readInt();
		}

		public int compareTo(compositeKey k2) {
			// TODO Auto-generated method stub
			
			int result=stationID.compareTo(k2.stationID);
			if(result==0)
			{
				if(year>k2.year)
				{
					result = 1;
				}
				else if (year==k2.year) {
					result = 0;
				}
				else {
					result = -1;
				}
			}
			
			return result;
		}

	}

	
	
	class KeyGroup extends WritableComparator{

		public KeyGroup() {
			super(compositeKey.class, true);
		}

		@SuppressWarnings("rawtypes")
		public int compare(WritableComparable wc1, WritableComparable wc2){
			compositeKey i1 = (compositeKey) wc1;
			compositeKey i2 = (compositeKey) wc2;

			return i1.getStationID().compareTo(i2.getStationID());
		}
	}
		
	
	
 class tempData
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
	
	
	
	
	
	
	
class secondarySortMapper
	extends Mapper<Object, Text, compositeKey, Text>{

		private Map<compositeKey, tempData> tMap;
		@Override
		protected void setup(Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			 tMap=new HashMap<compositeKey,tempData>();
		}
		
		private Text values = new Text();

		
		
		protected void map(Object key, Text value, Context context
				) throws IOException, InterruptedException {
			String line=value.toString();
			String[] vals=line.split(",");
			String type=vals[2];
			String id=vals[0];
			int year=Integer.parseInt(vals[1].substring(0, 4));
			System.out.println(year);
			double count=1;
			double temp=Double.parseDouble(vals[3]);
			System.out.println(temp);
			if(type.equals("TMAX")|| type.equals("TMIN"))
			{
				
				compositeKey objKey=new compositeKey(id, year);
				
				
				if(tMap.get(objKey) == null)
				{
					tMap.put(objKey,new tempData(type, temp, count));
				}
				else
				{
					tempData td=tMap.get(objKey);
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
		protected void cleanup(Context context)
				throws IOException, InterruptedException {
			@SuppressWarnings("rawtypes")
			Iterator itr = tMap.entrySet().iterator();

			while(itr.hasNext()){
				@SuppressWarnings("rawtypes")
				Map.Entry pair = (Map.Entry)itr.next();
				compositeKey id = (compositeKey) pair.getKey();
				tempData result = (tempData) pair.getValue();

				context.write(id, new Text(result.sumTmin + "," + result.countTmin + "," + result.sumTmax + "," + result.countTmax));

			}
			
		
	}
}
	

	
  class secondarySortReducer
       extends Reducer<compositeKey,Text,Text,NullWritable> {
     Text result = new Text();

    public void reduce(compositeKey key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int countTmax = 0;
      int countTmin= 0;
      double sumTmax=0;
      double sumTmin=0;
      double meanTmax=0;
      double meanTmin=0;
      double random=11;
      System.out.println("IN REDUCER");
     System.out.println(key.getStationID());
     //System.out.println(key.getYear());
      StringBuilder st=new StringBuilder();
      int currentYear=key.getYear();
      int prevYear=currentYear;
      
      
      
      for (Text val : values) {
    	  prevYear=currentYear;
    	  currentYear=key.getYear();
     	  String[] vals=val.toString().split(",");
     	  System.out.println("sumTMAX"+sumTmax);
     	  
  
    	  if(currentYear!=prevYear)
    	  {
    		  st.append("(");
    		  st.append(prevYear +",");
    		  st.append((countTmin==0) ? "NULL" :String.format("%.2f", sumTmin/countTmin));
    		  st.append(", ");
    		  st.append((countTmax==0) ? "NULL" :String.format("%.2f", sumTmax/countTmax));
    		  st.append(")");
    		  st.append(", ");
    		  
    		  sumTmax = 0; sumTmin = 0;
			  countTmax = 0; countTmin = 0;
    	  }
    	  
    	  sumTmin+=Double.parseDouble(vals[0]);
     	  countTmin+=Integer.parseInt(vals[1]);
     	  sumTmax+=Double.parseDouble(vals[2]);
    	  countTmax+=Integer.parseInt(vals[3]); 
    	  
      }
      
      if(currentYear==prevYear)
      {
    	  st.append("(");
		  st.append(prevYear +",");
		  st.append((countTmin==0) ? "NULL" :String.format("%.2f", sumTmin/countTmin));
		  st.append(", ");
		  st.append((countTmax==0) ? "NULL" :String.format("%.2f", sumTmax/countTmax));
		  st.append(")");
      }
      
      //result.set(finalVal);
      context.write(new Text(key.stationID +" "+ "[" + st +"]"), null);
    }
  }

 
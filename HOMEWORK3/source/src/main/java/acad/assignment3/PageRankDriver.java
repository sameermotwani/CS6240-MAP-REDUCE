package acad.assignment3;
import org.apache.hadoop.conf.Configuration; 
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.log4j.BasicConfigurator;

import acad.assignment3.PRClass.PageRankMapper;
import acad.assignment3.PRClass.PageRankReducer;
import acad.assignment3.PageSort.OutputMapper;
import acad.assignment3.PageSort.OutputReducer;

public class PageRankDriver {

	private static long totalPages;
	private static long df = 0L;

	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

		Path input;
		Path outputGraph;

		input = new Path(otherArgs[0]);
		outputGraph = new Path(otherArgs[1]);
		String op=otherArgs[1];

		// call for first Map-Reduce Job
		readData(conf, input, outputGraph);

		// call to Second Map-Reduce Job which is run 10 times
		int i = 0;
		@SuppressWarnings("unused")
		boolean done = false;
		while (i < 10) {
			done = getPageRank(conf, i, input,op);
			i++;
		}
		
		sortOutput(conf, input,op);

	}
	
	
	
	public static boolean getPageRank(Configuration conf, int i, Path p,String op) throws Exception{

		conf.setLong("noOfPages", totalPages);
		conf.setInt("iterator", i);
		conf.setLong("dandlingfactor", df); //dangling factor
		StringBuilder input = new StringBuilder();
		StringBuilder output = new StringBuilder();

		Job job = Job.getInstance(conf, "pageRank iterator");
		job.setJarByClass(PageRankDriver.class);
		job.setMapperClass(PageRankMapper.class);
		job.setReducerClass(PageRankReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(OutputData.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
		String in;
		if(i==0){
		 in = op;
		}
		else 
			in = op+"/PageRank-" + (i-1);
	
		String out = op+"/PageRank-" + i;
		FileInputFormat.addInputPath(job, new Path(in));
		FileOutputFormat.setOutputPath(job, new Path(out));

		job.waitForCompletion(true);

		df = job.getCounters().findCounter(CountData.DanglingFactor).getValue();
		totalPages = job.getCounters().findCounter(CountData.NumberOfPages).getValue();

		return false;


	}

	public static void readData(Configuration conf, Path input, Path outputGraph) throws Exception{
		BasicConfigurator.configure();
		Job job = Job.getInstance(conf, "input");
		job.setJarByClass(PageRankDriver.class);
		job.setMapperClass(IpParserMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);

		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, outputGraph);
		job.waitForCompletion(true);
		totalPages = job.getCounters().findCounter(CountData.NumberOfPages).getValue();
		System.out.println(totalPages);

	
	}
	

	public static void sortOutput(Configuration conf, Path input,String op) throws Exception{
		Job job = Job.getInstance(conf, "pageRank output");
		job.setJarByClass(PageRankDriver.class);
		job.setMapperClass(OutputMapper.class);
		job.setReducerClass(OutputReducer.class);
		job.setNumReduceTasks(1);
		job.setSortComparatorClass(PageSortComparator.class);
		job.setMapOutputKeyClass(DoubleWritable.class);
		job.setMapOutputValueClass(Text.class);

		StringBuilder in = new StringBuilder();
		StringBuilder out3 = new StringBuilder();

		String in1 = op+"/PageRank-9";

		FileInputFormat.setInputPaths(job, new Path(in1));
		
		String out1 = op+"/final";
		FileOutputFormat.setOutputPath(job, new Path(out1));

		job.waitForCompletion(true);
	}
	}


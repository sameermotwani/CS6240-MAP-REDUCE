package acad.assignment3;
import java.io.IOException;
import java.util.HashSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;


public class PRClass {



	/* Mapper reads the output of the first job which is a list of lines
	 * comprising the "Page->(list of outlinkPages)->PageRank". Map emits 2
	 * things 
	 * 
	 * 1) Page as the key and the list of outlinks as value(which is basically
	 * a GraphData object, with Boolean value as false and the list as String)
	 * 
	 * 2) Emits each page from the input outlink list and a GraphData object as a value
	 * comprising of Boolean as true, the Page rank for that page and the number of
	 * outlink count from that page 
	 * */
	public static class PageRankMapper extends Mapper<Object, Text, Text, OutputData>{

		Integer itr;
		long totalPages;
		final long DANGLING_FACTOR_MULTIPLIER = 100000000000L;

		/* setting up the itr from configuration and totalPages from the
		 * configuration */
		@Override
		protected void setup(Context context)
				throws IOException, InterruptedException {
			itr = context.getConfiguration().getInt("iterator", -1);
			totalPages = Long.parseLong(context.getConfiguration().get("noOfPages"));
		}
		

		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			double pageRVal = 0.0;

			String line = value.toString();
			String lineSplit[] = line.trim().split("->");		

			@SuppressWarnings("unused")
			String pages = lineSplit[1];
			String pageSplit = lineSplit[1].trim();

			String outlinks[] = null;
			if(!(pageSplit.length() == 0)){
				outlinks = pageSplit.split(",");
			}

			// for the first iteration set page Rank as 1/totalNoOfPages
			if( itr == 0){
				pageRVal = 1.0 / totalPages;
				System.out.println("ITERATORR" +itr+" "+pageRVal);
				
			}
			else{
				System.out.println("ITERATORR" +itr);
				pageRVal = Double.parseDouble(lineSplit[2]);
			}

			// checking if outlink list is empty to set the Dangling Factor counter
			if(outlinks == null){
				context.getCounter(CountData.DanglingFactor).increment((long)((Double.parseDouble(lineSplit[2])) * DANGLING_FACTOR_MULTIPLIER));
			}
			else {
				for(String page: outlinks){
					context.write(new Text(page), new OutputData(true, new Double(pageRVal), new Long(outlinks.length)));
				}
			}
			context.write(new Text(lineSplit[0]), new OutputData(false, lineSplit[1]));


		}

	}
	
	
	

/* Input to the Reducer is Text as key and GraphData as Value and it emits Text as
 * the key and NullWritable as value. The reduce function computes the page rank and
 * emits the text same as the input text with the updated page rank value*/
public static class PageRankReducer extends Reducer<Text, OutputData, Text, NullWritable>{

	private static long pageCount;
	private static long danglingFactor;
	final long DANGLING_FACTOR_MULTIPLIER = 100000000000L;
	HashSet<String> pageList;

	/* setting up the danglingFactor from configuration, pageCount from configuration
	 * and using a HashSet to store all the pages to get the pageCount*/
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		danglingFactor = Long.parseLong(context.getConfiguration().get("dandlingfactor")) / DANGLING_FACTOR_MULTIPLIER;
		pageCount = Long.parseLong(context.getConfiguration().get("noOfPages"));
		pageList = new HashSet<String>();

	}

	@Override
	protected void reduce(Text key, Iterable<OutputData> values, Context context)
			throws IOException, InterruptedException {

		String outlinkList = "";

		double prByCount = 0.0;
		double pageRank = 0.0;
		double alpha = 0.15;
		double randomJump = 0.0;
		double followLink = 0.0;
		StringBuilder output = new StringBuilder();

		for(OutputData d : values){
			if(!d.getIsPRdata()){
				outlinkList = d.getOutlinkList();
			}
			else{
				prByCount += d.getPageRVal()/ d.getOutlinkCount();
			}
		}

		
		randomJump = alpha / pageCount;

		followLink = (1 - alpha) * ((danglingFactor/pageCount) + prByCount);

		pageRank = randomJump + followLink;

		output.append(key.toString());
		output.append("->");
		output.append(outlinkList);
		output.append("->");
		output.append(pageRank);
		pageList.add(key.toString());

		context.write(new Text(output.toString()), NullWritable.get());
	}

	/* setting up the NumberOfPages counter based on the size of the HashSet which
	 * is used to store all the pages */
	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		long count = pageList.size();
		context.getCounter(CountData.NumberOfPages).setValue(count);
	}

}



	
	
	
}



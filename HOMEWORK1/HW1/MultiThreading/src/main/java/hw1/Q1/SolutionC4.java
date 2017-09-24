package hw1.Q1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.AllPermission;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



class SolutionC4Thread implements Runnable
{

	private List<String> Input;
	public ConcurrentHashMap<String,Float[]> allTmaxRecords;

	public SolutionC4Thread(List<String> Input, ConcurrentHashMap<String,Float[]> TotalTmaxPerStation){
		this.Input=Input;
		this.allTmaxRecords=TotalTmaxPerStation;
	}

	public void run() {
		// TODO Auto-generated method stub

		
			for (String ip : Input) {
				String[] currentRecord=ip.split(",");
				String stationID=currentRecord[0];
				String type=currentRecord[2];
				Integer reading=Integer.parseInt(currentRecord[3]);
				Integer count=1;

				if (!type.equals("TMAX") || type == null ) {
					continue;
				} else {
					//System.out.println("HERE");

					if (!allTmaxRecords.containsKey(stationID)){
						
						Float[] inpVal=new Float[2];
						inpVal[0]=(float)reading;
						inpVal[1]=(float)count;
						synchronized (inpVal) {
						//System.out.println(inpVal[1]);
						allTmaxRecords.put(stationID, inpVal);
						}
					}
					else {
						
							Float[] inpVal=allTmaxRecords.get(stationID);
							synchronized (inpVal) {
							inpVal[0]=inpVal[0]+reading;
							inpVal[1]=inpVal[1]+count;
							}
							allTmaxRecords.put(stationID, inpVal);
							fibonacci(17);
						
					
					}
				}	
			}
	}
	
	public int fibonacci(int n)  {
	    if(n == 0)
	        return 0;
	    else if(n == 1)
	      return 1;
	   else
	      return fibonacci(n - 1) + fibonacci(n - 2);
	}
}




















public class SolutionC4
{




	public static void main(String[] args) throws IOException, InterruptedException {


		List<String> Input=sourceloader.readFile(args[0]);
		ConcurrentHashMap<String, Float[]> allTmaxRecords=new ConcurrentHashMap<String, Float[]>();
		Map<String, Float> allTmaxAvgRecords=new HashMap<String, Float>();

		int inpSize=Input.size();
		List<String> Input1=Input.subList(0, (inpSize/4));
		List<String> Input2=Input.subList((inpSize/4), (inpSize/2));
		List<String> Input3=Input.subList((inpSize/2), (inpSize*3/4));
		List<String> Input4=Input.subList((inpSize*3/4), inpSize);


		// TODO Auto-generated method stub
		float minTime=0;
		float maxTime=0;
		float avgTime=0;
		float totalTime=0;

		for (int i = 0; i < 10; i++) {
			//System.out.println("Loop:"+i);

			Thread t1=new Thread(new SolutionC4Thread(Input1,allTmaxRecords));
			Thread t2=new Thread(new SolutionC4Thread(Input2,allTmaxRecords));
			Thread t3=new Thread(new SolutionC4Thread(Input3,allTmaxRecords));
			Thread t4=new Thread(new SolutionC4Thread(Input4,allTmaxRecords));

			long startTime = System.currentTimeMillis();
			t1.start();
			t2.start();
			t3.start();
			t4.start();

			t1.join();
			t2.join();
			t3.join();
			t4.join();


			for (String str : allTmaxRecords.keySet()) {
				Float[] finalVal=allTmaxRecords.get(str);
				float finalAvg= finalVal[0]/finalVal[1];
				allTmaxAvgRecords.put(str, finalAvg);
			}
			//System.out.println("FINAL: "+allTmaxRecords.size());

			long endTime = System.currentTimeMillis();

			long timeToFinish = endTime-startTime;
			totalTime=totalTime+timeToFinish;

			if (i==1) {
				minTime=timeToFinish;
				maxTime=timeToFinish;
			}
			else {
				if (minTime>timeToFinish) {
					minTime=timeToFinish;
				}
				if (maxTime<timeToFinish) {
					maxTime=timeToFinish;
				}
			}

		}

		File dir=new File("output");
		if(!dir.exists())
		{
			new File("output").mkdir();
		}
		File op=new File("output/C4Output.csv");
		FileWriter fw=new FileWriter(op.getAbsoluteFile());
		BufferedWriter bw=new BufferedWriter(fw);

		for (String stationID : allTmaxAvgRecords.keySet()) {
			//System.out.println(stationID);
			String str=stationID + "," + allTmaxAvgRecords.get(stationID) + "\n";
			bw.write(str);
		}
		bw.close();

		allTmaxAvgRecords.clear();
		allTmaxRecords.clear();
		avgTime=totalTime/10;

		System.out.println("Minimum Time :" +minTime);
		System.out.println("Average Time :" +avgTime);
		System.out.println("Maximum Time :" +maxTime);

	}

}

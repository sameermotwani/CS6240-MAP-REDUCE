package hw1.Q1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.AllPermission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


class SolutionC5Thread implements Runnable
{
	private List<String> Input;
	String threadName;
	public Map<String,Float[]> allTmaxRecords=new HashMap<String, Float[]>();

	public SolutionC5Thread(List<String> Input, String Thread){
		this.Input=Input;
		this.threadName=Thread;
	}

	public void run() {
		// TODO Auto-generated method stub
			//System.out.println("Thread Starting: "+threadName);
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
							allTmaxRecords.put(stationID, inpVal);
						//System.out.println(inpVal[1]);
						
						
					}
					else {
						
							Float[] inpVal=allTmaxRecords.get(stationID);
								inpVal[0]=inpVal[0]+reading;
								inpVal[1]=inpVal[1]+count;
							allTmaxRecords.put(stationID, inpVal);
							
							fibonacci(17);
					
					}
				}	
			}
			//System.out.println("Thread Ending: "+threadName);
	}
	
	public int fibonacci(int n)  {
	    if(n == 0)
	        return 0;
	    else if(n == 1)
	      return 1;
	   else
	      return fibonacci(n - 1) + fibonacci(n - 2);
	}
	
	public Map<String,Float[]> getMap(){
		return allTmaxRecords;
		
	}
}


public class SolutionC5
{
	public static void main(String[] args) throws IOException, InterruptedException {


		List<String> Input=sourceloader.readFile(args[0]);
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
		SolutionC5Thread sb1=new SolutionC5Thread(Input1,"Thread1");
		SolutionC5Thread sb2=new SolutionC5Thread(Input2,"Thread2");
		SolutionC5Thread sb3=new SolutionC5Thread(Input3,"Thread3");
		SolutionC5Thread sb4=new SolutionC5Thread(Input4,"Thread4");
		
			Thread t1=new Thread(sb1);
			Thread t2=new Thread(sb2);
			Thread t3=new Thread(sb3);
			Thread t4=new Thread(sb4);

			long startTime = System.currentTimeMillis();
			t1.start();
			t2.start();
			t3.start();
			t4.start();
			

			t1.join();
			t2.join();
			t3.join();
			t4.join();

			
			HashSet<String> allStationID=new HashSet<String>();
			
			allStationID.addAll(sb1.allTmaxRecords.keySet());
			allStationID.addAll(sb2.allTmaxRecords.keySet());
			allStationID.addAll(sb3.allTmaxRecords.keySet());
			allStationID.addAll(sb4.allTmaxRecords.keySet());
			
			for (String stationID : allStationID) {
				float finalSum=0;
				int finalCount=0;
				
				if(sb1.allTmaxRecords.containsKey(stationID))
				{
					finalSum=finalSum+sb1.allTmaxRecords.get(stationID)[0];
					finalCount=(int) (finalCount+sb1.allTmaxRecords.get(stationID)[1]);
				}
				if(sb2.allTmaxRecords.containsKey(stationID))
				{
					finalSum=finalSum+sb2.allTmaxRecords.get(stationID)[0];
					finalCount=(int) (finalCount+sb2.allTmaxRecords.get(stationID)[1]);
				}
				if(sb3.allTmaxRecords.containsKey(stationID))
				{
					finalSum=finalSum+sb3.allTmaxRecords.get(stationID)[0];
					finalCount=(int) (finalCount+sb3.allTmaxRecords.get(stationID)[1]);	
				}
				if(sb4.allTmaxRecords.containsKey(stationID))
				{
					finalSum=finalSum+sb4.allTmaxRecords.get(stationID)[0];
					finalCount=(int) (finalCount+sb4.allTmaxRecords.get(stationID)[1]);
				}
					
				allTmaxAvgRecords.put(stationID, finalSum/finalCount);
			}


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
		File op=new File("output/C5Output.csv");
		FileWriter fw=new FileWriter(op.getAbsoluteFile());
		BufferedWriter bw=new BufferedWriter(fw);

		for (String stationID : allTmaxAvgRecords.keySet()) {
			//System.out.println(stationID);
			String str=stationID + "," + allTmaxAvgRecords.get(stationID) + "\n";
			bw.write(str);
		}
		bw.close();

		allTmaxAvgRecords.clear();
		avgTime=totalTime/10;

		System.out.println("Minimum Time :" +minTime);
		System.out.println("Average Time :" +avgTime);
		System.out.println("Maximum Time :" +maxTime);

	}

}

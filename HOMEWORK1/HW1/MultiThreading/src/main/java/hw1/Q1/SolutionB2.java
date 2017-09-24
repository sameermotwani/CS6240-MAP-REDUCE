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



 class SolutionB2Thread implements Runnable
{

	private List<String> Input;
	private Map<String,Float[]> allTmaxRecords;
	
	public SolutionB2Thread(List<String> Input, Map<String,Float[]> TotalTmaxPerStation){
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
						//System.out.println(inpVal[1]);
						allTmaxRecords.put(stationID, inpVal);
						
					}
					else {
						Float[] inpVal=allTmaxRecords.get(stationID);
						inpVal[0]=inpVal[0]+reading;
						inpVal[1]=inpVal[1]+1;
						allTmaxRecords.put(stationID, inpVal);
					}
				}
				
				
				
			}
			

		
	}

	
}




















public class SolutionB2
{
	
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
	 
		
		List<String> Input=sourceloader.readFile(args[0]);
		Map<String, Float[]> allTmaxRecords=new HashMap<String, Float[]>();
		Map<String, Float> allTmaxAvgRecords=new HashMap<String, Float>();
		
		
		
		
		// TODO Auto-generated method stub
		float minTime=0;
		float maxTime=0;
		float avgTime=0;
		float totalTime=0;
		
		int inpSize=Input.size();
		List<String> Input1=Input.subList(0, (int)inpSize/4);
		List<String> Input2=Input.subList((int)inpSize/4, (int)inpSize/2);
		List<String> Input3=Input.subList((int)inpSize/2, (int)3*inpSize/4);
		List<String> Input4=Input.subList((int)3*inpSize/4, inpSize);
		
		for (int i = 0; i < 10; i++) {
			//System.out.println("Loop:"+i);
			Thread t1=new Thread(new SolutionB2Thread(Input1,allTmaxRecords));
			Thread t2=new Thread(new SolutionB2Thread(Input2,allTmaxRecords));
			Thread t3=new Thread(new SolutionB2Thread(Input3,allTmaxRecords));
			Thread t4=new Thread(new SolutionB2Thread(Input4,allTmaxRecords));
			
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
			
			long endTime = System.currentTimeMillis();
		//System.out.println("FINAL: "+allTmaxRecords.size());
			
			
			
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
		File op=new File("output/B2Output.csv");
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

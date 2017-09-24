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

public class SolutionB1 {
	
	private List<String> Input;
	private Map<String, Float[]> allTmaxRecords=new HashMap<String, Float[]>();
	private Map<String, Float> allTmaxAvgRecords=new HashMap<String, Float>();
	
	public SolutionB1(String Fp) throws IOException {
		// TODO Auto-generated constructor stub
		 Input=sourceloader.readFile(Fp);
		 //System.out.println(Input.size());
	}
	
	
	public void getAverageTemperature() {
		
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
					inpVal[1]=inpVal[1]+count;
					allTmaxRecords.put(stationID, inpVal);
				}
			}
			
			
			
		}
		
		for (String str : allTmaxRecords.keySet()) {
			Float[] finalVal=allTmaxRecords.get(str);
			float finalAvg= finalVal[0]/finalVal[1];
			allTmaxAvgRecords.put(str, finalAvg);
		}
		//System.out.println("FINAL: "+allTmaxRecords.size());
	}
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		float minTime=0;
		float maxTime=0;
		float avgTime=0;
		float totalTime=0;
		
		SolutionB1 sb1=new SolutionB1(args[0]);
		
		for (int i = 0; i < 10; i++) {
			//System.out.println("Loop:"+i);
			
			long startTime = System.currentTimeMillis();
			sb1.getAverageTemperature();
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
		File op=new File("output/B1Output.csv");
		FileWriter fw=new FileWriter(op.getAbsoluteFile());
		BufferedWriter bw=new BufferedWriter(fw);
		
		for (String stationID : sb1.allTmaxAvgRecords.keySet()) {
			//System.out.println(stationID);
			String str=stationID + "," + sb1.allTmaxAvgRecords.get(stationID) + "\n";
			bw.write(str);
		}
		bw.close();
		
		sb1.allTmaxAvgRecords.clear();
		sb1.allTmaxRecords.clear();
		avgTime=totalTime/10;
		
		System.out.println("Minimum Time :" +minTime);
		System.out.println("Average Time :" +avgTime);
		System.out.println("Maximum Time :" +maxTime);
		
	}

}

/**
 * 
 */
package hw1.Q1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author samym
 *
 */


public class sourceloader {

	/**
	 * @param args
	 * @return 
	 * @throws IOException 
	 * 
	 */
	
	public static List<String> readFile(String Fp) throws IOException
	{
		int count = 0;
		List<String> str=new ArrayList<String>();
		try {
			BufferedReader br=new BufferedReader(new FileReader(Fp));
			String line;
			
			try {
				while((line=br.readLine())!=null) {
					str.add(line);
					count++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(count);
		return str;
		
	}
	
	
//	public static void main(String[] args) throws IOException {
//		// TODO Auto-generated method stub
//
//		sourceloader src=new sourceloader();
//		src.readFile("C:\\Users\\samym\\Desktop\\ip\\1763.csv");
//	}	

}
 
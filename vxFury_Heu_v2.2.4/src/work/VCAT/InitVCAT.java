package work.VCAT;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author vxFury
 *
 */
public class InitVCAT {
	public static void main(String[] args) {
		HeuVCAT heu = new HeuVCAT();
		
		String dataPath = "./data/";
		
		String[] netName = { "usnet" };// { "n6s8", "smallnet", "cost239", "nsfnet",  "usnet" };
		
		int demand[] = {100,500,1000,1500,2000,2500,3000,3500,4000,4500,5000};
		
		int k = 3;
		
		for (String net : netName) {
			long time = System.currentTimeMillis();
			SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
			System.out.println("********************************************************************************");
			System.out.println("Topology File : " + net + ".csv\r\n" + date.format(new Date()) + "\r\n");
			
			for(int tmp : demand){
				System.out.println("Demand(fixed) for each nodepair is " + tmp);
				heu.availabilityEnhancing_VCAT(dataPath,net,k,tmp,tmp);
			}
			
			System.out.println(date.format(new Date()));
			System.out.println("End of analysis : " + (net + ".csv"));
			time = System.currentTimeMillis() - time;
			System.out.println("Time-consuming(Total) : " + time + " ms");
			System.out.println("********************************************************************************\r\n");
		}
	}
}
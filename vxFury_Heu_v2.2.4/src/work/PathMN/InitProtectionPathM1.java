package work.PathMN;

import java.text.SimpleDateFormat;
import java.util.Date;

import work.utilities.Logger;

/**
 * @author vxFury
 *
 */
public class InitProtectionPathM1 {
	public static void main(String[] args) {
		HeuProtectionPathM1 heu = new HeuProtectionPathM1();

		String dataPath = "./data/";

		String[] netName = { "cost239" };// { "n6s8", "smallnet", "cost239", "nsfnet", "usnet" };
		
		int minRate = 200, maxRate = 3200, gap = 200;

		int M = 3;

		for (String net : netName) {
			long time = System.currentTimeMillis();
			SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			System.out.println("********************************************************************************");
			System.out.println("Topology File : " + net + ".csv\r\n" + date.format(new Date()) + "\r\n");
			
			for(int rate = minRate; rate <= maxRate;rate += gap) {
				Logger.logln("FixedRate : " + rate, null);
				heu.availabilityEnhancing_ProtectionPathM1_MD(dataPath, net, M, rate, rate);
			}
			
			System.out.println(date.format(new Date()));
			System.out.println("End of analysis : " + (net + ".csv"));
			time = System.currentTimeMillis() - time;
			System.out.println("Time-consuming : " + time + " ms");
			System.out.println("********************************************************************************\r\n");
		}
	}
}

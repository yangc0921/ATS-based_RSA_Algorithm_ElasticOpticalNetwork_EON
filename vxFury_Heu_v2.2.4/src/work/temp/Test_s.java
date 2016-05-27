package work.temp;

import java.util.ArrayList;

import eon.general.Constant;
import work.utilities.Logger;
import work.utilities.Utility;

public class Test_s {
	public static void main(String[] args) {
		ArrayList<Integer> resourceList = new ArrayList<Integer>();
		int size = (Constant.TotalSlotsNum >>> 5) + (((Constant.TotalSlotsNum & 0x1F) == 0) ? (0) : (1));
		for(int i = 0; i < size; i ++) {
			resourceList.add(i, 0xFFFFFFFF);
		}
		
		spectrumOccupy(10,10, resourceList);
		spectrumOccupy(30,10, resourceList);
		
		int bits = getBitsFlanked(resourceList,20,5);
		
		Logger.logln("" + bits, null);
		
		Long tmp = Long.MAX_VALUE;
		tmp |= 0x1 << 63;
		
		Logger.logln(Utility.toBinaryString(tmp), null);
		
		//spectrumRelease(startIndex,slots, resourceList);
	}
	
	public static int getBitsFlanked(ArrayList<Integer> resourceList, int index, int range) {
		int bits = 0;
		
		if(range == 0) {
			return 0;
		}
		
		int min, max;
		
		if(range < 0) {
			min = Math.max(0, index + range);
			max = index - 1;
			
			for(int i = max;i >= min; i --) {
				int tmpIndex = i >>> 5;
				int tmpOffset = i & 0x1F;
				
				int check = 0x1 << tmpOffset;
				if((resourceList.get(tmpIndex) & check) != 0x0) {
					bits ++;
				} else {
					break;
				}
			}
		} else {
			min = index + 1;
			max = Math.min(Constant.TotalSlotsNum, index + range);
			
			for(int i = min;i <= max; i ++) {
				int tmpIndex = i >>> 5;
				int tmpOffset = i & 0x1F;
				
				int check = 0x1 << tmpOffset;
				if((resourceList.get(tmpIndex) & check) != 0x0) {
					bits ++;
				} else {
					break;
				}
			}
		}
		
		return bits;
	}
	
	public static void spectrumOccupy(int startIndex, int slots, ArrayList<Integer> resourceList) {
		int endIndex = startIndex + slots;
		int offsetStart = startIndex & 0x1F;
		int indexStart = startIndex >>> 5;
		int offsetEnd = endIndex & 0x1F;
		int indexEnd = endIndex >>> 5;
		
		Logger.logln(null, "%d %d", endIndex, indexEnd);
		
		if(indexStart == indexEnd) {
			// resource required in the same Integer-Section
			Integer status = resourceList.get(indexStart);
			
			int check = ~(((0x1 << offsetStart) - 1) ^ ((0x01 << offsetEnd) - 1));
			resourceList.set(indexStart, status & check);
			
			Logger.logln(null, "%s %s", Utility.toBinaryString(status), Utility.toBinaryString(status & check));
		} else {
			Integer status = resourceList.get(indexStart);
			int check = (0x1 << offsetStart) - 1;
			resourceList.set(indexStart, status & check);
			Logger.logln(null, "%s %s", Utility.toBinaryString(status), Utility.toBinaryString(status & check));
			
			for(int index = indexStart + 1; index < indexEnd; index ++) {
				resourceList.set(index, 0x0);
			}
			
			if(offsetEnd != 0) {
				status = resourceList.get(indexEnd);
				
				check = ~((0x1 << offsetEnd) - 1);
				
				resourceList.set(indexEnd, status & check);
				
				Logger.logln(null, "%s %s", Utility.toBinaryString(status), Utility.toBinaryString(status & check));
			}
		}
	}
	
	public static void spectrumRelease(int startIndex, int slots, ArrayList<Integer> resourceList) {
		int endIndex = startIndex + slots;
		int offsetStart = startIndex & 0x1F;
		int indexStart = startIndex >>> 5;
		int offsetEnd = endIndex & 0x1F;
		int indexEnd = endIndex >>> 5;
		
		if(indexStart == indexEnd) {
			// resource required in the same Integer-Section
			Integer status = resourceList.get(indexStart);
			
			int check = ((0x1 << offsetStart) - 1) ^ ((0x01 << offsetEnd) - 1);
			resourceList.set(indexStart, status | check);
			
			Logger.logln(null, "%s %s", Utility.toBinaryString(status), Utility.toBinaryString(status | check));
		} else {
			Integer status = resourceList.get(indexStart);
			int check = ~((0x1 << offsetStart) - 1);
			resourceList.set(indexStart, status | check);
			Logger.logln(null, "%s %s", Utility.toBinaryString(status),Utility.toBinaryString(status | check));
			
			for(int index = indexStart + 1; index < indexEnd; index ++) {
				resourceList.set(indexEnd, 0xFFFFFFFF);
			}
			
			if(offsetEnd != 0) {
				status = resourceList.get(indexEnd);
				
				check = (0x1 << offsetEnd) - 1;
				
				resourceList.set(indexEnd, status | check);
				
				Logger.logln(null, "%s %s", Utility.toBinaryString(status), Utility.toBinaryString(status | check));
			}
		}
	}
}
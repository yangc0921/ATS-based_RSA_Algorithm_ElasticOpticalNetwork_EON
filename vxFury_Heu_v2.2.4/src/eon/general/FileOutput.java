package eon.general;

import java.io.*;

/**
 * @restructured by vxFury
 *
 */
public class FileOutput {
	public void writeLn(String filename, String result) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename, true);
			byte[] buffer1 = result.getBytes();
			fos.write(buffer1);
			String str = "\r\n";

			byte[] buffer = str.getBytes();
			fos.write(buffer);
			fos.close();
		} catch (FileNotFoundException e1) {
			System.out.println(e1);
		} catch (IOException e1) {
			System.out.println(e1);
		}
	}

	public void write(String filename, String result) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename, true);
			byte[] buffer1 = result.getBytes();
			fos.write(buffer1);
			String str = "";
			byte[] buffer = str.getBytes();
			fos.write(buffer);
			fos.close();
		} catch (FileNotFoundException e1) {
			System.out.println(e1);
		} catch (IOException e1) {
			System.out.println(e1);
		}
	}
}

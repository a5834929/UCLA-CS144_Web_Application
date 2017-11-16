package part1;
import java.io.*;
import java.security.*;
import javax.xml.bind.DatatypeConverter;

public class ComputeSHA {
	public static void main(String args[]) throws IOException, NoSuchAlgorithmException {  
		FileInputStream in = null;
		byte[] content = new byte[1024];
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		
		try{
			in = new FileInputStream("sample-input.txt");
			int c;
			while ((c = in.read(content)) != -1){
				md.update(content, 0, c);
			}
		}finally{
			if (in != null) in.close();
			byte[] rawBytes = md.digest();
			String hashValue = DatatypeConverter.printHexBinary(rawBytes);
			System.out.println(hashValue.toLowerCase());
		}
	}
}


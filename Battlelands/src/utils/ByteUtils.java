package utils;

public class ByteUtils {
	
	public static byte[] convertIntToByte(int intValue, int byteBase) {
		
	        byte[] convertByte = new byte[byteBase];

	        // Extraction des octets individuels
	        convertByte[0] = (byte) (intValue & 0xFF);
	        for(int i = 1; i <byteBase; i++) {
	        convertByte[i] = (byte) ((intValue >> 8*i) & 0xFF);
	        }
	        return convertByte;
	}
	
	public static int convertByteToInt(byte[] byteValue) {
	        int convertInt = 0;

	        // Reconstruction du nombre int
	        convertInt |= (byteValue[0] & 0xFF);
	        for(int i = 1; i <byteValue.length; i++) {
	        convertInt |= ((byteValue[i] & 0xFF) << 8*i);
	        }

	        return convertInt;
	}
	
	public static byte[] linkArrays(byte[] arrayOne, byte[] arrayTwo) {
		byte[] returnBytes = new byte[arrayOne.length + arrayTwo.length];

        System.arraycopy(arrayOne, 0, returnBytes, 0, arrayOne.length);
        System.arraycopy(arrayTwo, 0, returnBytes, arrayOne.length, arrayTwo.length);

		return returnBytes;
	}
	
	public static void main(String[] args) {
		double angle = Math.toRadians(180);
		System.out.println(angle);
		
		int toIntAngle = (int)((angle/(2*Math.PI))*(double)65535);
		System.out.println(toIntAngle);
		System.out.println((angle/(2*Math.PI)) + "   " + (float)toIntAngle/65535);
		
		byte[] bytesAngle = ByteUtils.convertIntToByte(toIntAngle, 2);
		int finalInt =ByteUtils.convertByteToInt(bytesAngle);
		angle = ((double)finalInt/(double)65535)*Math.PI*2;
		System.out.println(angle);
		
		
	}
	
}

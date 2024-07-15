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
	
	public static int convertByteToInt(byte byteValue) {
        int convertInt = 0;
        byte[] tempBytes = {byteValue};
        // Reconstruction du nombre int
        convertInt |= (tempBytes[0] & 0xFF);
        for(int i = 1; i <tempBytes.length; i++) {
        convertInt |= ((tempBytes[i] & 0xFF) << 8*i);
        }

        return convertInt;
}
	
	public static byte[] linkArrays(byte[] arrayOne, byte[] arrayTwo) {
		byte[] returnBytes = new byte[arrayOne.length + arrayTwo.length];

        System.arraycopy(arrayOne, 0, returnBytes, 0, arrayOne.length);
        System.arraycopy(arrayTwo, 0, returnBytes, arrayOne.length, arrayTwo.length);

		return returnBytes;
	}
	
}

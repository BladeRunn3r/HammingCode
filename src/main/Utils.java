package main;

class Utils {
					
	static int[] generateCode(int a[]) {
		
		int b[];
		//find number of parity bits required:
		int i = 0, parity_count = 0, j = 0, k = 0;
		while(i < a.length) {
			// 2^(parity bits) must equal the current position
			// Current position is (number of bits traversed + number of parity bits + 1)
			// +1 is needed since because we start from 1
			
			if(Math.pow(2, parity_count) == i + parity_count + 1) {
				parity_count++;
			}
			else i++;
		}
		
		// Length of 'b' is length of original data (a) + number of parity bits.
		b = new int[a.length + parity_count];
		
		// Initialize this array with '2' to indicate an 'unset' value in parity bit locations:
		
		for(i = 1 ; i <= b.length; i++) {
			if(Math.pow(2, j) == i) {
			// Found a parity bit location
			// Adjusting with (-1) to account for array indices starting from 0 instead of 1
				
				b[i - 1] = 2;
				j++;
			}
			else b[k + j] = a[k++];
		}
		for(i = 0; i < parity_count; i++) {
			// Setting even parity bits at parity bit locations:
			b[((int) Math.pow(2, i))-1] = getParity(b, i);
		}
		return b;
	}
	
	private static int getParity(int b[], int power) {
		int parity = 0;
		for(int i = 0; i < b.length; i++) {
			if(b[i] != 2) {
				// If 'i' doesn't contain an unset value, save that index value in k,
				//increase it by 1, then convert it into binary:
				
				int k = i + 1;
				String s = Integer.toBinaryString(k);
				
				// if the bit at the 2^(power) location of the binary value of index is 1
				//check the value stored at that location
				//check if that value is 1 or 0, and calculate the parity value
				
				int x = ((Integer.parseInt(s)) / ((int) Math.pow(10, power)))%10;
				if(x == 1) {
					if(b[i] == 1) parity = (parity + 1)%2;
				}
			}
		}
		return parity;
	}
	
	static String receive(int a[], int parity_count) {
		//detect the error and correct it, if any.
		int power;
		StringBuilder x = new StringBuilder();
		// use the value stored in 'power' to find the correct bits to check for parity
		
		int parity[] = new int[parity_count];
		// store the values of the parity checks
		
		StringBuilder syndrome = new StringBuilder();
		// used to store the integer value of error location
		
		for(power = 0; power < parity_count; power++) {
		//check the parities, the same no of times as the no of parity bits added
			
			for(int i = 0; i < a.length; i++) {
				// Extracting the bit from 2^(power):
				
				int k = i + 1;
				String s = Integer.toBinaryString(k);
				int bit = ((Integer.parseInt(s))/((int) Math.pow(10, power)))%10;
				if(bit == 1) {
					if(a[i] == 1) {
						parity[power] = (parity[power] + 1)%2;
					}
				}
			}
			syndrome.insert(0, parity[power]);
		}
		//parity check equation values
		// Using these values check if there is a single bit error and correct
		
		int error_location = Integer.parseInt(syndrome.toString(), 2);

			System.out.println("Error is at location " + error_location + ".");
			a[error_location - 1] = (a[error_location - 1] + 1)%2;
			System.out.println("Corrected code is:");
			for(int i = 0; i < a.length; i++) {
				System.out.print(a[a.length - i - 1]);
				x.append(Integer.toString(a[a.length - i - 1]));
			}
			System.out.println();
					
		// extract the original data from the received (and corrected) code:
		System.out.println("Original data sent was:");
		power = parity_count - 1;
		for(int i = a.length; i > 0; i--) {
			if(Math.pow(2, power) != i) {
				System.out.print(a[i - 1]);
			}
			else power--;
		}
		System.out.println();
		return x.toString();
	}	
}
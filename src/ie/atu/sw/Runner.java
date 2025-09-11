package ie.atu.sw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Runner {
	
	static Map<String, Integer> suffixes = new TreeMap<>(); // Takes the csv mapping file data
	static List<String> words = new ArrayList<>(); // Instance variable of type List (an interface)
	static String outputFile = "./out.txt";
	static boolean progressBar = true;
	
	/*
	 * main method is used to take in the users input and display the method
	 * 
	 * Method runs at O(1) as there are no loops in the method
	 */
	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		int option = 0;
		
		do {
			// Display menu
			menu();
			
			//Output a menu of options and solicit text from the user
			System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
			System.out.print("\nSelect Option [1-7]> ");
			option = scanner.nextInt();
			System.out.println();
			
			if (option == 1) {
				specifyMappingFile(scanner);
			} else if (option == 2) {
				specifyTextFileToEncode(scanner);
			} else if (option == 3) {
				specifyOutputFile(scanner);
			} else if (option == 4) {
				toggleProgressBar(scanner);
			} else if (option == 5) {
				encodeTextFile(scanner);
			} else if (option == 6) {
				decodeTextFile(scanner);
			} else if (option == 7) {
				System.out.println("Exiting program.");
			} else {
				System.out.println("Invalid option, please select again.");
			}
		} while (option != 7);
	}
	
	/*
	 * Method reads in the users mapping file
	 * Remove the , using split() and have to change second field to Integer for Map
	 * 
	 * I believe this method runs at O(n) as there is only one loop
	 */
	public static void specifyMappingFile(Scanner scanner) throws Exception {
		System.out.println("Chosen Option: (1) Specify Mapping File");
		
		System.out.print("Enter the name of the mapping file: ");
		String mapFile = scanner.next();
		
		try (var br = new BufferedReader(new InputStreamReader(new FileInputStream(mapFile)))) {
			String next;
			while ((next = br.readLine()) != null) { // Loop through each line in the csv file
				// split line on comma
				String[] fields = next.split(",");
				
				//put the first and second fields into the map - parse to Integer as Map is <String,Integer>
				suffixes.put(fields[0], Integer.parseInt(fields[1]));
			}
			
			System.out.println("Loaded map file: " + mapFile);
			
		} catch (Exception e) {
			throw new Exception("[ERROR] Encountered a problem reading the mapping file: " + e.getMessage());
		}
	}
	
	/*
	 * This method takes in the txt file the user wants to encode/decode
	 * 
	 * This method should run at O(n) as there is only one loop
	 */
	public static void specifyTextFileToEncode(Scanner scanner) throws Exception {
		System.out.println("Chosen Option: (2) Specify Text File to Encode");
		
		System.out.print("Enter the name of the txt file: ");
		String txtFile = scanner.next();
		
		words.clear(); // Remove last data read in
		
		try (var br = new BufferedReader(new InputStreamReader(new FileInputStream(txtFile)))) {
			String next;
			while ((next = br.readLine()) != null) { // Loop through each line in the txt file
				words.add(next); //Add the word to our array list
			}
			
			System.out.println("Loaded txt file: " + txtFile);
			
		} catch (Exception e) {
			throw new Exception("[ERROR] Encountered a problem reading the txt file: " + e.getMessage());
		}
	}
	
	/*
	 * This method takes in the users name for the output file containing the encoded/decoded text
	 * The output file has a default of ./out.txt if nothing was entered in this method
	 * 
	 * This method runs at O(1) time as there are no loops in the method
	 */
	public static void specifyOutputFile(Scanner scanner) {
		System.out.println("Chosen Option: (3) Specify Output File (default: ./out.txt)");
		
		System.out.print("Enter the name of the output file (default: ./out.txt): ");
		outputFile = scanner.next();
		
		System.out.println("Output file: " + outputFile);
	}
	
	/*
	 * This method is used to enable/disable the progress bar
	 * 
	 * This method runs at O(1) as there are no loops in the method
	 */
	public static void toggleProgressBar(Scanner scanner) {
		System.out.println("Chosen Option: (4) Toggle Progress Bar");
		
		if(progressBar == true) {
			progressBar = false;
		}
		else {
			progressBar = true;
		}
		
		System.out.println("Progress Bar now: " + progressBar + "\n");
	}
	
	/*
	 * This method is for encoding the txt file using the map file
	 * I first made a new ArrayList to store the finished encoded text
	 * Got each line and then split it by the spaces to get seperate words
	 * Then check full words and if not full words then check the prefix/suffix using substring to cut the words in half
	 * If not found at all then give it 0
	 * Then write the encoded text to file
	 * 
	 * I think the Method runs at O(n^2) due to the fact that it has three nested loops
	 * However, I couldn't seem to make it O(n log n) as whenever I tried to move the prefix suffix part
	 * it just never seemed to work properly
	 * Maybe I could have done it a better way? The encoding of file seems to be the same speed as decoding though
	 */
	public static void encodeTextFile(Scanner scanner) throws Exception {
		System.out.println("Chosen Option: (5) Encode Text File");
		
		if (suffixes.isEmpty() || words.isEmpty()) { // Check if map and txt file have been entered
			System.out.println("[ERROR] Option 1 or 2 not done");
			return;
		}
		
		// Encoding the txt file
		List<String> encodedWords = new ArrayList<>();
		
		for (int i = 0; i < words.size(); i++) {
			// Get current line and split it into words
			String line = words.get(i);
			String[] allWords = line.split(" ");
			
			// Loop each word in line
			for (int j = 0; j < allWords.length; j++) {
				String currentWord = allWords[j];
				
				// Find full word
				Integer fullWord = suffixes.get(currentWord);
				
				if (fullWord != null) { // Full word
					encodedWords.add(fullWord.toString());
				} else { // Not full
					boolean match = false;
					
					for (int k = 1; k < currentWord.length(); k++) {  // Loop to split the word
						String prefix = currentWord.substring(0, k);
						String suffix = "@@" + currentWord.substring(k);
						
						// Find prefix and suffix in map
						Integer prefixNum = suffixes.get(prefix);
						Integer suffixNum = suffixes.get(suffix);
						
						if (prefixNum != null && suffixNum != null) {  // Prefix and suffix match
							// Add the prefix and suffix, convert toString
							encodedWords.add(prefixNum.toString());
							encodedWords.add(suffixNum.toString());
							match = true;
							break;  // Exit the loop once a match is found
						}
					}
					
					// No match
					if (match == false) {
						encodedWords.add("0");
					}
				}
			}
			
			encodedWords.add("\n");
		}
		
		// Write encoded words to file
		try (var bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)))) {
			for (int i = 0; i < encodedWords.size(); i++) {
				bw.write(encodedWords.get(i) + " ");
			}
		} catch (Exception e) {
			throw new Exception("[ERROR] Failed to write encoded text to file: " + e.getMessage());
		}
		
		if (progressBar == true) {
			//You may want to include a progress meter in you assignment!
			System.out.print(ConsoleColour.YELLOW); //Change the colour of the console text
			int size = 100; 						//The size of the meter. 100 equates to 100%
			for (int i = 0; i < size; i++) { 		//The loop equates to a sequence of processing steps
				printProgress(i + 1, size); 		//After each (some) steps, update the progress meter
				Thread.sleep(10); 					//Slows things down so the animation is visible
			}
		}
		
		System.out.println("\nEncoded and saved to: " + outputFile);
	}

	/*
	 * This method is used to decode the encoded file
	 * At first I reversed the map to be able to decode
	 * Then I read in the encoded file with an ArrayList similar to how I read in the non-encoded txt file
	 * For the decoding part I got the number value of the index in the ArrayList and first checked if it was 0
	 * If not 0 then I got the word that matched the encoded number and check if it was a suffix or not (@@)
	 * Then at the end I wrote it out into the output file
	 * 
	 * I believe this method should run at O(n log n) as it has no nested loops
	 * However, I think the two for loops make it n log n and so I don't think it's O(n)
	 */
	public static void decodeTextFile(Scanner scanner) throws Exception {
		System.out.println("Chosen Option: (6) Decode Text File");
	    
		if (suffixes.isEmpty() || words.isEmpty()) { // Check if map and txt file have been entered
			System.out.println("[ERROR] Option 1 or 2 not done");
			return;
		}
		
	    // Reverse the map to decode
		Map<Integer, String> reversedMap = new TreeMap<>();
		for (String key : suffixes.keySet()) {
			reversedMap.put(suffixes.get(key), key);
		}
		
		// Read encoded file
		Scanner scannerFile = new Scanner(new File(outputFile));
		List<Integer> encodedWords = new ArrayList<>();
		while (scannerFile.hasNextInt()) {
			encodedWords.add(scannerFile.nextInt());
		}
		
		// Decoding the encoded file
		List<String> decodedWords = new ArrayList<>();
		String currentWord = "";
		
		for (int i = 0; i < encodedWords.size(); i++) {
			int number = encodedWords.get(i); // Get number
			
			if (number == 0) {
				decodedWords.add("[???]");
			} else {
				String word = reversedMap.get(number); // Get the word from map
				
				if (word.startsWith("@@")) { // Suffix
					currentWord += word.substring(2); // Add to the word using substring
				} else { // Prefix/Full word
					decodedWords.add(currentWord);
					currentWord = word; // New word
				}
			}
		}
		
		decodedWords.add(currentWord); // Add the word at the end, doesn't add in the loop
		
		// Write decoded words to file
		try (var bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)))) {
			for (int i = 0; i < decodedWords.size(); i++) {
				bw.write(decodedWords.get(i) + " ");
			}
		} catch (Exception e) {
			throw new Exception("[ERROR] Failed to write: " + e.getMessage());
		}
		
		if (progressBar == true) {
			//You may want to include a progress meter in you assignment!
			System.out.print(ConsoleColour.YELLOW);	//Change the colour of the console text
			int size = 100;							//The size of the meter. 100 equates to 100%
			for (int i =0 ; i < size ; i++) {		//The loop equates to a sequence of processing steps
				printProgress(i + 1, size); 		//After each (some) steps, update the progress meter
				Thread.sleep(10);					//Slows things down so the animation is visible 
			}
		}
		
		System.out.println("\nDecoded and saved to: " + outputFile);
	}
	
	/*
	 * Terminal Progress Meter
	 * -----------------------
	 * You might find the progress meter below useful. The progress effect
	 * works best if you call this method from inside a loop and do not call
	 * System.out.println(....) until the progress meter is finished.
	 * 
	 * Please note the following carefully:
	 * 
	 * 1) The progress meter will NOT work in the Eclipse console, but will
	 * work on Windows (DOS), Mac and Linux terminals.
	 * 
	 * 2) The meter works by using the line feed character "\r" to return to
	 * the start of the current line and writes out the updated progress
	 * over the existing information. If you output any text between
	 * calling this method, i.e. System.out.println(....), then the next
	 * call to the progress meter will output the status to the next line.
	 * 
	 * 3) If the variable size is greater than the terminal width, a new line
	 * escape character "\n" will be automatically added and the meter won't
	 * work properly.
	 * 
	 * 
	 * I believe the method should run at O(n) as there is only one loop
	 */
	public static void printProgress(int index, int total) {
		if (index > total) return; 	// Out of range
		int size = 50; 				// Must be less than console width
		char done = '#'; 			// Symbolises progress bar done
		char todo = '-'; 			// Symbolises progress not done
		
		// Compute basic metrics for the meter
		int complete = (100 * index) / total;
		int completeLen = size * complete / 100;
		
		/*
		 * A StringBuilder should be used for string concatenation inside a
		 * loop. However, as the number of loop iterations is small, using
		 * the "+" operator may be more efficient as the instructions can
		 * be optimized by the compiler. Either way, the performance overhead
		 * will be marginal.
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < size; i++) {
			sb.append((i < completeLen) ? done : todo);
		}
		
		/*
		 * The line feed escape character "\r" returns the cursor to the
		 * start of the current line. Calling print(...) overwrites the
		 * existing line and creates the illusion of an animation.
		 */
		System.out.print("\r" + sb + "] " + complete + "%");
		
		// Once the meter reaches its max, move to a new line.
		if (done == total)
			System.out.println("\n");
	}
	
	/*
	 * The menu class displays all the options available for the user to choose
	 * 
	 * This class runs at O(1), no loops
	 */
	public static void menu() {
		System.out.println();
		System.out.println(ConsoleColour.WHITE);
		System.out.println("************************************************************");
		System.out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
		System.out.println("*                                                          *");
		System.out.println("*              Encoding Words with Suffixes                *");
		System.out.println("*                                                          *");
		System.out.println("************************************************************");
		System.out.println("(1) Specify Mapping File");
		System.out.println("(2) Specify Text File to Encode");
		System.out.println("(3) Specify Output File (default: ./out.txt)");
		System.out.println("(4) Toggle Progress Bar (" + progressBar + ")");
		System.out.println("(5) Encode Text File");
		System.out.println("(6) Decode Text File");
		System.out.println("(7) Quit");
	}
}
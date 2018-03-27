package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FullAssembler implements Assembler{

	private boolean readingCode = true;
	private int noArgCount = 0;
	
	private Instruction makeCode(String[] parts){
		Instruction instr = null;
		int flags = 0;
		if(noArgument.contains(parts[0])){
			noArgCount++;
			int opPart = 8*Instruction.opcodes.get(parts[0]);
			//ensure parity is correct
			opPart += Instruction.numOnes(opPart)%2;
			instr = new Instruction((byte)opPart,0);
		}
		else{
			if(parts[1].charAt(0) == '#'){
				flags = 2;
				parts[1] = parts[1].substring(1);
			}
			else if(parts[1].charAt(0) == '@'){
				flags = 4;
				parts[1] = parts[1].substring(1);
			}
			else if(parts[1].charAt(0) == '&'){
				flags = 6;
				parts[1] = parts[1].substring(1);
			}
			int arg = Integer.parseInt(parts[1],16);
			int opPart =  8*Instruction.opcodes.get(parts[0]) + flags;
			//correct parity again
			opPart += Instruction.numOnes(opPart)%2;
			instr = new Instruction((byte)opPart,arg);
		}
		return instr;
	}
	
	@Override
	public int assemble(String inputFileName, String outputFileName, StringBuilder error){
		
		readingCode = true;
		
		if(error == null) {
			throw new IllegalArgumentException("Coding error: the error buffer is null");
		}
		
		ArrayList<String> codeInput = new ArrayList<String>();
		ArrayList<String> dataInput = new ArrayList<String>();
		boolean blankLine = false;
		int blankCount = 0;
		int lineNumber = 1;
		int tempLine = 1;
		int retVal = 0;
		String dataStr = "";
		boolean dataFound = false;
		ArrayList<String> wholeFileList = new ArrayList<String>();
		
		try(Scanner scan = new Scanner(new File(inputFileName))) {
			//find where DATA starts
			while(scan.hasNext()) {
				String line = scan.nextLine();
				if(line.trim().equals("DATA")) {
					dataFound = true;
					break;
				}
				if(line.trim().equalsIgnoreCase("DATA") && !line.trim().equals("DATA")) {
					error.append("error: DATA not in capitals" + " on line " + (lineNumber) + "\n");
					retVal = lineNumber;
				}
				lineNumber++;
			}
			/*
			if(dataFound == false) {
				error.append("error: data declaration not present in input file\n");
				//retVal = 1 means error wasn't on a specific line
				retVal = 1;
			}
			*/
		} catch (FileNotFoundException e) {
			System.out.println("Error: Unable to open the input file\n");
			System.exit(0);
			retVal = -1;
		} catch (IOException e) {
			System.out.println("Unexplained IO Exception\n");
			System.exit(0);
			retVal = -1;
		}
		
		if(dataFound == false) {
			try(Scanner scan = new Scanner(new File(inputFileName))){
				//add code and data to arraylists
				int tempLineNum = 1;
				while(scan.hasNext()) {
					String line = scan.nextLine();
					wholeFileList.add(line);
				}
			} catch (FileNotFoundException e) {
				System.out.println("Error: unable to open the input file\n");
				retVal = -1;
				System.exit(0);
			} catch (IOException e) {
				System.out.println("Unexplained IO Exception\n");
				retVal = -1;
				System.exit(0);
			}
		}
		
		else {
			try(Scanner scan = new Scanner(new File(inputFileName))){
				//add code and data to arraylists
				int tempLineNum = 1;
				while(scan.hasNext()) {
					String line = scan.nextLine();
					if(tempLineNum<lineNumber) {
						codeInput.add(line);
						tempLineNum++;
					}
					else if(tempLineNum == lineNumber) {
						dataStr = line;
						tempLineNum++;
					}
					else if(tempLineNum>lineNumber) {
						dataInput.add(line);
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("Error: unable to open the input file\n");
				retVal = -1;
				System.exit(0);
			} catch (IOException e) {
				System.out.println("Unexplained IO Exception\n");
				retVal = -1;
				System.exit(0);
			}
			
			wholeFileList.addAll(codeInput);
			wholeFileList.add(dataStr);
			wholeFileList.addAll(dataInput);
		}
		
		for(int i=0; i<wholeFileList.size(); i++) {
			
			if(wholeFileList.get(i).trim().length() == 0) {
				blankLine = true;
				tempLine = i+1;
				blankCount++;
			}
			
			if(blankLine && wholeFileList.get(i).length() > 0) {
				if(blankCount>1) {
					error.append("error -- there was a blank line on line: " + (tempLine-blankCount+1) + "\n");
					blankLine = false;
					retVal = tempLine-blankCount+1;
					blankCount = 0;
				}
				else {
					error.append("error - there was a blank line on line: " + tempLine + "\n");
					blankLine = false;
					blankCount = 0;
					retVal = tempLine;
				}

			}
			if((wholeFileList.get(i).length() > 0) && (wholeFileList.get(i).charAt(0) == ' ' || wholeFileList.get(i).charAt(0) == '\t')) {
				error.append("error - line starts with illegal whitespace" + " on line: " + (i+1) + "\n");
				retVal = i+1;
			}
			
			if(wholeFileList.get(i).trim().equals("DATA") && readingCode == false) {
				error.append("error - after DATA declaration 'DATA' was found again" + " on line: " + (i+1) + "\n");
				retVal = lineNumber;
			}
			
			if(wholeFileList.get(i).trim().toUpperCase().equals("DATA") && readingCode) {
				readingCode = false;
			}
			
			if(readingCode) {
				
				String[] parts = wholeFileList.get(i).trim().split("\\s+");
				if(!Instruction.opcodes.containsKey(parts[0]) && (wholeFileList.get(i).trim().length() > 0 )) {
					error.append("error - invalid mnemonic " + "on line: " + (i+1) + "\n");
					retVal = i+1;
				}
				if(!parts[0].toUpperCase().equals(parts[0])) {
					error.append("error - mnemonic is not in capitals" + " on line: " + (i+1) + "\n");
					retVal = i+1;
				}
				if(Assembler.noArgument.contains(parts[0])) {
					if(parts.length>1) {
						error.append("error - this mnemonic cannot take arguments" + " on line: " + (i+1) + "\n");
						retVal = i+1;
					}
				}
				
				if(parts.length>2) {
					error.append("error: this mnemonic has too many arguments" + " on line " + (i+1) + "\n");
					retVal = i+1;
				}
				
				if(parts.length == 2) {
					try {
						int flags = 0;
						if(parts[1].charAt(0) == '#'){
							flags = 2;
							parts[1] = parts[1].substring(1);
						}
						else if(parts[1].charAt(0) == '@'){
							flags = 4;
							parts[1] = parts[1].substring(1);
						}
						else if(parts[1].charAt(0) == '&'){
							flags = 6;
							parts[1] = parts[1].substring(1);
						}
						
						int arg = Integer.parseInt(parts[1],16);
						//why did i have to comment out this garbage below
						/*
						int opPart =  8*Instruction.opcodes.get(parts[0]) + flags;
						opPart += Instruction.numOnes(opPart)%2;
						Instruction instr = new Instruction((byte)opPart,arg);
						*/
					}catch(NumberFormatException e) {
						error.append("\nError on line " + (i+1) + 
								": argument is not a hex number\n");
						retVal = i + 1;	
					}
				}
				
			}
			
			else {
				String[] parts = wholeFileList.get(i).trim().split("\\s+");
				if(!wholeFileList.get(i).trim().toUpperCase().equals("DATA") && wholeFileList.get(i).trim().length() > 0 ) {
					try {
						int address = Integer.parseInt(parts[0],16);	
					}catch(NumberFormatException e) {
						error.append("\nError on line " + (i+1) + 
								": data has non-numeric memory address\n");
							retVal = i + 1;
					}
					try {
						if(parts.length > 1) {
							int value = Integer.parseInt(parts[1], 16);
						}
					}catch(NumberFormatException e) {
						error.append("\nError on line " + (i+1) + 
								": data has non-numeric memory value\n");
						retVal = i+1;
					}
				}

			}
		}
		
		
		if(error.length() == 0) {
			new SimpleAssembler().assemble(inputFileName, outputFileName, error);
		}
		
	return retVal;
	
	
	}
	
	public static void main(String[] args){
		StringBuilder error = new StringBuilder();
		System.out.println("Enter the name of the file without extension: ");
		try (Scanner keyboard = new Scanner(System.in)) { 
			String filename = keyboard.nextLine();
			int i = new FullAssembler().assemble(filename + ".pasm", 
				filename + ".pexe", error);
			System.out.println("result = " + i);
			if(error.length()>0) {
				System.out.println("Errors ---\n" + error);
			}
		}catch(Exception e) {
			System.out.println("there was an exception caught");
			e.printStackTrace();
		}
	}

}

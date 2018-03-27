package project;

import java.util.Set;
import java.util.TreeSet;
import java.util.Arrays;

public interface Assembler {
	class DataPair{
		protected int address;
		protected int value;
		
		public DataPair(int address, int value){
			this.address = address;
			this.value = value;
		}
		
		public String toString(){
			return "DataPair (" + address + ", " + value + ")";
		}
	}
	
	Set<String> noArgument = new TreeSet<String>(Arrays.asList("HALT", "NOP", "NOT"));
	int assemble(String inputFileName, String outputFileName, StringBuilder error);
}
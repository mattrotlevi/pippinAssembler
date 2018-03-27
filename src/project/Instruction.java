package project;

import java.util.Map;
import java.util.TreeMap;

public class Instruction {
	byte opcode;
	int arg;
	
	public Instruction(byte opcode, int arg){
		this.opcode = opcode;
		this.arg = arg;
	}
	// NOT, HALT, LOD, STO, ADD, SUB, MUL, DIV, AND, JUMP, JMPZ, CMPL, CMPZ
	public static Map<String, Integer> opcodes = new TreeMap<>();
	public static Map<Integer, String> mnemonics = new TreeMap<>();
	static {
		opcodes.put("NOP", 0);
		opcodes.put("NOT", 1);
		opcodes.put("HALT", 2);
		opcodes.put("LOD", 3);
		opcodes.put("STO", 4);
		opcodes.put("ADD", 5);
		opcodes.put("SUB", 6);
		opcodes.put("MUL", 7);
		opcodes.put("DIV", 8);
		opcodes.put("AND", 9);
		opcodes.put("JUMP", 10);
		opcodes.put("JMPZ", 11);
		opcodes.put("CMPL", 12);
		opcodes.put("CMPZ", 13);
		for(String str : opcodes.keySet()){ 
			mnemonics.put(opcodes.get(str), str);
		}	
	}
	
	public static boolean noArgument(Instruction instr){
		if(instr.opcode < 24){
			return true;
		}
			return false;
	}
	
	public static int numOnes(int input){
		String str = Integer.toUnsignedString(input, 2);
		int count = 0;
	    for (char ch : str.toCharArray()){
	        if(ch == '1'){
	        	count++;
	        }
	    }
	return count;    
	}
	
	public static void checkParity(Instruction instr){
		int count = numOnes(instr.opcode);
		if((count & 1) != 0){
			throw new ParityCheckException("The instruction is corrupted");
		}
	}
	
	public String getText(){
		StringBuilder buff = new StringBuilder();
		buff.append(mnemonics.get(opcode/8));
		buff.append("  ");
		int flags = opcode & 6;
		if(flags == 2){
			buff.append('#');
		}
		else if(flags == 4){
			buff.append('@');
		}
		else if(flags == 6){
			buff.append('&');
		}
		buff.append(Integer.toString(arg, 16));
		return buff.toString().toUpperCase();
	}
	
	public String getBinHex(){
		String s = "00000000" + Integer.toString(opcode,2) + "  " + Integer.toHexString(arg);
		StringBuilder buff = new StringBuilder();
		buff.append(s.substring(s.length()-8));
		return buff.toString().toUpperCase();
	}
	
	public String toString(){
		return "Instruction [" + Integer.toString(opcode,2) + ", " + Integer.toString(arg, 16)+"]";
	}
	
	
}

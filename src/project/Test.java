package project;

public class Test {

	public static void main(String[] args) {
		
		byte opcode = (byte) 0b00101011;
		int arg = 3;
		
		Instruction instr = new Instruction(opcode, arg);
		
		Instruction.checkParity(instr);

	}

}

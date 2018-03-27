package project;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.Arrays;
import static project.Instruction.*;

public class MachineModel {
	
	public final Map<Integer, Consumer<Instruction>> ACTION = new TreeMap<>();
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private boolean withGUI = false;
	private HaltCallback callBack;
	
	public MachineModel(boolean withGUI, HaltCallback cb){
		this.withGUI = withGUI;
		callBack = cb;
		ACTION.put(opcodes.get("NOP"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags != 0) {
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(opcodes.get("ADD"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum += memory.getData(instr.arg);
			}
			else if(flags == 2){ // immediate addressing
				cpu.accum += instr.arg;
			} 
			else if(flags == 4) { // indirect addressing
				cpu.accum += memory.getData(memory.getData(instr.arg));				
			} 
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
				+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(opcodes.get("SUB"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum -= memory.getData(instr.arg);
			}
			else if(flags == 2){ // immediate addressing
				cpu.accum -= instr.arg;
			} 
			else if(flags == 4) { // indirect addressing
				cpu.accum -= memory.getData(memory.getData(instr.arg));				
			} 
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
				+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(opcodes.get("MUL"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum *= memory.getData(instr.arg);
			}
			else if(flags == 2){ // immediate addressing
				cpu.accum *= instr.arg;
			} 
			else if(flags == 4) { // indirect addressing
				cpu.accum *= memory.getData(memory.getData(instr.arg));				
			} 
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
				+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(opcodes.get("DIV"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				if(memory.getData(instr.arg) == 0){
					throw new DivideByZeroException("null");
				}
				else{
					cpu.accum /= memory.getData(instr.arg);
				}
			}
			else if(flags == 2){ // immediate addressing
				if(instr.arg == 0){
					throw new DivideByZeroException("null");
				}
				else{
					cpu.accum /= instr.arg;
				}
			} 
			else if(flags == 4) { // indirect addressing
				if(memory.getData(memory.getData(instr.arg)) == 0){
					throw new DivideByZeroException("null");
				}
				else{
					cpu.accum /= memory.getData(memory.getData(instr.arg));	
				}
			} 
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
				+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(opcodes.get("NOT"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				if(cpu.accum == 0){
					cpu.accum = 1;
				}
				else{
					cpu.accum = 0;
				}
			}
			else if(flags == 2){ // immediate addressing
				throw new IllegalInstructionException("exception occured and caught");
			} 
			else if(flags == 4) { // indirect addressing
				throw new IllegalInstructionException("exception occured and caught");
			} 
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
				+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(opcodes.get("HALT"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags != 0) {
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			halt();
		});
		
		ACTION.put(opcodes.get("LOD"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum = memory.getData(instr.arg);
			}
			else if(flags == 2){ // immediate addressing
				cpu.accum = instr.arg;
			} 
			else if(flags == 4) { // indirect addressing
				cpu.accum = memory.getData(memory.getData(instr.arg));	
			} 
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
				+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(opcodes.get("STO"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				memory.setData(instr.arg, cpu.accum);
			}
			else if(flags == 4) { // indirect addressing
				memory.setData(memory.getData(instr.arg), cpu.accum);
			} 
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
				+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});	
		
		ACTION.put(opcodes.get("AND"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				if(cpu.accum != 0 && memory.getData(instr.arg) != 0){
					cpu.accum = 1;
				}
				else{
					cpu.accum = 0;
				}
			}
			else if(flags == 2){ // immediate addressing
				if(cpu.accum != 0 && instr.arg != 0){
					cpu.accum = 1;
				}
				else{
					cpu.accum = 0;
				}
			}
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
				+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});

		ACTION.put(opcodes.get("JUMP"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.pc += instr.arg;
			}
			else if(flags == 2){ // immediate addressing
				cpu.pc = instr.arg;
			} 
			else if(flags == 4) { // indirect addressing
				cpu.pc += memory.getData(instr.arg);			
			} 
			else {
				cpu.pc = memory.getData(instr.arg);
			}			
		});

		ACTION.put(opcodes.get("JMPZ"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				if(cpu.accum == 0){
					cpu.pc += instr.arg;
				}
				else{
					cpu.pc++;
				}
			}
			else if(flags == 2){ // immediate addressing
				if(cpu.accum == 0){
					cpu.pc = instr.arg;
				}
				else{
					cpu.pc++;
				}
			} 
			else if(flags == 4) { // indirect addressing
				if(cpu.accum == 0){
					cpu.pc += memory.getData(instr.arg);
				}
				else{
					cpu.pc++;
				}
			}
			else {
				if(cpu.accum == 0){
					cpu.pc = memory.getData(instr.arg);
				}
				else{
					cpu.pc++;
				}
			} 			
		});

		ACTION.put(opcodes.get("CMPL"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags != 0) {
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			if(memory.getData(instr.arg) < 0){
				cpu.accum = 1;
			}
			else{
				cpu.accum = 0;
			}
			cpu.pc++;			
		});
		
		ACTION.put(opcodes.get("CMPZ"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags != 0) {
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			if(memory.getData(instr.arg) == 0){
				cpu.accum = 1;
			}
			else{
				cpu.accum = 0;
			}
			cpu.pc++;			
		});	
	}
	
	public MachineModel() {
		this(false, null);
	}
	
	private class CPU{
		private int accum;
		private int pc;
	}
	
	public void setData(int i, int j) {
		memory.setData(i, j);		
	}
	public int[] getData() {
		return memory.getData();
	}
	public int getPC() {
		return cpu.pc;
	}
	public int getAccum() {
		return cpu.accum;
	}
	public void setAccum(int i) {
		cpu.accum = i;
	}
	public void setPC(int i) {
		cpu.pc = i;
	}
	
	public int getData(int index){
		return memory.getData(index);
	}
	
	public int[] getData(int min, int max){
		int[] newData = Arrays.copyOfRange(memory.getData(), min, max);
		return newData;
	}
	
	public Instruction getCode(int index){
		return memory.getCode(index);
	}
	
	public void setCode(int i, Instruction j){
		memory.setCode(i, j);
	}
	
	public Instruction[] getCode(){
		return memory.getCode();
	}
	
	public Instruction[] getCode(int min, int max){
		return memory.getCode(min, max);
	}
	
	public int getProgramSize(){
		return memory.getProgramSize();
	}
	
	public int getChangedDataIndex(){
		return memory.getChangedDataIndex();
	}
	
	public void setProgramSize(int i){
		memory.setProgramSize(i);
	}
	
	public void clear(){
		memory.clearData();
		memory.clearCode();
		cpu.pc = 0;
		cpu.accum = 0;
	}
	
	public void step(){
		try{
			Instruction instr = memory.getCode(cpu.pc);
			Instruction.checkParity(instr);
			//System.out.println(instr.getText() + " " + cpu.pc);
			ACTION.get(instr.opcode/8).accept(instr);
		}catch(Exception e){
			halt();
			throw e;
		}
	}
	
	public void halt(){
		if(!withGUI){
			System.exit(0);
		}
		else{
			callBack.halt();
		}
	}
	
}

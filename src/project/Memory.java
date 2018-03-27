package project;

import java.util.Arrays;

public class Memory {

	public static final int DATA_SIZE = 512;
	public static final int CODE_SIZE = 256;
	private int[] data = new int[DATA_SIZE];
	private Instruction[] code = new Instruction[CODE_SIZE];
	private int changedDataIndex = -1;
	private int programSize = 0;
	
	public int[] getData(){
		return data;
	}
	
	public int getData(int index){
		if(index < 0 || index >= DATA_SIZE) {
			throw new CodeAccessException("-1");
		}
			return data[index];
	}
	 
	public void setData(int index, int value){
		data[index] = value;
	}
	 
	public void clearData(){
		for(int i=0; i<data.length; i++){
			data[i] = 0;
		}
		changedDataIndex = -1;
	}
	 
	public int[] getData(int min, int max){
		int[] newData = Arrays.copyOfRange(data, min, max);
		return newData;
	}
	
	public int getChangedDataIndex(){
		return changedDataIndex;
	}
	
	public int getProgramSize(){
		return programSize;
	}
	
	public Instruction[] getCode(){
		return code;
	}
	
	public Instruction[] getCode(int min, int max){
		Instruction[] newCode = Arrays.copyOfRange(code, min, max);
		return newCode;
	}
	
	public Instruction getCode(int index){
		if(index < 0 || index >= CODE_SIZE) {
			throw new CodeAccessException("Illegal access to code");
		}
		return code[index];
	}
	
	public void setCode(int index, Instruction value){
		code[index] = value;
		programSize = Math.max(programSize, index);
	}
	
	public void clearCode(){
		for(int i=0; i<code.length; i++){
			code[i] = null;
		}
	}
	
	public void setProgramSize(int programSize){
		this.programSize = programSize;
	}

}

package edu.lsu.cct.dgc;

public class Msg {
	public static final int DL = 0;
	public static final int PH = 1;
	public static final int CD = 2;
	public static final int TR = 3;
	public static final int TD = 4;
	public static final int DE = 5;
	public static final int R = 6;
	public static final int RR = 7;
	public static final int RD = 8;
	public static final int CL = 9;
	int type;
	int from,to;
	int which;
	int cid;
	int parent;
	int p;
	Msg(int t){
		type=t;
	}
	

}

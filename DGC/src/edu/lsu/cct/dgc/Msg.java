package edu.lsu.cct.dgc;

public class Msg {
	public static final int DL = 0;
	public static final int PH = 1;
	public static final int CD = 2;
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
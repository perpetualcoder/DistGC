package edu.lsu.cct.dgc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Node {
	public static final int CL = 0;
	public static final int CO = 1;
	public static final int CD = 2;
	public static final int TR = 3;
	public static final int TD = 4;
	int id,which;
	int[] rc = new int[3];
	int cid;
	int parent;
	int state;
	int msgsent=0;
	int d =0;
	HashMap<Integer,Link> outMap = new HashMap<Integer,Link>();
	ArrayList<Link> stash = new ArrayList<Link>();
	Queue<Msg> qu = new LinkedList<Msg>();
	Node(int i){
		id=i;
		which=0;
		cid=-1;
		parent=-1;
		state=CL;
					
	}
	public void addLink(Node toNode,int to){
		int from = id;
		if(toNode.outMap.size()>0){
			toNode.rc[1-toNode.which]++;
			Link l = new Link(to,1-toNode.which);
			Main.nMap.get(from).outMap.put(to,l);
		}
		else{
			toNode.rc[toNode.which]++;
			Link l = new Link(to,toNode.which);
			Main.nMap.get(from).outMap.put(to,l);
		}
		
	}
	public void deleteLink(int to){
		Node toNode=Main.nMap.get(to);
		if(state==CL && toNode.state==CL ){
			Msg m = new Msg(Msg.DL);
			Link l = outMap.get(to);
			outMap.remove(to);
			m.which=l.which;
			m.p=l.p;
			m.to=l.to;
			toNode.qu.add(m);
			
		}
	}
	public void processMsg(){
		if(!qu.isEmpty()){
			Msg m = qu.poll();
			switch(m.type) {
			case Msg.DL:
				processDL(m);
				break;
			case Msg.PH:
				processPH(m);
				break;
			case Msg.CD:
				processCD(m);
				break;
			case Msg.TR:
				processTR(m);
				break;
			case Msg.TD:
				processTD(m);
				break;
			case Msg.DE:
				processDE(m);
				break;
			}
		}
		
	}
	public void processDE(Msg m){
		if(m.cid==cid){
			if(m.p==1)
				rc[2]--;
			if(rc[which]==0 && rc[1-which]==0 && rc[2]==0){
				if(outMap.size()>0){
					for(Integer i:outMap.keySet()){
						Link l = outMap.get(i);
						Msg mn = new Msg(Msg.DE);
						mn.p=1;
						mn.to=l.to;
						mn.from=id;
						mn.parent=id;
						mn.cid=cid;
						Node toNode=Main.nMap.get(l.to);
						toNode.qu.add(mn);
						outMap.remove(i);
						
					}
				}
				d=1;
			}
		}
	}
	public void processTD(Msg m){
		if(m.cid==cid){
			msgsent--;
			if(msgsent==0){
				if(parent==id){
					if(rc[which]>0){
						System.out.println("recovery!!!");
					}
					else{
						System.out.println("Start deleting");
//						return;
						if(outMap.size()>0){
							for(Integer i:outMap.keySet()){
								Link l = outMap.get(i);
								Msg mn = new Msg(Msg.DE);
								mn.p=1;
								mn.to=l.to;
								mn.from=id;
								mn.parent=id;
								mn.cid=cid;
								Node toNode=Main.nMap.get(l.to);
								toNode.qu.add(mn);
								outMap.remove(i);
							}
							
						}
						else{
							d=1;
							System.out.println("impossible to happen!");
						}
					}
				}else{
					if(rc[which]>0){
						System.out.println("recovery!");
					}
					else{
						Msg mn = new Msg(Msg.TD);
						mn.from=id;
						mn.to=parent;
						mn.cid=cid;
						Main.nMap.get(mn.to).qu.add(mn);
					}
				}
			}
		}
	}
	private void processTR(Msg m){
		if(rc[which]==0 && rc[2]>0 && state==CD && cid==m.cid ){
			state=TR;
			if(outMap.size()>0){
				for(Integer i:outMap.keySet()){
					Link l = outMap.get(i);
					Msg mn = new Msg(Msg.TR);
					mn.to=l.to;
					mn.from=id;
					mn.cid=cid;
					Node toNode=Main.nMap.get(l.to);
					toNode.qu.add(mn);
					msgsent++;
				}
			}
			else{
				Msg mn = new Msg(Msg.TD);
				mn.from=id;
				mn.to=parent;
				mn.cid=cid;
				Main.nMap.get(mn.to).qu.add(mn);
			}
			
		}
		else if(state==TR && cid==m.cid){
			Msg mn = new Msg(Msg.TD);
			mn.from=id;
			mn.to=m.from;
			mn.cid=cid;
			Main.nMap.get(mn.to).qu.add(mn);
		}
		else if(rc[which]>0 && state==CD && cid==m.cid){
			System.out.println("recovery procedure!");
		}
	}
	private void processCD(Msg m) {
		if(m.cid==cid) {
			msgsent--;
			if(msgsent==0){
				
				if(parent==id){
					state=CD;
					if(rc[which]>0){
						System.out.println("send recover message to itself!");
					}
					else if(rc[2]>0){
						Msg mn = new Msg(Msg.TR);
						mn.to=id;
						mn.cid=cid;
						qu.add(mn);
					}
				}
				else{
					state=CD;
					Msg mn = new Msg(Msg.CD);
					mn.from=id;
					mn.to=parent;
					mn.cid=cid;
					Main.nMap.get(mn.to).qu.add(mn);
				}
			
			}
				
		}
	}
	public void processPH(Msg m){
		if(state==CL){
			processPHCL(m);
		}
		else if(state==CO){
			System.out.println("I am exectued!");
			System.out.println("parent"+parent+"id"+id);
			if(m.which==which){
				rc[which]--;
				rc[2]++;
			}
			else if(m.which==1-which){
				rc[1-which]--;
				rc[2]++;
			}
			if(cid>=m.cid){
				Msg mn = new Msg(Msg.CD);
				mn.from=id;
				mn.to=m.from;
				mn.cid=cid;
				Main.nMap.get(mn.to).qu.add(mn);
			}
			else {
				System.out.println("This is not supposed to happen now!");
			}
		}
	}
	private void processPHCL(Msg m) {
		state=CO;
		parent=m.parent;
		cid=m.cid;
		if(which==m.which){
			rc[which]--;
			rc[2]++;
		}
		else if(which==1-m.which){
			rc[1-which]--;
			rc[2]++;
		}
		if(rc[which]>0){
			Msg mn = new Msg(Msg.CD);
			mn.from=id;
			mn.to=parent;
			mn.cid=cid;
			Main.nMap.get(mn.to).qu.add(mn);
		}
		else {
			if(rc[1-which]>0){
				which=1-which;
			}
			if(outMap.size()>0){
				for(Integer i:outMap.keySet()){
					Link l = outMap.get(i);
					Msg mn = new Msg(Msg.PH);
					l.p=1;
					mn.p=1;
					mn.which=l.which;
					mn.to=l.to;
					mn.from=id;
					mn.parent=id;
					mn.cid=cid;
					Node toNode=Main.nMap.get(l.to);
					toNode.qu.add(mn);
					msgsent++;
				}
			}
			else {
				Msg mn = new Msg(Msg.CD);
				mn.from=id;
				mn.to=parent;
				mn.cid=cid;
				Main.nMap.get(mn.to).qu.add(mn);
			}
		}
	}
	public void processDL(Msg m){
		System.out.println("processed message!");
		if(state==CL){
			if(m.which==which){
				rc[which]--;
			}
			else{
				rc[1-which]--;
			}
			if(rc[which]==0 && rc[1-which]>0){
				state=CO;
				which=1-which;
				parent=id;
				cid=Main.cidcounter++;
				if(outMap.size()>0){
					for(Integer i:outMap.keySet()){
						Link l = outMap.get(i);
						Msg mn = new Msg(Msg.PH);
						l.p=1;
						mn.p=1;
						mn.which=l.which;
						mn.to=l.to;
						mn.from=id;
						mn.parent=id;
						mn.cid=cid;
						Node toNode=Main.nMap.get(l.to);
						toNode.qu.add(mn);
						msgsent++;
					}
				}
				
			}
		}
	}

}

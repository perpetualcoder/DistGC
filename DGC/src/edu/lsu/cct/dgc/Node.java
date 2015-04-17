package edu.lsu.cct.dgc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Node {
	int id,which;
	int[] rc = new int[3];
	int cid;
	int parent;
	ArrayList<Link> outgoing = new ArrayList<Link>();
	HashMap<Integer,Integer> outMap = new HashMap<Integer,Integer>();
	ArrayList<Link> stash = new ArrayList<Link>();
	Queue<Msg> qu = new LinkedList<Msg>();
	Node(int i){
		id=i;
		which=0;
		cid=-1;
		parent=-1;
			
	}
	public void addLink(Node toNode,int to){
		int from = id;
		if(toNode.outgoing.size()>0){
			toNode.rc[1-toNode.which]++;
			Link l = new Link(to,1-toNode.which);
			Main.nMap.get(from).outgoing.add(l);
		}
		else{
			toNode.rc[toNode.which]++;
			Link l = new Link(to,toNode.which);
			Main.nMap.get(from).outgoing.add(l);
		}
		outMap.put(to, to);
	}
	public void deleteLink(int to){
		Msg m = new Msg(Msg.DL);
	}

}

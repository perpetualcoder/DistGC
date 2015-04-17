package edu.lsu.cct.dgc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

public class Main {
	static int cidcounter = 0;
	static Node anchor = new Node(0);
	static HashMap<Integer, Node> nMap = new HashMap<Integer, Node>();
	static String dotlocation = "C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe";
	static ImageViewer iv;
	public static HashMap<Integer, String> pic = new HashMap<Integer, String>();

	public static void main(String[] args) throws Exception {
		System.out.println("Enter the file name:");
		Scanner sc = new Scanner(System.in);
		String fName = sc.next();
		File fi = new File(fName);
		File fo = new File(fName + "-out");
		sc = new Scanner(fi);
		nMap.put(0, anchor);
		Pattern create = Pattern.compile("(\\d)->(\\d)");
		Pattern delete = Pattern.compile("(\\d)X(\\d)");
		Matcher matcher;
		int counter=1;
		while (sc.hasNext()) {
			String line = sc.next();
			System.out.println(line);
			matcher = create.matcher(line);
			if (matcher.find()) {
				createLink(matcher);
			}
			else {
				matcher = delete.matcher(line);
				if (matcher.find()) {
					System.out.println(matcher.group(1) + "xxx"
							+ matcher.group(2));
				
				int from = Integer.parseInt(matcher.group(1));
				int to = Integer.parseInt(matcher.group(2));
				if(nMap.containsKey(from)){
					if(nMap.containsKey(to)){
						Node nFrom = nMap.get(from);
						if(nFrom.outMap.containsKey(to)){
							nFrom.deleteLink(to);
						}
					}
				}
				}
			}
			printer(fName+"-out",counter);
			if(iv==null) {
				iv = new ImageViewer(pic.get(counter));
			}
			else {
				iv.setPic(pic.get(counter));
			}
		counter++;
		try {
		    Thread.sleep(2000);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		}
	}

	private static void createLink(Matcher matcher) {
		System.out.println(matcher.group(1) + ";;;" + matcher.group(2));
		int from = Integer.parseInt(matcher.group(1));
		int to = Integer.parseInt(matcher.group(2));
		if (nMap.containsKey(from)) {
			if (nMap.containsKey(to)) {
					Node toNode=nMap.get(to);
					Node fromNode = nMap.get(from);
					fromNode.addLink(toNode, to);
			} else {
					Node toNode = new Node(to);
					Node fromNode = nMap.get(from);
					fromNode.addLink(toNode, to);
			}
		}
	}

	public static void printer(String str, int count) throws IOException {
		File theDir = new File(str);

		// if the directory does not exist, create it
		if (!theDir.isDirectory()) {
			System.out.println("creating directory: " + str);
			boolean result = false;

			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (result) {
				System.out.println("DIR created");
			}
		}
		System.out.println(theDir.getAbsolutePath());
		PrintWriter wr = new PrintWriter(str + "\\" + str + "_gv" + count
				+ ".gv", "UTF-8");
		wr.println("Digraph G {");
		for (Entry<Integer, Node> entry : nMap.entrySet()) {
			printNode(wr, (Node) entry.getValue());
		}
		wr.println("}");
		wr.close();
		String cmd = dotlocation + "-Tjpg " + theDir.getAbsolutePath() + "\\"
				+ str + "_gv" + count + ".gv " + "-o "
				+ theDir.getAbsolutePath() + "\\" + str + "_gv" + count
				+ ".jpg";
		System.out.println(cmd);
		ProcessBuilder pb = new ProcessBuilder(dotlocation, "-Tjpg",
				theDir.getAbsolutePath() + "\\" + str + "_gv" + count + ".gv ",
				"-o", theDir.getAbsolutePath() + "\\" + str + "_gv" + count
						+ ".jpg");
		pb.redirectErrorStream(true);
		pb.directory(theDir.getAbsoluteFile());
		Process process = pb.start();
		File picf = new File(theDir.getAbsolutePath() + "\\" + str + "_gv"
				+ count + ".jpg");
		while (true) {
			if (picf.exists())
				break;
		}
		pic.put(count, theDir.getAbsolutePath() + "\\" + str + "_gv" + count
				+ ".jpg");
		for (Entry<Integer, String> entry : pic.entrySet()) {
			System.out.println(entry.getKey() + "->" + entry.getValue());
		}
	}

	public static void printNode(PrintWriter wr, Node n) {
		for (Link i : n.outgoing) {
			if (i.p == 1) {
				wr.println("edge [color=red];");
				wr.println(n.id + "->" + i.to + ";");
				wr.println("edge [color=black];");
			} else {
				if (n.which == i.which) {
					wr.println(n.id + "->" + i.to + ";");
				} else {
					wr.println(n.id + "->" + i.to + "[style=dotted];");
				}
			}
		}
	}
}

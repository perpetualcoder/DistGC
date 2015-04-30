package edu.lsu.cct.dgc;

import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

public class Main {
	static int cidcounter = 0;
	static Node anchor = new Node(0);
	static HashMap<Integer, Node> nMap = new HashMap<Integer, Node>();
	static String dotlocation = File.separatorChar == '/' ? "/usr/bin/dot" : "C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe";
	static ImageViewer iv;
	public static HashMap<Integer, String> pic = new HashMap<Integer, String>();
	static String fName;
	public static int counter = 1;
	public static int auto = 0;
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}
	public static void main(String[] args) throws Exception {
    Scanner sc = null;
    if(args.length == 0) {
      System.out.println("Enter the file name:");
      sc = new Scanner(System.in);
      fName = sc.next();
    } else {
      fName = args[0];
    }
		File fi = new File(fName);
		File fo = new File(fName + "-out");
		deleteDir(fo);
		sc = new Scanner(fi);
		nMap.put(0, anchor);
		Pattern create = Pattern.compile("(\\d)->(\\d)");
		Pattern delete = Pattern.compile("(\\d)X(\\d)");
		Matcher matcher;
		counter = 1;
		while (sc.hasNext()) {
			String line = sc.next();
			System.out.println(line);
			matcher = create.matcher(line);
			if (matcher.find()) {
				createLink(matcher);
			} else {
				matcher = delete.matcher(line);
				if (matcher.find()) {
					// System.out.println(matcher.group(1) + "xxx"
					// + matcher.group(2));
					//
					int from = Integer.parseInt(matcher.group(1));
					int to = Integer.parseInt(matcher.group(2));
					if (nMap.containsKey(from)) {
						if (nMap.containsKey(to)) {
							Node nFrom = nMap.get(from);
							if (nFrom.outMap.containsKey(to)) {
								nFrom.deleteLink(to);
							}
						}
					}
				}
			}
			display(fName, counter);
		}
		display(fName, counter);
		processConsole(sc);
	}

	private static void display(String fName, int counter) throws IOException,
			Exception {
		printer(fName + "-out", counter);
		if (iv == null) {
			iv = new ImageViewer(pic.get(counter));
		} else {
			iv.setPic(pic.get(counter));
		}
		Main.counter++;
		try {
			Thread.sleep(2000); // 1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	private static void processConsole(Scanner sc) throws IOException,
			Exception {

		String str;
		if (auto == 0) {
			sc = new Scanner(System.in);
			System.out.println("$>");
			str = sc.next();
		} else {
			str = "refresh";
		}
		Pattern process = Pattern.compile("(\\d)P");
		Pattern create = Pattern.compile("(\\d)->(\\d)");
		Pattern delete = Pattern.compile("(\\d)X(\\d)");
		Matcher match;
		System.out.println(str);
		while (!str.equals("exit")) {
			if (auto == 1) {
				str = pickRandom();
				if (str == null)
					break;
				str = str + "P";
			}
			if (!str.equals("refresh")) {
				match = process.matcher(str);
				if (match.find()) {
					int nodeId = Integer.parseInt(match.group(1));
					Node n = nMap.get(nodeId);
					n.processMsg();

				}
				match=create.matcher(str);
				if(match.find()){
					createLink(match);
				}
				match=delete.matcher(str);
				if(match.find()){
					int from = Integer.parseInt(match.group(1));
					int to = Integer.parseInt(match.group(2));
					if (nMap.containsKey(from)) {
						if (nMap.containsKey(to)) {
							Node nFrom = nMap.get(from);
							if (nFrom.outMap.containsKey(to)) {
								nFrom.deleteLink(to);
							}
						}
					}
				}
			}
			display(fName, counter);
			if (auto != 1) {
				System.out.println("$>");
				str = sc.next();
				System.out.println(str);
			}

		}
	}

	private static String pickRandom() {
		ArrayList<Node> mList = new ArrayList<Node>();
		for (Entry<Integer, Node> entry : nMap.entrySet()) {
			if (entry.getValue().qu.size() > 0)
				mList.add(entry.getValue());
		}
		Random r = new Random();

		if (mList.size() > 0) {
			int x = r.nextInt(mList.size());
			return String.valueOf(mList.get(x).id);
		}
		return null;
	}

	private static void createLink(Matcher matcher) {
		// System.out.println(matcher.group(1) + ";;;" + matcher.group(2));
		int from = Integer.parseInt(matcher.group(1));
		int to = Integer.parseInt(matcher.group(2));
		if (nMap.containsKey(from)) {
			if (nMap.containsKey(to)) {
				Node toNode = nMap.get(to);
				Node fromNode = nMap.get(from);
				fromNode.addLink(toNode, to);
			} else {
				Node toNode = new Node(to);
				Node fromNode = nMap.get(from);
				fromNode.addLink(toNode, to);
				nMap.put(to, toNode);
			}
		}
	}

	public static void printer(String str, int count) throws IOException {
		File theDir = new File(str);

		// if the directory does not exist, create it
		if (!theDir.isDirectory()) {
			// System.out.println("creating directory: " + str);
			boolean result = false;

			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
        se.printStackTrace();
			}
			// if (result) {
			// System.out.println("DIR created");
			// }
		}
		// System.out.println(theDir.getAbsolutePath());
		PrintWriter wr = new PrintWriter(str + File.separatorChar + str + "_gv" + count
				+ ".gv", "UTF-8");
		wr.println("Digraph G {");
		for (Entry<Integer, Node> entry : nMap.entrySet()) {
			printNode(wr, (Node) entry.getValue());
		}
		wr.println("}");
		wr.close();

		String cmd = dotlocation + "-Tjpg " + theDir.getAbsolutePath() + File.separatorChar
				+ str + "_gv" + count + ".gv " + "-o "
				+ theDir.getAbsolutePath() + File.separatorChar + str + "_gv" + count
				+ ".jpg";
		// System.out.println(cmd);
    System.out.println("HERE "+new Throwable().getStackTrace()[0]);

    String[] pbargs = new String[]{dotlocation,"-Tjpg",
				theDir.getAbsolutePath() + File.separatorChar + str + "_gv" + count + ".gv",
				"-o", theDir.getAbsolutePath() + File.separatorChar + str + "_gv" + count + ".jpg"};

		ProcessBuilder pb = new ProcessBuilder(pbargs);
		pb.redirectErrorStream(true);
		pb.directory(theDir.getAbsoluteFile());
		Process process = pb.start();
		File picf = new File(theDir.getAbsolutePath() + File.separatorChar + str + "_gv"
				+ count + ".jpg");
    InputStream in = process.getInputStream();
    byte[] buf = new byte[512];
		while (true) {
      int n = in.read(buf,0,buf.length);
      if(n > 0)
        System.out.write(buf,0,n);
			if (picf.exists())
				break;
		}

		pic.remove(count - 1);
		pic.put(count, theDir.getAbsolutePath() + File.separatorChar + str + "_gv" + count
				+ ".jpg");
		// for (Entry<Integer, String> entry : pic.entrySet()) {
		// System.out.println(entry.getKey() + "->" + entry.getValue());
		// }
	}

	public static void printNode(PrintWriter wr, Node n) {
		n.print();
		if (n.d == 1)
			return;
		String from = String.valueOf(n.id);
		if (nMap.get(n.id).qu.size() > 0) {
			from = "\"" + from + "M\"";
		}

		for (Link i : n.outMap.values()) {

			String to = String.valueOf(i.to);
			if (nMap.get(i.to).d != 1) {

				if (nMap.get(i.to).qu.size() > 0) {
					to = "\"" + to + "M\"";

				}
				if (i.p == 1) {
					wr.println("edge [color=red];");
					wr.println(from + "->" + to + ";");
					wr.println("edge [color=black];");
				} else {
					if (i.which == nMap.get(i.to).which) {
						wr.println(from + "->" + to + ";");
					} else {
						wr.println(from + "->" + to + "[style=dotted];");
					}
				}
			}
		}
		if(n.stash.size()>0){
			for(Link i : n.stash){
				String to = String.valueOf(i.to);
				if (nMap.get(i.to).d != 1) {

					if (nMap.get(i.to).qu.size() > 0) {
						to = "\"" + to + "M\"";

					}
					
						wr.println("edge [color=yellow];");
						wr.println(from + "->" + to + ";");
						wr.println("edge [color=black];");
					
				}
			}
		}
		if (n.outMap.size() == 0) {
			wr.println(from + ";");
		}
	}
}

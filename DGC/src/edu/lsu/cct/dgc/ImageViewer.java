package edu.lsu.cct.dgc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

/**
 * A Java class that helps to display the algorithm in visuals
 * 
 * @author Hari Krishnan
 */
public class ImageViewer extends JFrame {
	public static BufferedImage image = null;
	public static int current = 1;
	public static JFrame us = null;
	public static JLabel jLabel = new JLabel();

	public static void msain(String[] args) throws Exception {
		us = new ImageViewer("sample");
	}

	public void setPic(final String filename) {
		System.out.println(filename);
		File f = new File(filename);
		while (true) {
			if (f.exists())
				break;
		}
		while (true) {
			try {
				while (true) {
					image = ImageIO.read(f);
					if (image != null)
						break;
				}
				current = Main.pic.size();
				ImageIcon imageIcon = new ImageIcon(image);

				jLabel.setIcon(imageIcon);
				jLabel.repaint();
				SwingUtilities.updateComponentTreeUI(jLabel);
				System.out.println("pic changed!");
				break;
			}

			catch (Exception er) {
				er.printStackTrace();
				System.exit(1);
			}
		}

	}

	public ImageViewer(final String filename) throws Exception {

		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setBackground(Color.white);
		setFocusable(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JButton but = new JButton("click");
		JToolBar menubar = new JToolBar();
		JButton file = new JButton("prev");
		JButton next = new JButton("next");
		menubar.add(file);
		menubar.add(next);
		add(menubar, BorderLayout.NORTH);
		try {
			image = ImageIO.read(new File(filename));
			current = Main.pic.size();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		file.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("action cleared!" + current + "..."
						+ Main.pic.size());
				if (current > 1) {
					try {
						image = ImageIO.read(new File(Main.pic.get(--current)));
					}

					catch (Exception er) {
						er.printStackTrace();
						System.exit(1);
					}

					ImageIcon imageIcon = new ImageIcon(image);

					jLabel.setIcon(imageIcon);
					jLabel.repaint();
					SwingUtilities.updateComponentTreeUI(jLabel);
					System.out.println("pic changed!");
				}
			}
		});

		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("action cleared! next" + current + "..."
						+ Main.pic.size());
				if (current < Main.pic.size()) {
					try {
						image = ImageIO.read(new File(Main.pic.get(++current)));
					}

					catch (Exception er) {
						er.printStackTrace();
						System.exit(1);
					}
					ImageIcon imageIcon = new ImageIcon(image);

					jLabel.setIcon(imageIcon);
					jLabel.repaint();
					SwingUtilities.updateComponentTreeUI(jLabel);
					System.out.println("pic changed!");
				}
			}
		});
		ImageIcon imageIcon = new ImageIcon(image);

		jLabel.setIcon(imageIcon);
		jLabel.setHorizontalAlignment(JLabel.CENTER);
		getContentPane().add(jLabel, BorderLayout.CENTER);
		getContentPane().setBackground(Color.white);
		pack();
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setVisible(true);
	}

}
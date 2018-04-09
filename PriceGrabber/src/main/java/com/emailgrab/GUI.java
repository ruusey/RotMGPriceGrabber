package com.emailgrab;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class GUI extends JFrame {
	JTextArea text1;
	private JTextField text2, text3;
	public static GrabEmail g;
	public GUI() {
		super("Contact Info Finder");

		Container c = getContentPane();
		c.setLayout(new FlowLayout());

		JButton b1 = new JButton("Go");
		JButton b2 = new JButton("Clear");
		// construct textfield with default text
		text2 = new JTextField("Paste URL Here",20);
		text1 = new JTextArea("Console:", 20, 20);
		c.add(text2);
		c.add(b1);
		c.add(b2);
		c.add(text1);

		b1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent evt) {
				text1.setText("Retreiving Contact Info...");
				try {
					ArrayList<String> contact = g.grabEmail(text2.getText());
					text1.setText("");
					for(String s:contact){
						text1.append(s);
						text1.append("\n");
					}
				} catch (Exception e) {
					text1.append(e.getMessage());
				}
			}
		});
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				text2.setText("Paste URL Here");
				text1.setText("");
			}
		});

		setSize(500, 500);
		show();
	}

	public static void main(String args[]) {
		GUI app = new GUI();
		g=new GrabEmail();
		app.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
}
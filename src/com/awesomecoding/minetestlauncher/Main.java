/*
	Minetest Launcher
	Copyright (C) 2014 Nicholas Jones <awesomecoding@gmail.com>
	
	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation; either version 2.1 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License along
	with this program; if not, write to the Free Software Foundation, Inc.,
	51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package com.awesomecoding.minetestlauncher;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main {

	private JFrame frmMinetestLauncherV;
	private FileGetter fileGetter;
	
	private String latest;
	private String currentVersion;
	private String changelog;
	
	private String userhome = System.getProperty("user.home");
	
	private boolean newVersion = false;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmMinetestLauncherV.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		initialize();
	}

	private void initialize() {
		fileGetter = new FileGetter(userhome + "\\minetest\\temp\\");
		latest = fileGetter.getContents("http://socialmelder.com/minetest/latest.txt", true);
		currentVersion = latest.split("\n")[0];
		changelog = fileGetter.getContents("http://socialmelder.com/minetest/changelog.html", false);
		
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			e.printStackTrace();
		}
		
		frmMinetestLauncherV = new JFrame();
		frmMinetestLauncherV.setResizable(false);
		frmMinetestLauncherV.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/com/awesomecoding/minetestlauncher/icon.png")));
		frmMinetestLauncherV.setTitle("Minetest Launcher (Version 0.1)");
		frmMinetestLauncherV.setBounds(100, 100, 720, 480);
		frmMinetestLauncherV.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frmMinetestLauncherV.getContentPane().setLayout(springLayout);
		
		final JProgressBar progressBar = new JProgressBar();
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.WEST, frmMinetestLauncherV.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, progressBar, -10, SpringLayout.SOUTH, frmMinetestLauncherV.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, progressBar, -130, SpringLayout.EAST, frmMinetestLauncherV.getContentPane());
		frmMinetestLauncherV.getContentPane().add(progressBar);
		
		final JButton btnDownloadPlay = new JButton("Play!");
		springLayout.putConstraint(SpringLayout.WEST, btnDownloadPlay, 6, SpringLayout.EAST, progressBar);
		springLayout.putConstraint(SpringLayout.SOUTH, btnDownloadPlay, 0, SpringLayout.SOUTH, progressBar);
		springLayout.putConstraint(SpringLayout.EAST, btnDownloadPlay, -10, SpringLayout.EAST, frmMinetestLauncherV.getContentPane());
		frmMinetestLauncherV.getContentPane().add(btnDownloadPlay);
		
		final JLabel label = new JLabel("Ready to play!");
		springLayout.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, frmMinetestLauncherV.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, btnDownloadPlay, 0, SpringLayout.NORTH, label);
		springLayout.putConstraint(SpringLayout.SOUTH, label, -37, SpringLayout.SOUTH, frmMinetestLauncherV.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, progressBar, 6, SpringLayout.SOUTH, label);
		frmMinetestLauncherV.getContentPane().add(label);
		
		JTextPane txtpnNewFeatures = new JTextPane();
		txtpnNewFeatures.setBackground(SystemColor.window);
		springLayout.putConstraint(SpringLayout.NORTH, txtpnNewFeatures, 10, SpringLayout.NORTH, frmMinetestLauncherV.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, txtpnNewFeatures, 10, SpringLayout.WEST, frmMinetestLauncherV.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, txtpnNewFeatures, -10, SpringLayout.NORTH, btnDownloadPlay);
		springLayout.putConstraint(SpringLayout.EAST, txtpnNewFeatures, 0, SpringLayout.EAST, btnDownloadPlay);
		txtpnNewFeatures.setEditable(false);
		txtpnNewFeatures.setContentType("text/html");
		txtpnNewFeatures.setText(changelog);
		txtpnNewFeatures.setFont(new Font("Tahoma", Font.PLAIN, 12));
		frmMinetestLauncherV.getContentPane().add(txtpnNewFeatures);
		
		File file = new File(userhome + "\\minetest\\version.txt");
		
		if(!file.exists())
			newVersion = true;
		else{
			String version = fileGetter.getLocalContents(file, false);
			if(!version.equals(currentVersion))
				newVersion = true;
		}
		
		if(newVersion){
			label.setText("New Version Available! (" + currentVersion + ")");
			btnDownloadPlay.setText("Download & Play");
			
			btnDownloadPlay.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					Thread t = new Thread(){
						public void run(){
							File file = new File(userhome + "\\minetest\\version.txt");
							String version = fileGetter.getLocalContents(file, false);
							try{
								FileUtils.deleteDirectory(new File(userhome + "\\minetest\\minetest-" + version));
							} catch(Exception e) {
								e.printStackTrace();
							}
							fileGetter.download(latest.split("\n")[1], userhome + "\\minetest\\temp\\", "minetest.zip", label, progressBar, btnDownloadPlay, currentVersion);
							try {
								label.setText("Cleaning up...");
								btnDownloadPlay.setText("Cleaning up...");
								FileUtils.deleteDirectory(new File(userhome + "\\minetest\\temp"));
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.exit(0);
						}
					};
					t.start();
				}
			});
		} else {
			btnDownloadPlay.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					try {
						label.setText("Launching...");
						btnDownloadPlay.setEnabled(false);
						btnDownloadPlay.setText("Launching...");
						File file = new File(userhome + "\\minetest\\version.txt");
						String version = fileGetter.getLocalContents(file, false);
						Runtime.getRuntime().exec(userhome + "\\minetest\\minetest-" + version + "\\bin\\minetest.exe");
						System.exit(0);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			progressBar.setValue(100);
		}
	}
}

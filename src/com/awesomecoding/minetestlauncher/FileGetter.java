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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class FileGetter {
	
	String dir;
	
	public FileGetter(String directory){
		this.dir = directory;
	}
	
	public void download(String url, String filedir, String filename, JLabel label, JProgressBar progress, JButton button, String version){
		try{
			label.setText("Starting Download...");
			button.setText("Downloading...");
			button.setEnabled(false);
			
			URL website = new URL(url);
			
			URLConnection con = website.openConnection();
			int total = con.getContentLength();
			
			InputStream instream = website.openStream();
			File file = new File(filedir);
			file.mkdirs();
			FileOutputStream outstream = new FileOutputStream(filedir + "\\" + filename);
			
			byte[] buffer = new byte[153600];
	        int totalBytesRead = 0;
	        int bytesRead = 0;

	        while((bytesRead = instream.read(buffer)) > 0){
	        	outstream.write(buffer, 0, bytesRead);
	        	buffer = new byte[153600];
	        	totalBytesRead += bytesRead;
	        	double prog = Math.round(((double)totalBytesRead/total)*100);
	        	label.setText("Downloading... (" + (int)prog + "%)");
	        	progress.setValue((int)prog);
	        }
	        
	        label.setText("Unzipping...");
	        button.setText("Unzipping...");
	        unzip(filedir + "\\" + filename, System.getProperty("user.home") + "\\minetest\\", true);
	        
	        PrintWriter writer = new PrintWriter(filedir+"../version.txt", "UTF-8");
	        writer.println(version);
	        writer.close();
	        
	        label.setText("Launching...");
	        button.setText("Launching...");
	        
	        outstream.close();
	        instream.close();
	        
	        Runtime.getRuntime().exec(System.getProperty("user.home") + "\\minetest\\minetest-" + version + "\\bin\\minetest.exe");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getContents(String url, boolean seperate){
		try{
			String lineSeperator = "";
			if(seperate)
				lineSeperator = "\n";
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.connect();

			BufferedReader r  = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

			String sb = "";
			String line;
			while ((line = r.readLine()) != null) {
			    sb += line + lineSeperator;
			}
			return sb;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getLocalContents(File file, boolean seperate){
		try{
			String lineSeperator = "";
			if(seperate)
				lineSeperator = "\n";
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));

		    String line;
		    String response = "";

		    while ((line = reader.readLine()) != null) {
		        response = line + lineSeperator;
		    }
		    
		    reader.close();
		    return response;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void unzip(String fileloc, String dir, boolean delete){
		try {
            // Open the zip file
            ZipFile zipFile = new ZipFile(fileloc);
            Enumeration<?> enu = zipFile.entries();
            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enu.nextElement();

                String name = zipEntry.getName();

                // Do we need to create a directory ?
                File file = new File(dir+name);
                if (name.endsWith("/")) {
                    file.mkdirs();
                    continue;
                }

                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                // Extract the file
                InputStream is = zipFile.getInputStream(zipEntry);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = is.read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }
                is.close();
                fos.close();

            }
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}

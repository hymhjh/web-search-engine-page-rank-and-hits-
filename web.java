package pagerank;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class web extends JLabel{
	
	final String s;
	//for displaying the urls on a frame and make them web accessible
	public web(String u){
		this.s = u;
		this.setForeground(Color.white);
		
		this.addMouseListener(new MouseListener(){
		    	
			public void mouseClicked(MouseEvent e){
				URI url;
				try {
					url = new URI(s);
					Desktop d = Desktop.getDesktop();
					if(Desktop.isDesktopSupported() && d.isSupported(Desktop.Action.BROWSE)){
						try {
							d.browse(url);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					
			}
	
			@Override
			public void mousePressed(MouseEvent e) {}
		
			@Override
			public void mouseReleased(MouseEvent e) {}
		
			@Override
			public void mouseEntered(MouseEvent e) {}
		
			@Override
			public void mouseExited(MouseEvent e) {}
		});
	}  
	
}



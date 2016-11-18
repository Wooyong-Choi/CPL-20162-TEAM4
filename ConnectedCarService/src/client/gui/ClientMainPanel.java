package client.gui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import client.OtherCar;
import common.CarAttribute;
import common.Environment;
import common.MapDataFetcher;
import common.Point;
import common.gui.ImagePanel;

import javax.swing.ImageIcon;

public class ClientMainPanel extends JPanel implements Runnable, ActionListener {

	private boolean isDebug = false;
	
	private String serv_ip;
	private OtherCar car;

	private ImagePanel mapPanel;

	private JButton btnSetDest;
	private JToggleButton tglbtnONOFF;
	private JLabel lblConnectedCar;

	private Point destPoint;
	
	public ClientMainPanel(CarAttribute attr, String serv_ip, boolean isDebug) throws IOException {

		this.isDebug = isDebug;
		
		setLayout(null);
		setSize(1020, 722);
		setBackground(Color.WHITE);

		btnSetDest = new JButton("");
		btnSetDest.setActionCommand("1");
		btnSetDest.setIcon(new ImageIcon(ClientMainPanel.class.getResource("/common/gui/SetDst.png")));
		btnSetDest.setFont(new Font("����", Font.PLAIN, 20));
		btnSetDest.setBounds(83, 616, 240, 57);
		add(btnSetDest);

		//set destination action listener
		btnSetDest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String destMsg = JOptionPane.showInputDialog("Input your destination");
				//System.out.println(destMsg);
				if(destMsg.equals("1")) //Kyungpook North gate
					destPoint = new Point(35.892461, 128.609228);
				else if(destMsg.equals("2")) // Kyungpook Main gate
					destPoint = new Point(35.885136, 128.614203);
				else if(destMsg.equals("3")) // Daegu city hall
					destPoint = new Point(35.871379, 128.601800);
				else if(destMsg.equals("4")) // Daegu airport
					destPoint = new Point(35.899019, 128.639006);
				else if(destMsg.equals("5")) // exercise park
					destPoint = new Point(35.864444, 128.631835);
				//System.out.println(destPoint.getLatitude() + ":" + destPoint.getLongitude());
			}
		});
		
		tglbtnONOFF = new JToggleButton("");
		tglbtnONOFF.setActionCommand("2");
		tglbtnONOFF.setSelectedIcon(new ImageIcon(ClientMainPanel.class.getResource("/common/gui/ON.png")));
		tglbtnONOFF.setIcon(new ImageIcon(ClientMainPanel.class.getResource("/common/gui/OFF.png")));
		tglbtnONOFF.setFont(new Font("����", Font.PLAIN, 20));
		tglbtnONOFF.setBounds(360, 616, 240, 57);
		add(tglbtnONOFF);

		lblConnectedCar = new JLabel("Connected Car : None");
		lblConnectedCar.setFont(new Font("����", Font.PLAIN, 20));
		lblConnectedCar.setBounds(666, 630, 264, 24);
		add(lblConnectedCar);
		
		car = new OtherCar(attr);
		
		if (isDebug) {
			car.setCurPos(new Point(35.892441, 128.609169));
			
		} else {
			new Thread(car).start();
		}
		
		mapPanel = new ImagePanel(MapDataFetcher.getCurImage(car.getCurPos(), car.getAttr().getNum()));
		mapPanel.setBounds(0, 0, 1020, 600);
		add(mapPanel);
		mapPanel.setVisible(true);
		
		// Thread for Update Map Image
		new Thread(this).start();

		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		JButton btn = (JButton) arg0.getSource();
		char command = btn.getActionCommand().charAt(0);
		
		switch(command) {
		case 1:  // Set Destination
			
			break;
		case 2:  // Connected Car ON/OFF
			if (btn.isSelected()) {  // ON
				new Thread(new CarManager()).run();
				
			} else {  // OFF
				// Close safely
			}
			break;
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// Update Map Image
			while (true) {
				mapPanel.updateImage(MapDataFetcher.getCurImage(car.getCurPos(), car.getAttr().getNum()));
				Thread.sleep(Environment._IMAGE_UPDATE_TIME);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class CarManager implements Runnable {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				car.startConnectedCar_Client(serv_ip);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

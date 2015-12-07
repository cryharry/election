package election;

import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import javax.comm.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ElectionMain implements Runnable{
	static CommPortIdentifier portId;
	static Enumeration portList;
	InputStream input;
	SerialPort serialPort;
	Thread readThread;
	
	
	JFrame jFrame = new JFrame("선거");
	JPanel topPanel = new JPanel(new GridLayout(2,1));
	JPanel northPanel = new JPanel(new FlowLayout());
	JTextField jTextFieldName = new JTextField(10);
	JTextField jTextFieldClass= new JTextField(2);
	JTextField jTextFieldBan = new JTextField(2);
	JTextField jTextFieldNum = new JTextField(2);
	JTextField elecText = new JTextField(2);
	JLabel elecLabel = new JLabel("기호");
	Connection con;
	PreparedStatement pstmt;
	ResultSet rs, rs2, rs3;
	String sql = "", e_st = "";
	String[] splitString;
	
	public ElectionMain() { 
		JPanel elecTitle = new JPanel(new FlowLayout());
		elecTitle.setSize(JFrame.MAXIMIZED_HORIZ, 100);
		Calendar c =  Calendar.getInstance();
		String year = String.valueOf(c.get(Calendar.YEAR));
		JLabel yearLabel = new JLabel(year);
		elecTitle.add(yearLabel);
		JLabel titleLabel = new JLabel("년도");
		elecTitle.add(titleLabel);
		JLabel elecString = new JLabel();
		elecTitle.add(elecString);
		elecString.setText(getElecTitle(0));
		JLabel titleLabel2 = new JLabel("선거 투표");
		elecTitle.add(titleLabel2);
		topPanel.add(elecTitle);
		
		northPanel.add(new JLabel("성 명"));
		northPanel.add(jTextFieldName);
		northPanel.add(new JLabel("학년"));
		northPanel.add(jTextFieldClass);
		northPanel.add(new JLabel("반"));
		northPanel.add(jTextFieldBan);
		northPanel.add(new JLabel("번 호"));
		northPanel.add(jTextFieldNum);
		northPanel.add(elecLabel);
		northPanel.add(elecText);
		
		topPanel.add(northPanel);
		jFrame.add(topPanel,"North");
		
		JPanel centerPanel = new JPanel(new FlowLayout());
		JLabel checkLabel = new JLabel("학생증을 체크해주세요!");
		centerPanel.add(checkLabel);
		
		jFrame.add(centerPanel,"Center");
		
		jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setVisible(true);
		try {
		    serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
			input = serialPort.getInputStream();
		    serialPort.addEventListener(new SerialPortEventListener() {
				@Override
				public void serialEvent(SerialPortEvent event) {
					switch (event.getEventType()) {
					case SerialPortEvent.BI:
					case SerialPortEvent.OE:
					case SerialPortEvent.FE:
					case SerialPortEvent.PE:
					case SerialPortEvent.CD:
					case SerialPortEvent.CTS:
					case SerialPortEvent.DSR:
					case SerialPortEvent.RI:
					case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				    break;
				    
					case SerialPortEvent.DATA_AVAILABLE:
				    byte[] readBuffer = new byte[20];
				    try {
				    	while (input.available() > 0) {
						    int numBytes = input.read(readBuffer);
				    	}
				    	String rfcard = new String(readBuffer,0,10);
				    	String st_id;
				    	con = dbConn();
						sql = "SELECT name,class,ban,num,st_id FROM student WHERE rf_card_num='"+rfcard+"'";
						Statement stmt = con.createStatement();
						rs = stmt.executeQuery(sql);
						if(rs.next()) {
							centerPanel.remove(checkLabel);
							jTextFieldName.setText(rs.getString("name"));
							jTextFieldName.setEditable(false);
							jTextFieldClass.setText(String.valueOf(rs.getInt("class")));
							jTextFieldClass.setEditable(false);
							jTextFieldBan.setText(String.valueOf(rs.getInt("ban")));
							jTextFieldBan.setEditable(false);
							jTextFieldNum.setText(String.valueOf(rs.getInt("num")));
							jTextFieldNum.setEditable(false);
							st_id = rs.getString("st_id");
							sql = "SELECT st_id FROM Election_Cand WHERE subject='1학년 학생회장'";
							pstmt = con.prepareStatement(sql);
							rs = pstmt.executeQuery();
							int size = 0;
							while(rs.next()) {
								size++;
							}
							pstmt = con.prepareStatement(sql);
							rs2 = pstmt.executeQuery();
							String e_st_id[] = new String[size];
							JPanel elecImage= new JPanel(new FlowLayout());
							while(rs2.next()) {
								e_st_id[rs2.getRow()-1] = rs2.getString("st_id");
								String filePath = "C:\\Uni_cool\\image\\"+rs2.getString("st_id")+".jpg";
								ImageIcon icon = new ImageIcon(filePath);
								if(icon != null) {
									ImageIcon thumbnailIcon = new ImageIcon(getScaledImage(icon.getImage(), 200, 200));
									elecImage.add(new JLabel(thumbnailIcon));
								}
								centerPanel.add(elecImage);
							}
							
							
							jFrame.add(centerPanel);
							elecText.requestFocus();
							elecText.addKeyListener(new KeyListener() {
								@Override
								public void keyTyped(KeyEvent e) {
								}
								@Override
								public void keyReleased(KeyEvent e) {
								}
								
								@Override
								public void keyPressed(KeyEvent e) {
									switch (e.getKeyCode()) {
									case 97:
										e_st=e_st_id[0];
										break;
									case 98:
										e_st=e_st_id[1];
										break;
									case 99:
										e_st=e_st_id[2];
										break;
									default:
										break;
									}
									if(e.getKeyCode() == 10) {
										sql = "INSERT INTO Election_list(e_sel, st_id, e_st_id, e_date, e_time) VALUES ('A','"+st_id+"','"+e_st+"',getdate(),'000000')";
										try {
											pstmt = con.prepareStatement(sql);
											pstmt.executeUpdate();
										} catch (SQLException e1) {
											e1.printStackTrace();
										}
									}
								}
							});
							//jFrame.setVisible(false);
							jFrame.setVisible(true);
						} else {
							//System.out.println("b");
						}
				    } catch (Exception e) {
				    	e.printStackTrace();
				    }
				    break;
					}
				}

				private Image getScaledImage(Image image, int i, int j) {
					BufferedImage resizedImg = new BufferedImage(i, j, BufferedImage.TYPE_INT_RGB);
			        Graphics2D g2 = resizedImg.createGraphics();
			        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			        g2.drawImage(image, 0, 0, i, j, null);
			        g2.dispose();
			        return resizedImg;
				}
			});
		    serialPort.notifyOnDataAvailable(true);
		    serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, 
					   SerialPort.STOPBITS_1, 
					   SerialPort.PARITY_NONE);
		} catch (Exception e) {}		
		readThread = new Thread(this);
		readThread.start();
				 
	}
	public String getElecTitle(int i) {
		con = dbConn();
		sql = "SELECT DISTINCT(subject) FROM Election_Cand";
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			String elecString = "";
			while(rs.next()) {
				elecString += rs.getString("subject")+",";
			}
			splitString = elecString.split(",");	
		} catch (SQLException e1) {
			e1.printStackTrace();
		} 
		return splitString[i];
	}
	
	public void run() {
		try {
		    Thread.sleep(20000);
		} catch (InterruptedException e) {}
	}
	
	public Connection dbConn() {
		FileRead fr = new FileRead();
		List list = fr.getDB();
		String DBurl = "jdbc:sqlserver://"+list.get(0)+":1433;databaseName="+list.get(1);
		String user = "sa";
		String pwd = "unicool";
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(DBurl, user, pwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public void dbClose() {
		if(con!=null)try{con.close();}catch (Exception e) {}
		if(rs!=null)try{rs.close();}catch(Exception e){}
		if(pstmt!=null)try{pstmt.close();}catch(Exception e){}
		sql = "";
	}
	
	
	public static void main(String[] args) {
		portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
		    portId = (CommPortIdentifier) portList.nextElement();
		    if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals("COM1")) {
				    ElectionMain reader = new ElectionMain();
				} 
		    } 
		} 
	}

}

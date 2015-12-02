package election;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.List;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.swing.JFrame;

public class ElectionMain implements Runnable{
	static CommPortIdentifier portId;
	static Enumeration portList;
	InputStream input;
	SerialPort serialPort;
	Thread readThread;
	
	JFrame jFrame = new JFrame("선거");
	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	String sql = "";
	
	public ElectionMain() {
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
				    	String rfcard = new String(readBuffer,1,10);
				    	con = dbConn();
						sql = "SELECT class,ban,num,name FROM student WHERE rf_card_num="+rfcard;
						pstmt = con.prepareStatement(sql);
						rs = pstmt.executeQuery();
						if(rs.next()) {
							
						} else {
							
						}
				    } catch (Exception e) {
				    	e.printStackTrace();
				    }
				    break;
					}
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

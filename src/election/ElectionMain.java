package election;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.swing.JFrame;

public class ElectionMain {
	JFrame jFrame = new JFrame("선거");
	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	String sql = "";
	
	public ElectionMain() {
		con = dbConn();
		sql = "SELECT class,ban,num,name FROM student WHERE rf_card_num=?";
		
		 
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
		new ElectionMain();
	}

}

package election;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileRead {
		public List<String> getDB() {
			List list =  new ArrayList<String>();
			try {
				BufferedReader br = new BufferedReader(new FileReader("C:/Uni_Cool/KCM_IP.DAT"));
				String ip = br.readLine();
				String license = br.readLine();
				list.add(ip);
				list.add(license);
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}
}

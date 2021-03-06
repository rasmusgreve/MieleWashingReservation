import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.GregorianCalendar;



public class Main {

	final static String ip = "91.100.104.82";
	final static String user = "rasnano";
	
	public static void main(String[] args) throws Exception {
		//Get info
		
		System.out.println("Enter password for " + user);
		BufferedReader lineReader = new BufferedReader(new InputStreamReader(System.in));
		String pw = lineReader.readLine();
		
		MieleCommunicator mc = new MieleCommunicator(ip);
		mc.setCredentials(user, pw);
		
		
		System.out.println("Connected: " + mc.testCredentials());
		
		System.out.println("Current reservations: ");
		for (Reservation r : mc.getReservations()){
			System.out.println(r.getYMDDate() + " " + r.getPeriod() + " - " + r.getColor());
			if (r.getColor() == MachineColor.BLUE){
				Thread.sleep(500);
				boolean res = mc.deleteReservation(r);
				System.out.println("Deleted blue:" + res);
			}
		}
		
		System.out.println("Current reservations: ");
		for (Reservation r : mc.getReservations()){
			System.out.println(r.getYMDDate() + " " + r.getPeriod() + " - " + r.getColor());
		
		}
		
		/*
		System.out.println("Open Slots: ");
		for (Reservation r : mc.getOpenSlots(new GregorianCalendar(2015, 6-1, 2))){
			System.out.println(r.getPeriod() + " - " + r.getColor());
		}
		*/
	}

}

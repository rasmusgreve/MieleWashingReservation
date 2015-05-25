import java.util.Date;
import java.util.GregorianCalendar;

public class Reservation implements Comparable<Reservation>{

	private MachineColor color;
	private MachinePeriod period;
	private GregorianCalendar date;
	
	public Reservation(MachineColor color, MachinePeriod period, GregorianCalendar date) {
		this.color = color;
		this.period = period;
		this.date = date;
	}
	
	public MachineColor getColor() {return color;}
	public MachinePeriod getPeriod() {return period;}
	public GregorianCalendar getDate() {return date;}

	@Override
	public int compareTo(Reservation other) {
		int dateCompare = this.date.compareTo(other.date);
		if (dateCompare != 0) return dateCompare;
		return period.getStart().compareTo(other.period.getStart());
	}
	
	public static Reservation fromCResCreate(String cRes, GregorianCalendar date){
		String sub = cRes.substring(5, cRes.length()-1);
		String[] split = sub.split(",");
		
		//Raw parse
		String strColor = split[0].replaceAll("'", "").trim();
		//String strPeriod = split[1].replaceAll("'", "").trim();
		//String strUnknown = split[2].replaceAll("'", "").trim();
		String strPeriodAsStartAndTime = split[3].replaceAll("'", "").trim();
		
		//Intermediate
		String[] timeParts1 = strPeriodAsStartAndTime.split(" ");
		String[] timeParts2 = timeParts1[0].split(":");
		int hour = Integer.parseInt(timeParts2[0]);
		int minute = Integer.parseInt(timeParts2[1]);
		
		//Final parse
		MachineColor color = MachineColor.fromName(strColor);
		MachinePeriod period = MachinePeriod.fromStartTime(new TimeOfDay(hour, minute));
		
		return new Reservation(color, period, date);
	}
	
	public static Reservation fromCResDelete(String cRes){
		String sub = cRes.substring(5, cRes.length()-1);
		String[] split = sub.split(",");
		
		//Raw parse
		String strColor = split[0].replaceAll("'", "").trim();
		//String strPeriod = split[1].replaceAll("'", "").trim();
		//String strUnknown = split[2].replaceAll("'", "").trim();
		//String strDate1 = split[3].replaceAll("'", "").trim();
		String strDate2 = split[4].replaceAll("'", "").trim();
		//String strUnknownId = split[5].replaceAll("'", "").trim();
		String strPeriodAsStartAndTime = split[6].replaceAll("'", "").trim();
		
		//Intermediate
		String[] dateParts = strDate2.split("-");
		int year = Integer.parseInt(dateParts[0]);
		int month = Integer.parseInt(dateParts[1]);
		int day = Integer.parseInt(dateParts[2]);
		
		String[] timeParts1 = strPeriodAsStartAndTime.split(" ");
		String[] timeParts2 = timeParts1[0].split(":");
		int hour = Integer.parseInt(timeParts2[0]);
		int minute = Integer.parseInt(timeParts2[1]);
		
		//Final parse
		MachineColor color = MachineColor.fromName(strColor);
		GregorianCalendar date = new GregorianCalendar(year, month, day);
		MachinePeriod period = MachinePeriod.fromStartTime(new TimeOfDay(hour, minute));
		
		/*
		System.out.println("Parsing: " + sub);
		System.out.println("Color: " + color);
		System.out.println("Date:" + date.get(GregorianCalendar.YEAR) + " / " + date.get(GregorianCalendar.MONTH) + " / " + date.get(GregorianCalendar.DAY_OF_MONTH));
		System.out.println("Time: " + period);
		*/
		
		return new Reservation(color, period, date);
	}

}


public class TimeOfDay implements Comparable<TimeOfDay>{
	public final int hour, minute;
	
	public TimeOfDay(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
		assert(invariant());
	}
	
	public TimeOfDay addHours(int hours){
		return add(hours,0);
	}
	
	public TimeOfDay addMinutes(int minutes){
		return add(0, minutes);
	}
	
	public TimeOfDay add(int hours, int minutes){
		int newMinutes = (minute + minutes) % 60;
		int newHours = hour + hours + ((minute + minutes) / 60);
		return new TimeOfDay(newHours, newMinutes);
	}
	
	private boolean invariant(){
		return hour >= 0 && hour < 23 && minute >= 0 && minute < 60;
	}
	
	@Override
	public String toString() {
		return String.format("%d:%02d", hour, minute);
	}

	@Override
	public int compareTo(TimeOfDay other) {
		if (other.hour != hour) return hour - other.hour;
		return minute - other.minute;
	}
	
	@Override
	public boolean equals(Object other){
		if (other == null) return false;
		if (!(other instanceof TimeOfDay)) return false;
		TimeOfDay o = (TimeOfDay)other;
		return (o.hour == hour && o.minute == minute);
	}
	
	@Override
	public int hashCode(){
		return hour*60+minute;
	}
}

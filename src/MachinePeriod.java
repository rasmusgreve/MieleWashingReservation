
public enum MachinePeriod{

	Period700_930(new TimeOfDay(7,0)  , 150),
	Period930_11 (new TimeOfDay(9,30) ,  90),
	Period11_1330(new TimeOfDay(11,0) , 150),
	Period1330_15(new TimeOfDay(13,30),  90),
	Period15_1730(new TimeOfDay(15,0) , 150),
	Period1730_19(new TimeOfDay(17,30),  90),
	Period19_2130(new TimeOfDay(19,0) , 150);


	private final TimeOfDay start;
	private final int duration;
	private final TimeOfDay end;
	MachinePeriod(TimeOfDay start, int duration){
		this.start = start;
		this.duration = duration;
		this.end = start.addMinutes(duration);
	}
	public TimeOfDay getStart() { return start;}
	public TimeOfDay getEnd() { return end;}
	public int getDuration() { return duration;}
	public String getPeriodString(){
		return start + " +" + end.add(-start.hour, -start.minute);
	}
	
	
	public static MachinePeriod fromStartTime(TimeOfDay start){
		for(MachinePeriod p : values()){
			if (p.start.equals(start))
				return p;
		}
		return null;
	}
	
}

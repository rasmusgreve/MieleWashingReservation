
public enum MachineColor {
	RED("Rød"), BLUE("Bl�"), GREEN("Grøn"), YELLOW("Gul");
	
	private final String name;
	MachineColor(String name){
		this.name = name;
	}
	
	public static MachineColor fromName(String name){
		for (MachineColor c : MachineColor.values())
			if (c.name.equalsIgnoreCase(name))
				return c;
		return null;
	}
}

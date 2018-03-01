
public class Event {

	
	private String desc;
	private String date;
	private String startTime;
	private String endTime;

	
	public Event(String desc, String date, String startTime, String endTime) {
		this.desc = desc;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public String getStart(){
		return startTime;
	}
	public String getDate(){
		return date;
	}
	public String getTitle(){
		return desc;
	}
	public String getEnd(){
		return endTime;
	}
	
	

	public String toString() {
		if (endTime.equals("")) {
			return startTime + ": " + desc;
		}
		return startTime + " - " + endTime + ": " + desc;
	}
}
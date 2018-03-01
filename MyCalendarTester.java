

public class MyCalendarTester {
	public static void main(String[] args) {
		MyCalendar cal = new MyCalendar();
		MyCalendarViewer view = new MyCalendarViewer(cal);
		cal.attach(view);
	}
}

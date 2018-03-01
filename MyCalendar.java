

import java.io.*;
import java.util.*;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class MyCalendar{

	int maxDays;
	int focus;
	HashMap<String, ArrayList<Event>> eventsMap = new HashMap<>();
	ArrayList<ChangeListener> listeners = new ArrayList<>();
	GregorianCalendar cal = new GregorianCalendar();
	boolean changed = false;
	
	
	public MyCalendar() {
		maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		focus = cal.get(Calendar.DATE);
		loadEvents();
	}
	
	public void attach(ChangeListener l) {
		listeners.add(l);
	}
	
	
	public void updateListeners() {
		for (ChangeListener l : listeners) {
			l.stateChanged(new ChangeEvent(this));
		}
	}
	
	
	public void setCurrentDay(int day) {
		focus = day;
	}
	
	
	public int getCurrentDay() {
		return focus;
	}

	
	public int getCurrentYear() {
		return cal.get(Calendar.YEAR);
	}
	
	
	public int getCurrentMonth() {
		return cal.get(Calendar.MONTH);
	}
	
	
	public int getDayOfWeek(int i) {
		cal.set(Calendar.DAY_OF_MONTH, i);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	
	public int getMaxDays() {
		return maxDays;
	}

	
	public void nextMonth() {
		cal.add(Calendar.MONTH, 1);
		maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		changed = true;
		updateListeners();
	}
	
	
	public void prevMonth() {
		cal.add(Calendar.MONTH, -1);
		maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		changed = true;
		updateListeners();
	}
	
	
	public void nextDay() {
		focus++;
		if (focus > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			nextMonth();
			focus = 1;
		}
		updateListeners();
	}
	
	
	public void prevDay() {
		focus--;
		if (focus < 1) {
			prevMonth();
			focus = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		updateListeners();
	}
	
	
	public boolean hasMonthChanged() {
		return changed;
	}
	
	
	public void resetHasMonthChanged() {
		changed = false;
	}
	
	
	public void createEvent(String title, String startTime, String endTime) {
		String date = (cal.get(Calendar.MONTH) + 1) + "/" + focus + "/" + cal.get(Calendar.YEAR);
		Event e = new Event(title, date, startTime, endTime);
		ArrayList<Event> eventArray = new ArrayList<>();
		if (hasEvent(e.getDate())) {
			eventArray = eventsMap.get(date);
		}
		eventArray.add(e);
		eventsMap.put(date, eventArray);
	}
	
	
	public Boolean hasEvent(String date) {
		return eventsMap.containsKey(date);
	}

	
	public Boolean hasEventConflict(String timeStart, String timeEnd) {
		String date = (getCurrentMonth() + 1) + "/" + focus + "/" + getCurrentYear();
		if (!hasEvent(date)) {
			return false;
		}
		
		ArrayList<Event> eventArray = eventsMap.get(date);
		Collections.sort(eventArray, timeComparator());
		
		int timeStartMins = convertHourToMin(timeStart), timeEndMins = convertHourToMin(timeEnd);
		for (Event e : eventArray) {
			int eventStartTime = convertHourToMin(e.getStart()), eventEndTime = convertHourToMin(e.getEnd());
			if (timeStartMins >= eventStartTime && timeStartMins < eventEndTime) {
				return true;
			} else if (timeStartMins <= eventStartTime && timeEndMins > eventStartTime) {
				return true;
			}
		}
		return false;
	}
	
	
	public String getEvents(String date) {
		ArrayList<Event> eventArray = eventsMap.get(date);
		Collections.sort(eventArray, timeComparator());
		String events = "";
		for (Event e : eventArray) {
			events += e.toString() + "\n";
		}
		return events;
	}
	
	
	public void saveEvents() {
		if (eventsMap.isEmpty()) {
			return;
		}
		try {
			FileOutputStream fOut = new FileOutputStream("events.ser");
			ObjectOutputStream oOut = new ObjectOutputStream(fOut);
			oOut.writeObject(eventsMap);
			oOut.close();
			fOut.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	
	@SuppressWarnings("unchecked")
	private void loadEvents() {
		try {
			FileInputStream fIn = new FileInputStream("events.ser");
			ObjectInputStream oIn = new ObjectInputStream(fIn);
			HashMap<String, ArrayList<Event>> temp = (HashMap<String, ArrayList<Event>>) oIn.readObject();
			for (String date : temp.keySet()) {
				if (hasEvent(date)) {
					ArrayList<Event> eventArray = eventsMap.get(date);
					eventArray.addAll(temp.get(date));
				} else {
					eventsMap.put(date, temp.get(date));
				}
			}
			oIn.close();
			fIn.close();
		} catch (IOException ioe) {
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
		}
	}
	
	
	private int convertHourToMin(String time) {
		int hours = Integer.valueOf(time.substring(0, 2));
		return hours * 60 + Integer.valueOf(time.substring(3));
	}

	
	private static Comparator<Event> timeComparator() {
		return new Comparator<Event>() {
			@Override
			public int compare(Event arg0, Event arg1) {
				if (arg0.getStart().substring(0, 2).equals(arg1.getStart().substring(0, 2))) {
					return Integer.parseInt(arg0.getStart().substring(3, 5)) - Integer.parseInt(arg1.getStart().substring(3, 5));
				}
				return Integer.parseInt(arg0.getStart().substring(0, 2)) - Integer.parseInt(arg1.getStart().substring(0, 2));
			}
		};
	}
	
	
}


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class MyCalendarViewer implements ChangeListener {

	private MyCalendar cal;
	private DAYS[] daysArray = DAYS.values();
	private MONTHS[] monthsArray = MONTHS.values();
	private int previousNum = -1;
	private int maxDays;

	private JFrame frame = new JFrame("GUI Calendar");
	private JPanel monthViewPanel = new JPanel();
	private JLabel monthLbl = new JLabel();
	private JButton btnCreate = new JButton("Create");
	private JButton btnNext = new JButton("Next");
	private JButton btnPrev = new JButton("Prev");
	private JTextPane dayTextPane = new JTextPane();
	private ArrayList<JButton> dayBtns = new ArrayList<JButton>();

	
	public MyCalendarViewer (MyCalendar cal) {
		this.cal = cal;
		maxDays = cal.getMaxDays();
		monthViewPanel.setLayout(new GridLayout(0, 7));
		dayTextPane.setPreferredSize(new Dimension(300, 150));
		dayTextPane.setEditable(false);

		createDayBtns();
		addBlankBtns();
		addDayBtns();
		highlightEvents();
		showDate(cal.getCurrentDay());
		highlightSelectedDate(cal.getCurrentDay() - 1);

		btnCreate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createEventDialog();
			}
		});
		JButton prevMonth = new JButton("<");
		prevMonth.addActionListener(new ActionListener() {

			
			public void actionPerformed(ActionEvent e) {
				cal.prevMonth();
				btnCreate.setEnabled(false);
				btnNext.setEnabled(false);
				btnPrev.setEnabled(false);
				dayTextPane.setText("");
			}
		});
		JButton nextMonth = new JButton(">");
		nextMonth.addActionListener(new ActionListener() {

			
			public void actionPerformed(ActionEvent e) {
				cal.nextMonth();
				btnCreate.setEnabled(false);
				btnNext.setEnabled(false);
				btnPrev.setEnabled(false);
				dayTextPane.setText("");
			}
		});
		
		JPanel monthContainer = new JPanel();
		monthContainer.setLayout(new BorderLayout());
		monthLbl.setText(monthsArray[cal.getCurrentMonth()] + " " + cal.getCurrentYear());
		monthContainer.add(monthLbl, BorderLayout.NORTH);
		monthContainer.add(new JLabel("       S             M             T             W             T              F             S"), BorderLayout.CENTER);
		monthContainer.add(monthViewPanel, BorderLayout.SOUTH);
		
		JPanel dayViewPanel = new JPanel();
		dayViewPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		JScrollPane dayScrollPane = new JScrollPane(dayTextPane);
		dayScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		dayViewPanel.add(dayScrollPane, c);
		JPanel btnsPanel = new JPanel();
		btnNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cal.nextDay();
			}
		});
		btnPrev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cal.prevDay();
			}
		});
		btnsPanel.add(btnPrev);
		monthContainer.add(btnCreate);
		btnsPanel.add(btnNext);
		c.gridx = 0;
		c.gridy = 1;
		dayViewPanel.add(btnsPanel, c);

		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cal.saveEvents();
				System.exit(0);
			}
		});

		frame.add(prevMonth);
		frame.add(monthContainer);
		frame.add(nextMonth);
		frame.add(dayViewPanel);
		frame.add(quit, BorderLayout.NORTH);
		frame.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	
	public void stateChanged(ChangeEvent e) {
		if (cal.hasMonthChanged()) {
			maxDays = cal.getMaxDays();
			dayBtns.clear();
			monthViewPanel.removeAll();
			monthLbl.setText(monthsArray[cal.getCurrentMonth()] + " " + cal.getCurrentYear());
			createDayBtns();
			addBlankBtns();
			addDayBtns();
			highlightEvents();
			previousNum = -1;
			cal.resetHasMonthChanged();
			frame.pack();
			frame.repaint();
		} else {
			showDate(cal.getCurrentDay());
			highlightSelectedDate(cal.getCurrentDay() - 1);
		}
	}

	
	private void createEventDialog() {
		final JDialog eventDialog = new JDialog();
		eventDialog.setTitle("Create event");
		eventDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		final JTextField eventText = new JTextField(30);
		final JTextField timeStart = new JTextField(10);
		final JTextField timeEnd = new JTextField(10);
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (eventText.getText().isEmpty()) {
					return;
				}
				if ((!eventText.getText().isEmpty() && (timeStart.getText().isEmpty() || timeEnd.getText().isEmpty())) || timeStart.getText().length() != 5 || timeEnd.getText().length() != 5 || !timeStart.getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]") || !timeEnd.getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
					JDialog timeErrorDialog = new JDialog();
					timeErrorDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
					timeErrorDialog.setLayout(new GridLayout(2, 0));
					timeErrorDialog.add(new JLabel("Please enter start and end time in format XX:XX."));
					JButton ok = new JButton("Okay");
					ok.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							timeErrorDialog.dispose();
						}
					});
					timeErrorDialog.add(ok);
					timeErrorDialog.pack();
					timeErrorDialog.setVisible(true);
				} else if (!eventText.getText().equals("")) {
					if (cal.hasEventConflict(timeStart.getText(), timeEnd.getText())) {
						JDialog conflictDialog = new JDialog();
						conflictDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
						conflictDialog.setLayout(new GridLayout(4, 0));
						conflictDialog.add(new JLabel("Event already exists at that time"));
						JButton okay = new JButton("Okay");
						okay.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								conflictDialog.dispose();
							}
						});
						conflictDialog.add(okay);
						conflictDialog.pack();
						conflictDialog.setVisible(true);
					} else {
						eventDialog.dispose();
						cal.createEvent(eventText.getText(), timeStart.getText(), timeEnd.getText());
						showDate(cal.getCurrentDay());
						highlightEvents();
					}
				}
			}
		});
		eventDialog.setLayout(new GridBagLayout());
		JLabel date = new JLabel();
		date.setText(cal.getCurrentMonth() + 1 + "/" + cal.getCurrentDay() + "/" + cal.getCurrentYear());
		date.setBorder(BorderFactory.createEmptyBorder());

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.gridx = 0;
		c.gridy = 0;
		eventDialog.add(date, c);
		c.gridy = 1;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		eventDialog.add(new JLabel("Event"), c);
		c.gridy = 2;
		eventDialog.add(eventText, c);
		c.gridy = 3;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.LINE_START;
		eventDialog.add(new JLabel("Time Start (00:00)"), c);
		c.anchor = GridBagConstraints.CENTER;
		eventDialog.add(new JLabel("Time End (24:00)"), c);
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		eventDialog.add(timeStart, c);
		c.anchor = GridBagConstraints.CENTER;
		eventDialog.add(timeEnd, c);
		c.anchor = GridBagConstraints.LINE_END;
		eventDialog.add(save, c);
		eventDialog.pack();
		eventDialog.setVisible(true);
	}

	
	private void showDate(final int d) {
		cal.setCurrentDay(d);
		String dayOfWeek = daysArray[cal.getDayOfWeek(d) - 1] + "";
		String date = (cal.getCurrentMonth() + 1) + "/" + d + "/" + cal.getCurrentYear();
		String events = "";
		if (cal.hasEvent(date)) {
			events += cal.getEvents(date);
		}
		dayTextPane.setText(dayOfWeek + " " + date + "\n" + events);
		dayTextPane.setCaretPosition(0);
	}

	private void highlightSelectedDate(int d) {
		Border border = new LineBorder(Color.ORANGE, 2);
		dayBtns.get(d).setBorder(border);
		if (previousNum != -1) {
			dayBtns.get(previousNum).setBorder(new JButton().getBorder());
		}
		previousNum = d;
	}

	
	private void highlightEvents() {
		for (int i = 1; i <= maxDays; i++) {
			if (cal.hasEvent((cal.getCurrentMonth() + 1) + "/" + i + "/" + cal.getCurrentYear())) {
				dayBtns.get(i - 1).setBackground(Color.decode("0xE4EFF8"));
			}
		}
	}

	
	private void createDayBtns() {
		for (int i = 1; i <= maxDays; i++) {
			final int d = i;
			JButton day = new JButton(Integer.toString(d));
			day.setBackground(Color.WHITE);
	
			day.addActionListener(new ActionListener() {
	
				@Override
				public void actionPerformed(ActionEvent arg0) {
					showDate(d);
					highlightSelectedDate(d - 1);
					btnCreate.setEnabled(true);
					btnNext.setEnabled(true);
					btnPrev.setEnabled(true);
				}
			});
			dayBtns.add(day);
		}
	}

	
	private void addDayBtns() {
		for (JButton d : dayBtns) {
			monthViewPanel.add(d);
		}
	}

	
	private void addBlankBtns() {
		for (int j = 1; j < cal.getDayOfWeek(1); j++) {
			JButton blank = new JButton();
			blank.setEnabled(false);
			monthViewPanel.add(blank);
		}
	}
}

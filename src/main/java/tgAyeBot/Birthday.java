package tgAyeBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Birthday implements BirthdayInterface {
	private static String newLineChar = "FIfWdtkdtU";
	
	private String code;
	private long authorId;
	private String authorName;
	private long contactId;
	private String contactName;
	private ZonedDateTime birthdayDate;
	private String text;
	private boolean isDisplayed;
	
	Birthday(String code, long authorId, String authorName, long contactId, String contactName, ZonedDateTime birthdayDate, String text, boolean isDisplayed) {
		setCode(code);
		setAuthorId(authorId);
		setAuthorName(authorName);
		setContactId(contactId);
		setContactName(contactName);
		setBirthdayDate(birthdayDate);
		setText(text);
		setIsDisplayed(isDisplayed);
	}
	
	@Override
	public String code() {
		return this.code;
	}
	@Override
	public long authorId() {
		return this.authorId;
	}
	@Override
	public String authorName() {
		return this.authorName;
	}
	@Override
	public long contactId() {
		return this.contactId;
	}
	@Override
	public String contactName() {
		return this.contactName;
	}
	@Override
	public ZonedDateTime birthdayDate() {
		return this.birthdayDate;
	}
	@Override
	public String text() {
		return this.text;
	}
	public boolean isDisplayed() {
		return this.isDisplayed;
	}
	
	
	@Override
	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}
	@Override
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	@Override
	public void setContactId(long contactId) {
		this.contactId = contactId;
	}
	@Override
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	@Override
	public void setBirthdayDate(ZonedDateTime birthdayDate) {
		this.birthdayDate = birthdayDate;
	}
	@Override
	public void setText(String text) {
		this.text = text;
	}
	public void setIsDisplayed(boolean isDisplayed) {
		this.isDisplayed = isDisplayed;
	}
	
	public static List<Birthday> readBirthdays() throws FileNotFoundException {
		ZoneId zoneId = ZoneId.of("Europe/Kiev");
		List<Birthday> birthdays = new ArrayList<Birthday>();
		List<String> lines = TextFile.readLines(Resource.birthdays.path);
		String codeLine;
		String authorIdLine;
		String authorNameLine;
		String contactIdLine;
		String contactNameLine;
		String birthdayDateLine;
		String textLine;
		String isDisplayedLine;
		
		String code = null;
		long authorId = 0;
		String authorName = null;
		long contactId = 0;
		String contactName = null;
		ZonedDateTime birthdayDate = null;
		String text = null;
		boolean isDisplayed = false;
		
		int counter = 0;
		for (String line : lines) {
			switch (counter) {
			case 0:
				codeLine = line;
				code = codeLine;
				counter++;
				break;
			case 1:
				authorIdLine = line;
				authorId = Long.parseLong(authorIdLine);
				counter++;
				break;
			case 2:
				authorNameLine = line;
				authorName = authorNameLine;
				counter++;
				break;
			case 3:
				contactIdLine = line;
				contactId = Long.parseLong(contactIdLine);
				counter++;
				break;
			case 4:
				contactNameLine = line;
				contactName = contactNameLine;
				counter++;
				break;
			case 5:
				birthdayDateLine = line;
				long birthdayDateUnix = Long.parseLong(birthdayDateLine);
				Instant instant = Instant.ofEpochSecond(birthdayDateUnix);
				birthdayDate = ZonedDateTime.ofInstant(instant, zoneId);
				counter++;
				break;
			case 6:
				textLine = line;
				text = textLine.replace(newLineChar, "\n");
				counter++;
				break;
			case 7:
				isDisplayedLine = line;
				String yes = "yes";
				String no = "no";
				if (isDisplayedLine.contentEquals(yes)) isDisplayed = true;
				else if (isDisplayedLine.contentEquals(no)) isDisplayed = false;
				
				Birthday birthday = new Birthday(code, authorId, authorName, contactId, contactName, birthdayDate, text, isDisplayed);
				birthdays.add(birthday);
				
				counter = 0;
				break;
			}
		}
		return birthdays;
	}
	
	public static void writeBirthdays(List<Birthday> birthdays) throws IOException {
		List<String> lines = new ArrayList<String>();
		String codeLine;
		String authorIdLine;
		String authorNameLine;
		String contactIdLine;
		String contactNameLine;
		String birthdayDateLine;
		String textLine;
		String isDisplayedLine;
		
		for (Birthday birthday : birthdays) {
			codeLine = birthday.code();
			
			authorIdLine = Long.toString( birthday.authorId() );
			
			authorNameLine = birthday.authorName();
			
			contactIdLine = Long.toString( birthday.contactId() );
			
			contactNameLine = birthday.contactName();
			
			long birthdayDateUnix = birthday.birthdayDate().toEpochSecond();
			birthdayDateLine = Long.toString(birthdayDateUnix);
			
			textLine = birthday.text().replace("\n", newLineChar);
			
			if ( birthday.isDisplayed() ) isDisplayedLine = "yes";
			else isDisplayedLine = "no";
			
			lines.add(codeLine);
			lines.add(authorIdLine);
			lines.add(authorNameLine);
			lines.add(contactIdLine);
			lines.add(contactNameLine);
			lines.add(birthdayDateLine);
			lines.add(textLine);
			lines.add(isDisplayedLine);
		}
		String filePath = Resource.birthdays.path;
		TextFile.writeLines(filePath, lines, false);
	}
	
	public static void addBirthday(Birthday birthday) throws IOException {
		List<Birthday> birthdays = readBirthdays();
		birthdays.add(birthday);
		Birthday.writeBirthdays(birthdays);
	}
	public static void addBirthday(List<Birthday> newBirthdays) throws IOException {
		List<Birthday> birthdays = Birthday.readBirthdays();
		for (Birthday newBirthday : newBirthdays) {
			birthdays.add(newBirthday);
		}
		Birthday.writeBirthdays(birthdays);
	}
	
	public static List<Birthday> todayBirthdays() throws IOException {
		List<Birthday> birthdays = readBirthdays();
		List<Birthday> todayBirthdays = new ArrayList<Birthday>();
		ZonedDateTime now = Bot.uaDateTimeNow();
		ZonedDateTime birthdayDate;
		boolean isDisplayed;
		boolean change = false; //if there's a need to save down birthdays
		for (Birthday birthday : birthdays) {
			birthdayDate = birthday.birthdayDate();
			isDisplayed = birthday.isDisplayed();
			boolean isToday =
					now.getYear() == birthdayDate.getYear() &&
					now.getDayOfYear() == birthdayDate.getDayOfYear();
			if (isToday && !isDisplayed) {
				change = true;
				birthday.setIsDisplayed(true);
				todayBirthdays.add(birthday);
			}
		}
		
		if (change) Birthday.writeBirthdays(birthdays);
		
		if (todayBirthdays.isEmpty()) return null;
		else return todayBirthdays;
	}
	public static List<Birthday> expiredBirthdays() throws IOException {
		List<Birthday> birthdays = readBirthdays();
		List<Birthday> expiredBirthdays = new ArrayList<Birthday>();
		ZonedDateTime now = Bot.uaDateTimeNow();
		long nowEpoch = now.toEpochSecond();
		long birthdayEpoch;
		boolean isDisplayed;
		ZonedDateTime birthdayDate;
		boolean change = false; //if there's a need to save birthdays
		for (Birthday birthday : birthdays) {
			birthdayDate = birthday.birthdayDate();
			birthdayEpoch = birthdayDate.toEpochSecond();
			isDisplayed = birthday.isDisplayed();
			
			boolean hasPassed = nowEpoch >= birthdayEpoch;
			boolean isThisYear =
					now.getYear() == birthdayDate.getYear() &&
					now.getDayOfYear() != birthdayDate.getDayOfYear();
			boolean isLaterYear = now.getYear() > birthdayDate.getYear();
			
			boolean expired = hasPassed && (isThisYear || isLaterYear);
			if (expired && !isDisplayed) {
				change = true;
				birthday.setIsDisplayed(true);
				expiredBirthdays.add(birthday);
			}
		}
		
		if (change) Birthday.writeBirthdays(birthdays);
		
		if (expiredBirthdays.isEmpty()) return null;
		else return expiredBirthdays;
	}
}

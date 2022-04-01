package tgAyeBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Birthday implements BirthdayInterface {
	private long authorId;
	private String authorName;
	private long contactId;
	private ZonedDateTime birthdayDate;
	private String text;
	
	Birthday(long authorId, String authorName, long contactId, ZonedDateTime birthdayDate, String text) {
		setAuthorId(authorId);
		setAuthorName(authorName);
		setContactId(contactId);
		setBirthdayDate(birthdayDate);
		setText(text);
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
	public ZonedDateTime birthdayDate() {
		return this.birthdayDate;
	}
	@Override
	public String text() {
		return this.text;
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
	public void setBirthdayDate(ZonedDateTime birthdayDate) {
		this.birthdayDate = birthdayDate;
	}
	@Override
	public void setText(String text) {
		this.text = text;
	}
	
	
	public static List<Birthday> readBirthdays() throws FileNotFoundException {
		ZoneId zoneId = ZoneId.of("Europe/Kiev");
		List<Birthday> birthdays = new ArrayList<Birthday>();
		List<String> lines = TextFile.readLines(Resource.birthdays.path);
		String authorIdLine;
		String authorNameLine;
		String contactIdLine;
		String birthdayDateLine;
		String textLine;
		
		long authorId = 0;
		String authorName = null;
		long contactId = 0;
		ZonedDateTime birthdayDate = null;
		String text = null;
		int counter = 0;
		for (String line : lines) {
			switch (counter) {
			case 0:
				authorIdLine = line;
				authorId = Long.parseLong(authorIdLine);
				counter++;
				break;
			case 1:
				authorNameLine = line;
				authorName = authorNameLine;
				counter++;
				break;
			case 2:
				contactIdLine = line;
				contactId = Long.parseLong(contactIdLine);
				counter++;
				break;
			case 3:
				birthdayDateLine = line;
				long birthdayDateUnix = Long.parseLong(birthdayDateLine);
				Instant instant = Instant.ofEpochSecond(birthdayDateUnix);
				birthdayDate = ZonedDateTime.ofInstant(instant, zoneId);
				counter++;
				break;
			case 4:
				textLine = line;
				text = textLine;
				Birthday birthday = new Birthday(authorId, authorName, contactId, birthdayDate, text);
				
				birthdays.add(birthday);
				counter = 0;
				break;
			}
		}
		return birthdays;
	}
	
	public static void writeBirthdays(List<Birthday> birthdays) throws IOException {
		List<String> lines = new ArrayList<String>();
		String authorIdLine;
		String authorNameLine;
		String contactIdLine;
		String birthdayDateLine;
		String textLine;
		for (Birthday birthday : birthdays) {
			authorIdLine = Long.toString( birthday.authorId() );
			
			authorNameLine = birthday.authorName();
			
			contactIdLine = Long.toString( birthday.contactId() );
			
			long birthdayDateUnix = birthday.birthdayDate().toEpochSecond();
			birthdayDateLine = Long.toString(birthdayDateUnix);
			
			textLine = birthday.text();
			
			lines.add(authorIdLine);
			lines.add(authorNameLine);
			lines.add(contactIdLine);
			lines.add(birthdayDateLine);
			lines.add(textLine);
			
			String filePath = Resource.birthdays.path;
			TextFile.writeLines(filePath, lines, false);
		}
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
	
	public static List<Birthday> todayBirthdays() throws FileNotFoundException {
		List<Birthday> birthdays = readBirthdays();
		List<Birthday> todayBirthdays = new ArrayList<Birthday>();
		ZonedDateTime now = Bot.uaDateTimeNow();
		ZonedDateTime birthdayDate;
		for (Birthday birthday : birthdays) {
			birthdayDate = birthday.birthdayDate();
			boolean isToday =
					now.getYear() == birthdayDate.getYear() &&
					now.getDayOfYear() == birthdayDate.getDayOfYear();
			if (isToday) todayBirthdays.add(birthday);
		}
		
		if (todayBirthdays.isEmpty()) return null;
		else return todayBirthdays;
	}
	public static List<Birthday> expiredBirthdays() throws FileNotFoundException {
		List<Birthday> birthdays = readBirthdays();
		List<Birthday> expiredBirthdays = new ArrayList<Birthday>();
		ZonedDateTime now = Bot.uaDateTimeNow();
		long nowEpoch = now.toEpochSecond();
		long birthdayEpoch;
		ZonedDateTime birthdayDate;
		for (Birthday birthday : birthdays) {
			birthdayDate = birthday.birthdayDate();
			birthdayEpoch = birthdayDate.toEpochSecond();
			boolean hasPassed = nowEpoch >= birthdayEpoch;
			boolean isThisYear =
					now.getYear() == birthdayDate.getYear() &&
					now.getDayOfYear() != birthdayDate.getDayOfYear();
			boolean isLaterYear = now.getYear() > birthdayDate.getYear();
			
			boolean expired = hasPassed && (isThisYear || isLaterYear);
			if (expired) expiredBirthdays.add(birthday);
		}
		
		if (expiredBirthdays.isEmpty()) return null;
		else return expiredBirthdays;
	}
}

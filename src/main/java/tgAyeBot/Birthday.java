package tgAyeBot;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Birthday implements BirthdayInterface {
	private String authorName;
	private long contactId;
	private ZonedDateTime birthdayDate;
	private String text;
	
	Birthday(String authorName, long contactId, ZonedDateTime birthdayDate, String text) {
		setAuthorName(authorName);
		setContactId(contactId);
		setBirthdayDate(birthdayDate);
		setText(text);
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
		String authorNameLine;
		String contactIdLine;
		String birthdayDateLine;
		String textLine;
		
		String authorName = null;
		long contactId = 0;
		ZonedDateTime birthdayDate = null;
		String text = null;
		int counter = 0;
		for (String line : lines) {
			switch (counter) {
			case 0:
				authorNameLine = line;
				authorName = authorNameLine;
				counter++;
				break;
			case 1:
				contactIdLine = line;
				contactId = Long.parseLong(contactIdLine);
				counter++;
				break;
			case 2:
				birthdayDateLine = line;
				long birthdayDateUnix = Long.parseLong(birthdayDateLine);
				Instant instant = Instant.ofEpochSecond(birthdayDateUnix);
				birthdayDate = ZonedDateTime.ofInstant(instant, zoneId);
				counter++;
				break;
			case 3:
				textLine = line;
				text = textLine;
				Birthday birthday = new Birthday(authorName, contactId, birthdayDate, text);
				
				birthdays.add(birthday);
				counter = 0;
				break;
			}
		}
		return birthdays;
	}
}

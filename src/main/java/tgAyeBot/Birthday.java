package tgAyeBot;

import java.time.ZonedDateTime;

public class Birthday implements BirthdayInterface {
	private long contactId;
	private ZonedDateTime birthdayDate;
	private String text;
	
	Birthday(long contactId, ZonedDateTime birthdayDate, String text) {
		setContactId(contactId);
		setBirthdayDate(birthdayDate);
		setText(text);
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

}

package tgAyeBot;

import java.time.ZonedDateTime;

public class BirthdaySession extends Session {
	private long contactId = 0;
	private ZonedDateTime birthdayDate = null;
	private String text = "";
	
	BirthdaySession(long authorId, ZonedDateTime created) {
		super(authorId, created);
	}
	
	public long contactId() {
		return this.contactId;
	}
	public ZonedDateTime birthdayDate() {
		return this.birthdayDate;
	}
	public String text() {
		return this.text;
	}
	
	public void setContactId(long contactId) {
		this.contactId = contactId;
	}
	public void setBirthdayDate(ZonedDateTime birthdayDate) {
		this.birthdayDate = birthdayDate;
	}
	public void setTest(String text) {
		this.text = text;
	}
}
package tgAyeBot;

import java.time.ZonedDateTime;

public class BirthdaySession extends Session {
	private long contactId = 0;
	private String date = "";
	private String text = "";
	
	BirthdaySession(long authorId, ZonedDateTime created) {
		super(authorId, created);
	}
	
	public long contactId() {
		return this.contactId;
	}
	public String date() {
		return this.date;
	}
	public String text() {
		return this.text;
	}
	
	public void setContactId(long contactId) {
		this.contactId = contactId;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setTest(String text) {
		this.text = text;
	}
}
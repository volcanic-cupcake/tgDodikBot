package tgAyeBot;

import java.time.ZonedDateTime;

public class BirthdaySession extends Session implements BirthdayInterface{
	private long contactId = 0;
	private ZonedDateTime birthdayDate = null;
	private String text = "";
	
	BirthdaySession(long authorId, ZonedDateTime created) {
		super(authorId, created);
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
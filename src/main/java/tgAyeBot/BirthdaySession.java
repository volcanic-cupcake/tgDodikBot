package tgAyeBot;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class BirthdaySession extends Session implements BirthdayInterface{
	
	private String authorName;
	private long contactId = 0;
	private ZonedDateTime birthdayDate = null;
	private String text = "";
	
	BirthdaySession(ZonedDateTime created, long authorId, String authorName) {
		super(authorId, created);
		setAuthorName(authorName);
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
	
	public Birthday toBirthday() {
		long authorId = authorId();
		String authorName = authorName();
		long contactId = contactId();
		ZonedDateTime birthdayDate = birthdayDate();
		String text = text();
		
		Birthday birthday = new Birthday(authorId, authorName, contactId, birthdayDate, text);
		return birthday;
	}
}
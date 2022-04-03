package tgAyeBot;

import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class SetBirthdaySession extends Session implements BirthdayInterface{
	
	private String authorName;
	
	private long contactId = 0;
	private String contactName = null;
	private ZonedDateTime birthdayDate = null;
	private String text = null;
	
	SetBirthdaySession(ZonedDateTime created, long authorId, String authorName) {
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
	
	public Birthday toBirthday(boolean anonymous) throws FileNotFoundException {
		
		long authorId = authorId();
		long contactId = contactId();
		String contactName = contactName();
		ZonedDateTime birthdayDate = birthdayDate();
		String text = text();
		
		String authorName;
		if (anonymous) authorName = "АНОНІМУС";
		else authorName = authorName();
		
		Birthday birthday = new Birthday(authorId, authorName, contactId, contactName, birthdayDate, text);
		return birthday;
	}
}
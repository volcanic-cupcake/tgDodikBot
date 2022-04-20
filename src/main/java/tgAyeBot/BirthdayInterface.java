package tgAyeBot;

import java.time.ZonedDateTime;

import tgAyeBot.Birthday.Privacy;


public interface BirthdayInterface {
	
	public String code();
	public Privacy privacy();
	public long authorId();
	public String authorName();
	public long contactId();
	public String contactName();
	public ZonedDateTime birthdayDate();
	public String text();
	
	public void setCode(String code);
	public void setPrivacy(Privacy privacy);
	public void setAuthorId(long authorId);
	public void setAuthorName(String authorName);
	public void setContactId(long contactId);
	public void setContactName(String contactName);
	public void setBirthdayDate(ZonedDateTime birthdayDate);
	public void setText(String text);
}

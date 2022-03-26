package tgAyeBot;

import java.time.ZonedDateTime;

public interface BirthdayInterface {
	
	public long contactId();
	public ZonedDateTime birthdayDate();
	public String text();
	
	public void setContactId(long contactId);
	public void setBirthdayDate(ZonedDateTime birthdayDate);
	public void setText(String text);
}

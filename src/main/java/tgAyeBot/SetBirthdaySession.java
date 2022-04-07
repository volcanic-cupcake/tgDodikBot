package tgAyeBot;

import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SetBirthdaySession extends Session implements BirthdayInterface{
	
	private String code;
	private String authorName;
	
	
	private long contactId = 0;
	private String contactName = null;
	
	private ZonedDateTime birthdayDate = null;
	private String text = null;
	
	SetBirthdaySession(ZonedDateTime created, long authorId, String authorName) {
		super(authorId, created);
		setCode( generateCode() );
		setAuthorName(authorName);
	}
	
	
	@Override
	public String code() {
		return this.code;
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
	public void setCode(String code) {
		this.code = code;
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
	
	private String generateCode() {
		final int LENGTH = 10;
		List<Birthday> birthdays = null;
		try {	birthdays = Birthday.readBirthdays();	}
		catch (FileNotFoundException e) {}
		
		String lowercase = "abcdefghijklmnopqrstuvwxyz";
		String uppercase = lowercase.toUpperCase();
		String all = lowercase + uppercase;
		
		char[] alphabet = all.toCharArray();
		Random random = new Random();
		
		String code = "";
		for (int i = 0; i < LENGTH; i++) {
			int rndIndex = random.nextInt(alphabet.length);
			code += alphabet[rndIndex];
		}
		
		//check if it's unique
		boolean unique = true;
		for (Birthday birthday : birthdays) {
			String birthdayCode = birthday.code();
			if (birthdayCode.contentEquals(code)) {
				unique = false;
				break;
			}
		}
		
		if (unique) return code;
		else return generateCode();
	}
	
	public Birthday toBirthday(boolean anonymous) throws FileNotFoundException {
		
		String code = code();
		long authorId = authorId();
		long contactId = contactId();
		String contactName = contactName();
		ZonedDateTime birthdayDate = birthdayDate();
		String text = text();
		
		String authorName;
		if (anonymous) authorName = "АНОНІМУС";
		else authorName = authorName();
		
		Birthday birthday = new Birthday(code, authorId, authorName, contactId, contactName, birthdayDate, text, false);
		return birthday;
	}
}
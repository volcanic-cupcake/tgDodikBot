package tgAyeBot;

import java.time.ZonedDateTime;

public class SetInsultSession extends Session implements InsultInterface{
	
	private String authorName;
	
	private String text = null;
	private boolean confirmed = false;
	
	SetInsultSession (ZonedDateTime created, long authorId, String authorName) {
		super(authorId, created);
		setAuthorName(authorName);
	}
	
	@Override
	public String authorName() {
		return this.authorName;
	}
	@Override
	public String text() {
		return this.text;
	}
	public boolean confirmed() {
		return this.confirmed;
	}
	
	@Override
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	@Override
	public void setText(String text) {
		this.text = text;
	}
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	
	public Insult toInsult(boolean anonymous) {
		long authorId = authorId();
		String text = text();
		
		String authorName;
		if (anonymous) authorName = "АНОНІМУС";
		else authorName = authorName();
		
		Insult insult = new Insult(authorId, authorName, text);
		return insult;
	}
}
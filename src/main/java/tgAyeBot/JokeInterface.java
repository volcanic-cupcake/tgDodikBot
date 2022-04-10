package tgAyeBot;

public interface JokeInterface {
	public long authorId();
	public String authorName();
	public String text();
	
	public void setAuthorId(long authorId);
	public void setAuthorName(String authorName);
	public void setText(String text);
}
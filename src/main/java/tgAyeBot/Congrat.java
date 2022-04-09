package tgAyeBot;

public class Congrat {
	private long chatId;
	private String text;
	
	Congrat(long chatId, String text) {
		setChatId(chatId);
		setText(text);
	}
	
	public long chatId() {
		return this.chatId;
	}
	public String text() {
		return this.text;
	}
	
	public void setChatId(long chatId) {
		this.chatId = chatId;
	}
	public void setText(String text) {
		this.text = text;
	}
}

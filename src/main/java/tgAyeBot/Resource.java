package tgAyeBot;

public enum Resource {
	TOKEN("src/main/resources/TOKEN.txt"),
	birthdays("src/main/resources/birthdays.txt"),
	chats("src/main/resources/chats.txt"),
	anonymousMode("src/main/resources/anonymousMode.txt");
	
	String path;
	Resource(String path) {
		this.path = path;
	}
}

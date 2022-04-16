package tgAyeBot;

public enum Resource {
	TOKEN("src/main/resources/TOKEN.txt"),
	birthdays("src/main/resources/birthdays.txt"),
	jokes("src/main/resources/jokes.txt"),
	chats("src/main/resources/chats.txt"),
	anonymousMode("src/main/resources/anonymousMode.txt"),
	banned("src/main/resources/banned.txt"),
	updateNotification("src/main/resources/updateNotification.txt");
	
	String path;
	Resource(String path) {
		this.path = path;
	}
}

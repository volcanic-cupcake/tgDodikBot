package tgAyeBot;

public enum Resource {
	TOKEN("src/main/resources/TOKEN.txt");
	
	String path;
	Resource(String path) {
		this.path = path;
	}
}

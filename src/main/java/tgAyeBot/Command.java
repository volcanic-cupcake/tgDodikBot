package tgAyeBot;

import java.io.FileNotFoundException;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

public class Command {
	enum Type {
		PRIVATE, GROUP, PRIVATE_AND_GROUP;
	}
	private Type type;
	private String command;
	
	public Command(Type type, String command) {
		setType(type);
		setCommand(command);
	}
	
	public Type type() {
		return this.type;
	}
	public String command() {
		return this.command;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
	//this is supposed to be overridden
	public void execute(Message message) throws FileNotFoundException {
		
	}
}

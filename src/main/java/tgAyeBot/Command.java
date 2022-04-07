package tgAyeBot;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

public class Command {
	enum CommandType {
		PRIVATE, GROUP, PRIVATE_AND_GROUP;
	}
	public static Command[] commands;
	
	private CommandType type;
	private boolean terminatesSessions;
	private String command;
	
	public Command(CommandType type, boolean terminatesSessions, String command) {
		setType(type);
		setTerminatesSessions(terminatesSessions);
		setCommand(command);
	}
	
	public CommandType type() {
		return this.type;
	}
	public boolean terminatesSessions() {
		return this.terminatesSessions;
	}
	public String command() {
		return this.command;
	}
	
	public void setType(CommandType type) {
		this.type = type;
	}
	public void setTerminatesSessions(boolean terminatesSessions) {
		this.terminatesSessions = terminatesSessions;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
	
	//this is supposed to be overridden
	public void execute(Message message) {
		
	}
	
	
}

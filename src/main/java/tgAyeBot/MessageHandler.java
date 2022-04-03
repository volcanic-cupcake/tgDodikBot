package tgAyeBot;

import com.pengrad.telegrambot.model.Message;

import tgAyeBot.Command.CommandType;

public class MessageHandler {
	private static Bot bot;
	private static Command[] commands;
	
	private Message message;
	
	public MessageHandler(Message message) {
		this.message = message;
	}
	
	public void Private() {
		for (Command command : commands) {
			
		}
	}
	public void group() {
		
	}
	
	public static void setBot(Bot newBot) {
		bot = newBot;
	}
	public static void setCommands(Command[] newCommands) {
		//I need to make it split it into private group and privateandgroup
		commands = newCommands;
	}
}

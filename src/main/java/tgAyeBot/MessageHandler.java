package tgAyeBot;

import java.util.ArrayList;
import java.util.List;

import com.pengrad.telegrambot.model.Message;



public abstract class MessageHandler {
	private static Bot bot;
	private static List<Command> Private;
	private static List<Command> group;
	
	
	public static void Private(Message message) {
		String text = message.text();
		if (text != null) {
			privateCommands(message);
		}
	}
	public static void group(Message message) {
		String text = message.text();
		if (text != null) {
			groupCommands(message);
		}
	}
	
	public static void setBot(Bot newBot) {
		bot = newBot;
	}
	public static void setCommands(Command[] commands) {
			
		List<Command> privateList = new ArrayList<Command>();
		List<Command> groupList = new ArrayList<Command>();

		for (Command command : commands) {
			switch (command.type()) {
			case PRIVATE:
				privateList.add(command);
				break;
			case GROUP:
				groupList.add(command);
				break;
			case PRIVATE_AND_GROUP:
				privateList.add(command);
				groupList.add(command);
				break;
			}
		}
		
		Private = privateList;
		group = groupList;
	}
	
	
	private static void privateCommands(Message message) {
		String inputText = message.text();
		String text = inputText.strip();
		
		for (Command command : Private) {
			String commandText = command.command();
			if ( commandText.contentEquals(text) ) {
				command.execute(message);
				break;
			}
		}
	}
	private static void groupCommands(Message message) {
		String inputText = message.text();
		String text = inputText.strip();
		
		for (Command command : group) {
			String commandText = command.command() + bot.username();
			if ( commandText.contentEquals(text) ) {
				command.execute(message);
				break;
			}
		}
	}
}

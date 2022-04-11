package tgAyeBot;




import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat.Type;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendContact;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import com.pengrad.telegrambot.response.GetMeResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import tgAyeBot.Command.CommandType;


public class Bot extends TelegramBot {
	
	private final User ME = botGetMe(); 
	private final String USERNAME = "@" + ME.username();
	List<BotChat> chats = new ArrayList<BotChat>();
	List<Long> banned = readBanned();
	
	public Bot(String token) {
		super(token);
	}
	
	public static ZonedDateTime uaDateTimeNow() {
		Instant instant = Instant.now();
		ZoneId zoneId = ZoneId.of("Europe/Kiev");
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zoneId);
		return zdt;
	}
	public static ZonedDateTime uaDateTime(String dateString) { //accepts dd.MM format
		dateString = dateString.strip();
		int dayInput = 0;
		int monthInput = 0;
		try {
			dayInput = Integer.parseInt(dateString.substring(0, 2));
			monthInput = Integer.parseInt(dateString.substring(3));
		}
		catch (NumberFormatException | StringIndexOutOfBoundsException e) {
			return null;
		}
		
		ZoneId zoneId = ZoneId.of("Europe/Kiev");
		ZonedDateTime now = uaDateTimeNow();
		
		int monthNow = now.getMonthValue();
		int dayNow = now.getDayOfMonth();
		boolean isThisYear = monthInput > monthNow || (monthInput == monthNow && dayInput > dayNow);
		
		int year;
		if (isThisYear) year = now.getYear();
		else year = now.getYear() + 1;
		
		ZonedDateTime zdt;
		try {
			zdt = ZonedDateTime.of(year, monthInput, dayInput, 0, 0, 0, 0, zoneId);
		}
		catch (DateTimeException e) {
			zdt = null;
		}
		return zdt;
	}
	
	public static long congratulateDelay(int hour) {
		ZonedDateTime now = uaDateTimeNow();
		long nowEpoch = now.toEpochSecond();
		int i = 0;
		while (now.plusHours(i).getHour() != hour) {
			i++;
		}
		
		long targetEpoch = now.plusHours(i).toEpochSecond();
		long difference = targetEpoch - nowEpoch;
		long toMilli = difference * 1000;
		
		return toMilli;
	}
	
	public SendMessage helpMessage(long chatId) {
		String text =
				"🔶УСІ ЧАТИ🔶\n"
				+ "/help - чит на легендарки бравл старс\n"
				+ "/joke - рандомний анекдот від користувача\n"
				+ "/youtube - плейлист з поясненням на Ютубі\n"
				+ "/privacy - які данні я збираю\n"
				+ "/info - інформація про проект\n"
				+ "/creator - автор бота\n"
				+ "/report - повідомити про порушення\n"
				+ "/github - репозиторій на GitHub\n"
				+ "/russian_warship - класика\n"
				+ "\n"
				
				+ "🔶ДИРЕКТ ЗІ МНОЮ🔶\n"
				+ "/cancel - відмінити попередню операцію\n"
				+ "/anonymous - анонімний режим\n"
				+ "/setbirthday - зберегти привітання\n"
				+ "/mybirthdays - управління привітаннями\n"
				+ "/setjoke - додати анекдот до спільного сховища\n"
				+ "\n"
				
				+ "🔶ГРУПОВІ ЧАТИ🔶\n"
				+ "/insult - образити рандомну людину\n"
				+ "\n"
				
				+ "❗️Зверніть увагу❗️\n"
				+ "якщо ви будете зловживати деякими функціями, вам буде назавжди заборонено "
				+ "користуватися ботом\n";
		SendMessage send = new SendMessage(chatId, text);
		return send;
	}
	public void sendMyBirthdays(long fromId, long chatId) throws FileNotFoundException {
		List<Birthday> allBirthdays = Birthday.readBirthdays();
		List<Birthday> myBirthdays = new ArrayList<Birthday>();
		for (Birthday birthday : allBirthdays) {
			boolean authorMatch = birthday.authorId() == fromId;
			boolean isDisplayed = birthday.isDisplayed();
			if (authorMatch && !isDisplayed) myBirthdays.add(birthday);
		}
		
		String output;
		if (myBirthdays.isEmpty()) {
			output = "Пусто, прямо як у москаляки в голові!";
		}
		else {
			output = myBirthdaysToText(myBirthdays);
		}
		
		List<Congrat> list = new ArrayList<Congrat>();
		Congrat congrats = new Congrat(chatId, output);
		list.add(congrats);
		
		secureCongratsSend(list);
	}
	
	public Command[] commands() {
		Bot bot = this;
		//List<Command> commands = new ArrayList<Command>();
		
		Command help = new Command(CommandType.PRIVATE_AND_GROUP, true, "/help") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				SendMessage send = helpMessage(chatId);
				bot.execute(send);
			}
		};
		
		Command joke = new Command(CommandType.PRIVATE_AND_GROUP, true, "/joke") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				List<Joke> jokes = null;
				try {
					jokes = Joke.readJokes();
				} catch (FileNotFoundException e) {}
				
				Random random = new Random();
				int rndIndex = random.nextInt( jokes.size() );
				Joke joke = jokes.get(rndIndex);
				
				String messageText =
						  "🔸" + joke.authorName() + "🔸\n"
						+ "\n"
						+ joke.text();
				SendMessage send = new SendMessage(chatId, messageText);
				bot.execute(send);
			}
		};
		
		Command privacy = new Command(CommandType.PRIVATE_AND_GROUP, true, "/privacy") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				String text =
						  "Використовуючи мене, ви погоджуєтеся, що я зберігаю певну інформацію про Вас:\n"
						+ "\n"
						+ "🔹данні про чат\n"
						+ "🔹список учасників чату\n"
						+ "🔹користувачі, які вступають до чату\n"
						+ "🔹користувачі, які покидають чат\n"
						+ "_ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n\n"
						
						+ "Я в жодному разі НЕ зберігаю:\n"
						+ "\n"
						+ "🔻повідомлення\n"
						+ "🔻пароль від вашого акаунту бравл старс\n";
				SendMessage send = new SendMessage(chatId, text);
				bot.execute(send);
			}
		};
		
		Command creator = new Command(CommandType.PRIVATE_AND_GROUP, true, "/creator") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				String phoneNumber = "+380672704424";
				String firstName = "Єгор";
				SendContact contact = new SendContact(chatId, phoneNumber, firstName);
				bot.execute(contact);
			}
		};
		
		Command info = new Command(CommandType.PRIVATE_AND_GROUP, true, "/info") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				String text =
						  "Мене звати Єгор, мені 16 років і я автор цього бота :D"
						+ "\n\n"
						+ "🔸цей проект має лише розважальний характер"
						+ "\n\n"
						+ "🔸проект є повністю open-source"
						+ "\n\n"
						+ "🔸мова програмування: Java"
						+ "\n\n"
						+ "🔸ви можете вільно використовувати мою роботу"
						+ "\n\n"
						+ "🔸отримати початковий код можна тут /github"
						+ "\n\n"
						+ "🔸прошу повідомляти мені про помилки /creator"
						+ "\n\n"
						+ "🔸путін хуйло"
						+ "\n\n"
						+ "Дякую за увагу!";
				SendMessage send = new SendMessage(chatId, text);
				bot.execute(send);
			}
		};
		
		Command report = new Command(CommandType.PRIVATE_AND_GROUP, true, "/report") {
			@Override
			public void execute (Message message) {
				long chatId = message.chat().id();
				String text =
						  "Прохання про усі проблеми повідомляти мені у телеграм\n"
						+ "/creator\n";
				SendMessage send = new SendMessage(chatId, text);
				bot.execute(send);
			}
		};
		
		Command github = new Command(CommandType.PRIVATE_AND_GROUP, true, "/github") {
			@Override
			public void execute (Message message) {
				long chatId = message.chat().id();
				String link = "https://github.com/volcanic-cupcake/tgDodikBot";
				SendMessage send = new SendMessage(chatId, link)
						.disableWebPagePreview(true);
				bot.execute(send);
			}
		};
		
		Command youtube = new Command(CommandType.PRIVATE_AND_GROUP, true, "/youtube") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				String link = "https://youtube.com/playlist?list=PL8C2JQ0S1cyNUdJE2PKJDmBq_l0Ibcwon";
				SendMessage send = new SendMessage(chatId, link)
						.disableWebPagePreview(true);
				bot.execute(send);
			}
		};
		
		Command russian_warship = new Command(CommandType.PRIVATE_AND_GROUP, true, "/russian_warship") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				int messageId = message.messageId();
				String text = "Русский военный корабль, иди нахуй!";
				SendMessage send = new SendMessage(chatId, text)
						.replyToMessageId(messageId);
				bot.execute(send);
			}
		};
		
		Command start = new Command(CommandType.PRIVATE, true, "/start") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				SendMessage send = helpMessage(chatId);
				bot.execute(send);
			}
		};
		
		Command cancel = new Command(CommandType.PRIVATE, true, "/cancel") {
			@Override
			public void execute(Message message) {
				int messageId = message.messageId();
				long chatId = message.chat().id();
				
				SendMessage send = new SendMessage(chatId, "гаразд")
						.replyToMessageId(messageId);
				bot.execute(send);
			}
		};
		
		Command anonymous = new Command(CommandType.PRIVATE, false, "/anonymous") {
			@Override
			public void execute(Message message) {
				long fromId = message.from().id();
				long chatId = message.chat().id();
				List<Long> anonymous = null;
				try {
					anonymous = readAnonymous();
				} catch (FileNotFoundException e) {}
				
				boolean isEnabled = anonymous.contains(fromId);
				String output;
				if (isEnabled) {
					anonymous.remove(fromId);
					try {
						writeAnonymous(anonymous);
					} catch (IOException e) {}
					
					output = "Ви більше не анонімус :o"; 
				}
				else {
					anonymous.add(fromId);
					try {
						writeAnonymous(anonymous);
					} catch (IOException e) {}
					
					output = "Ви тепер анонімус :D";
				}
				
				SendMessage send = new SendMessage(chatId, output);
				bot.execute(send);
			}
		};
		
		Command my_birthdays = new Command(CommandType.PRIVATE, true, "/mybirthdays") {
			@Override
			public void execute(Message message) {
				long fromId = message.from().id();
				long chatId = message.chat().id();
				
				try {
					sendMyBirthdays(fromId, chatId);
				} catch (FileNotFoundException e) {}
				
			}
		};
		
		Command set_birthday = new Command(CommandType.PRIVATE, true, "/setbirthday") {
			@Override
			public void execute(Message message) {
				User from = message.from();
				long fromId = from.id();
				long chatId = message.chat().id();
				
				String fullName = "";
				String firstName = from.firstName();
				String lastName = from.lastName();
				if (firstName != null) fullName += firstName;
				if (lastName != null) fullName += " " + lastName;
				ZonedDateTime now = uaDateTimeNow();
				
				List<SetBirthdaySession> bdaySessions = SessionStore.setBirthday();
				
				SetBirthdaySession newSession = new SetBirthdaySession(now, fromId, fullName);
				bdaySessions.add(newSession);
				
				String text =
						 	"Будь ласка, надішліть мені контакт друга, якого ми привітаємо\n"
						  + "\n"
						  + "Також можна переслати сюди будь-яке його повідомлення :)\n"
						  + "\n"
						  + "Пам'ятайте! Я надішлю ваше привітання у чати, де побачу "
						  + "цю людину, навіть якщо вас там немає.\n"
						  + "\n"
						  + "Також усім відобразиться ваше ім'я, але ви можете увімкнути "
						  + "анонімний режим командою /anonymous\n"
						  + "\n"
						  + "Якщо ви вкажете сьогоднішню дату, привітання прийде "
						  + "наступного року, адже про День Народження друзів треба "
						  + "пам'ятати заздалегідь :o";
				SendMessage send = new SendMessage(chatId, text);
				bot.execute(send);
			}
		};
		
		Command set_joke = new Command(CommandType.PRIVATE, true, "/setjoke") {
			@Override
			public void execute(Message message) {
				User from = message.from();
				long fromId = from.id();
				long chatId = message.chat().id();
				
				String fullName = getUserFullName(from);
				ZonedDateTime now = uaDateTimeNow();
				
				List<SetJokeSession> jokeSessions = SessionStore.setJoke();
				
				SetJokeSession newSession = new SetJokeSession(now, fromId, fullName);
				jokeSessions.add(newSession);
				
				String text =
					 	"Будь ласка, надішліть мені будь-який анекдот :D\n"
					  + "Максимальна кількість символів: 3700\n"
					  + "\n"
					  + "Пам'ятайте, усім відобразиться ваше ім'я, але ви можете умімкнути "
					  + "анонімний режим командою /anonymous\n";
				SendMessage send = new SendMessage(chatId, text);
				bot.execute(send);
			}
		};
		
		Command insult = new Command(CommandType.GROUP, true, "/insult") {
			@Override
			public void execute (Message message) {
				long chatId = message.chat().id();
				User user = randomChatMember(chatId).user();
				String fullName = getUserFullName(user);
				
				String offend1 = "ти випадково не граєш у Геншин?";
				String offend2 =
						    "щоб опуститися до твого рівня, "
						    + "мені потрібно провалитися крізь землю";
				String offend3 = "бачиш плінтус? Ось, це якраз твій рівень";
				String offend4 = "чудово пахнеш\n\nНаїбав. Ваняєш, як свинюка у багнюці!";
				String offend5 = "нехай твій батько надалі буде обережний. Потрібно берегтися, "
						+ "щоб на світ не з’являлися такі виродки, як ти";
				String offend6 = "своєю красою ти би явно світ не врятував";
				String offend7 = "ти погано себе почуваєш або виглядаєш так завжди?";
				String offend8 = "природа вирішила над тобою особливо не морочитися";
				String offend9 = "я б тебе образив, але думаю, тебе дзеркало кожен день ображає";
				String offend10 = "сподіваюся, ти не завжди такий дурний, а лише сьогодні";
				String offend11 = "тобою випадково в дитинстві Бабая не лякали?";
				String offend12 = "якби у тупості були крила, ти би пурхав, як метелик";
				String offend13 = "в вас з російським кораблем багато спільного. Ви обидва "
						+ "йдете нахуй.";
				String offend14 = "я навіть жарт вигадувати не буду, просто йди нахуй";
				String offend15 = "якщо б я не був ботом, начистив би тобі морду";
				String offend16 = "я спочатку рахував скільки разів тебе роняли у дитинстві, "
						+ "але на 100-му разі збився";
				String offend17 = "йди на три хуя, ти пизда нетрахана.";
				String offend18 = "був би ти негром, я б тебе відразу продав";
				String offend19 = "від тебе лайном ваняє";
				String offend20 = "ти мене бісиш, йди втопися";
				
				String offends[] = {
						offend1, offend2, offend3, offend4, offend5, offend6,
						offend7, offend8, offend9, offend10, offend11, offend12,
						offend13, offend14, offend15, offend16, offend17, offend18,
						offend19, offend20
				};
				
				Random random = new Random();
				int rndIndex = random.nextInt(offends.length);
				
				String text = fullName + ", " + offends[rndIndex];
				SendMessage send = new SendMessage(chatId, text);
				bot.execute(send);
			}
		};
		
		Command[] commands = {
				help, creator, info, report, github, youtube, russian_warship, start,
				cancel, anonymous,
				set_birthday, my_birthdays, set_joke, joke, privacy, insult
		};
		return commands;
	}
	
	public static List<Long> readAnonymous() throws FileNotFoundException {
		List<String> lines = TextFile.readLines(Resource.anonymousMode.path);
		List<Long> ids = new ArrayList<Long>();
		for (String line : lines) {
			ids.add( Long.parseLong(line) );
		}
		return ids;
	}
	public static List<Long> readBanned() {
		List<String> lines = null;
		try {	lines = TextFile.readLines(Resource.banned.path);	}
		catch (FileNotFoundException e) {}
		
		List<Long> ids = new ArrayList<Long>();
		for (String line : lines) {
			ids.add( Long.parseLong(line) );
		}
		return ids;
	}
	
	public static void writeAnonymous(List<Long> ids) throws IOException {
		List<String> lines = new ArrayList<String>();
		String line;
		for (long id : ids) {
			line = Long.toString(id);
			lines.add(line);
		}
		TextFile.writeLines(Resource.anonymousMode.path, lines, false);
	}

	public void secureTextSend(long chatId, String text) {
		final int MAX_LENGTH = 4096;
		
		if (text.length() <= MAX_LENGTH) {
			SendMessage send = new SendMessage(chatId, text);
			this.execute(send);
		}
		else { //sending multiple messages
			Bot bot = this;
			new Thread(() -> {
				String localText = text;
				
				int num = localText.length() / MAX_LENGTH;
				int leftNum = localText.length() % MAX_LENGTH;
				
				String temp;
				for (int i = 0; i < num; i++) {
					temp = localText.substring(0, MAX_LENGTH);
					localText = localText.substring(MAX_LENGTH, localText.length());
					
					SendMessage send = new SendMessage(chatId, temp);
					this.execute(send);
					
					try {	Thread.sleep(10000);	}
					catch (InterruptedException e) {}
				}
				
				if (leftNum != 0) {
					SendMessage send = new SendMessage(chatId, localText);
					bot.execute(send);
				}
			}).start();
		}
	}
	
	private void secureCongratsSend(List<Congrat> congrats) {
		Bot bot = this;
		new Thread(() -> {
			final int MAX_LENGTH = 4096;
			final int SLEEP_TIME = 10000;
			for (Congrat congrat : congrats) {
				
				long chatId = congrat.chatId();
				String text = congrat.text();
				
				if (text.length() <= MAX_LENGTH) {
					SendMessage send = new SendMessage(chatId, text);
					bot.execute(send);
					try {	Thread.sleep(SLEEP_TIME);	}
					catch (InterruptedException e) {}
				}
				else {
					String[] lines = text.split("\n");
					String temp = "";
					for (int i = 0; i < lines.length; i++) {
						String line = lines[i];
						temp += line + "\n";
						
						boolean lastIteration = i == lines.length - 1;
						boolean isLongEnough = temp.length() > 3700;
						
						if (!lastIteration && isLongEnough) {
							SendMessage send = new SendMessage(chatId, temp);
							bot.execute(send);
							temp = "";
							
							try {	Thread.sleep(SLEEP_TIME);	}
							catch (InterruptedException e) {}
						}
						else if (lastIteration) {
							SendMessage send = new SendMessage(chatId, temp);
							bot.execute(send);
							
							try {	Thread.sleep(SLEEP_TIME);	}
							catch (InterruptedException e) {}
						}		
					}
				}
			}
		}).start();
	}
	
	public boolean userIsAnonymous(long userId) throws FileNotFoundException {
		List<Long> anonymousList = Bot.readAnonymous();
		boolean anonymous = anonymousList.contains(userId);
		return anonymous;
	}
	
	private boolean chatMemberExists(ChatMember member) {
		boolean isNull = member == null;
		boolean isLeft = false;
		boolean isKicked = false;
		
		if (!isNull) {
			switch(member.status()) {
			case left:
				isLeft = true;
				break;
			case kicked:
				isKicked = true;
				break;
			default:
				break;
			}
		}
		
		boolean exists = !isNull && !(isLeft || isKicked);
		return exists;
	}
	private boolean chatMemberExists(long chatId, long userId) {
		GetChatMember request = new GetChatMember(chatId, userId);
    	GetChatMemberResponse getChatMemberResponse = this.execute(request);
    	ChatMember member = getChatMemberResponse.chatMember();
		
		boolean isNull = member == null;
		boolean isLeft = false;
		boolean isKicked = false;
		
		if (!isNull) {
			switch(member.status()) {
			case left:
				isLeft = true;
				break;
			case kicked:
				isKicked = true;
				break;
			default:
				break;
			}
		}
		
		boolean exists = !isNull && !(isLeft || isKicked);
		return exists;
	}
	
	private User getChatUser(long chatId, long userId) {
		GetChatMember request = new GetChatMember(chatId, userId);
    	GetChatMemberResponse getChatMemberResponse = this.execute(request);
    	ChatMember member = getChatMemberResponse.chatMember();
    	
    	if (member != null) return member.user();
    	else return null;
	}
	
	private String getUserFullName(User user) {
		String fullName = "";
		String firstName = user.firstName();
		String lastName = user.lastName();
		if (lastName == null) fullName += firstName;
		else fullName += firstName + " " + lastName;
		
		return fullName;
	}
	
	private ChatMember randomChatMember(long chatId) {
		ChatMember member = null;
		for (BotChat chat : this.chats) {
			if (chat.id() == chatId) {
				List<Long> chatMembers = chat.members();
				member = pickRandomChatMember(chatId, chatMembers);
				break;
			}
		}
		
		return member;
	}
	private ChatMember pickRandomChatMember(long chatId, List<Long> chatMembers) {
		Random random = new Random();
		int rndIndex = random.nextInt(chatMembers.size());
		long rndMemberId = chatMembers.get(rndIndex);
		
		GetChatMember request = new GetChatMember(chatId, rndMemberId);
    	GetChatMemberResponse getChatMemberResponse = this.execute(request);
    	ChatMember member = getChatMemberResponse.chatMember();
    	
    	boolean exists = chatMemberExists(member);
    	boolean isBot = exists && member.user().isBot();
    	if (exists && !isBot) return member;
    	else return pickRandomChatMember(chatId, chatMembers);
	}
	public User botGetMe() {
		GetMe request = new GetMe();
		GetMeResponse getMeResponse = this.execute(request);
		User me = getMeResponse.user();
		return me;
	}
	
	
	public String username() {
		return this.USERNAME;
	}
	public User me() {
		return this.ME;
	}
	
	private String zdtToString(ZonedDateTime zdt) {
		
		String day = Integer.toString( zdt.getDayOfMonth() );
		String month = Integer.toString( zdt.getMonthValue() );
		String year = Integer.toString( zdt.getYear() );
		
		if (day.length() != 2) day = "0" + day;
		if (month.length() != 2) month = "0" + month;
		
		String date = day + "." + month + "." + year;
		return date;
	}
	private String myBirthdaysToText (List<Birthday> myBirthdays) {
		String separator = "_ _ _ _ _ _ _ _ _ _ _ _ _ _ _";
		int number = 1;
		String output = "";
		String date;
		String displayCommand;
		String removeCommand;
		for (Birthday birthday : myBirthdays) {
			date = zdtToString( birthday.birthdayDate() );
			displayCommand = "/birthdaytext_" + birthday.code();
			removeCommand = "/birthdayremove_" + birthday.code();
			
			output	+= separator + "\n\n"
					
					+ "#" + number + " [ " + birthday.contactName() + " ]\n"
					+ "Видалити: " + removeCommand + "\n\n"
					+ "Ваше ім'я:\n"
					+ birthday.authorName() + "\n\n"
					+ "Дата:\n"
					+ date + "\n\n"
					+ "Текст: " + displayCommand + "\n";
			
			number++;
		}
		output += separator;
		
		return output;
	}
	
	public void confirmAllUpdates(MessageHandler handler) {
		GetUpdates getUnhandled = new GetUpdates();
		GetUpdatesResponse unhandledResponse = this.execute(getUnhandled);
		List<Update> unhandledList = unhandledResponse.updates();
		
		if (unhandledList != null) {
			int highestUpdateId = 0;
			for (Update unhandled : unhandledList) {
				Message message = unhandled.message();
				if (message != null) {
					Type type = message.chat().type();
					switch (type) {
					case group:
					case supergroup:
						try { handler.updateChatData(this.chats, message); }
			    		catch (IOException e) {}
						break;
					default:
						break;
					}
				}
				
				int updateId = unhandled.updateId();
				if (updateId > highestUpdateId) highestUpdateId = updateId;
			}
			
			GetUpdates confirmUnhandled = new GetUpdates()
					.offset(highestUpdateId + 1);
			this.execute(confirmUnhandled);
		}
	}
	
	public void congratulateToday() throws IOException {
		List<Birthday> today = Birthday.todayBirthdays();
		if (today != null) {
			List<BotChat> chats = this.chats;
			
			List<Congrat> congrats = new ArrayList<Congrat>();
			//for every chat we have
			for (BotChat chat : chats) {
				long chatId = chat.id();
				//for every chat member we have
				for (long memberId : chat.members()) {
					boolean memberExists = chatMemberExists(chatId, memberId);
					if (!memberExists) continue;
					
					
					String greetings = "";
					String separator = "_ _ _ _ _ _ _ _ _ _ _ _ _ _ _";
					//for every birthday greeting we have today
					for (Birthday birthday : today) {
						long contactId = birthday.contactId();
						if (memberId == contactId) {
							greetings +=
									  separator + "\n\n"
									+ "🔸" + birthday.authorName() + "🔸" + "\n\n"
									+ "Подивитися: /birthdaytext_" + birthday.code() + "\n";
						}
					}
					
					if (!greetings.contentEquals("")) {
						User user = getChatUser(chatId, memberId);
						String firstName = user.firstName();
						String lastName = user.lastName();
						String fullName;
						if (lastName == null) fullName = firstName;
						else fullName = firstName + " " + lastName;
						
						String intro = congratulateTodayIntro(firstName, fullName);
						String text = intro + greetings + separator;
						
						Congrat congrat = new Congrat(chatId, text);
						congrats.add(congrat);
					}
				}
			}
			
			if (!congrats.isEmpty()) secureCongratsSend(congrats);
		}
	}
	
	private String congratulateTodayIntro(String firstName, String fullName) {
		String text =
				  "Хобана, мені тут передали, що в 🎉" + fullName + "🎉 сьогодні День Народження!\n"
				+ "\n"
				+ firstName + ", тобі тут дехто залишив теплі привітання\n"
				+ "я думаю, твої друзі тебе справді люблять :)\n"
				+ "\n"
				+ "хоч я і просто бот, але в мене також є душа, тому я приєднуюся до усіх "
				+ "привітань та бажаю тобі весело провести цей день :D\n";
		
		return text;
	}
}
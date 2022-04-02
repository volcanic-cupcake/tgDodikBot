package tgAyeBot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SessionStore {
	private static List<BirthdaySession> setBirthday = new ArrayList<BirthdaySession>();
	// also add new stuff to clear() method!

	public static void clear(long userId) {
		//example
		Iterator<BirthdaySession> setBirthdayIterator = setBirthday.iterator();
		while (setBirthdayIterator.hasNext()) {
			BirthdaySession session = setBirthdayIterator.next();
			if (session.authorId() == userId) setBirthday.remove(session);
		}
		
		//add new stuff here as in the example
	}
	
	public static List<BirthdaySession> setBirthday() {
		return setBirthday;
	}
}
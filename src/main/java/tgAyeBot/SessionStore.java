package tgAyeBot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SessionStore {
	
	private static List<SetBirthdaySession> setBirthday = new ArrayList<SetBirthdaySession>();
	// also add new stuff to clear(long) and clear() methods!
	
	public static void clear() {
		setBirthday.clear();
		//add new stuff here
	}
	public static void clear(long userId) {
		//example begins
		for (SetBirthdaySession session : setBirthday) {
			if (session.authorId() == userId) setBirthday.remove(session);
			break;
		}
		//example ends
		
		//add new stuff here as in the example
		
	}
	
	public static List<SetBirthdaySession> setBirthday() {
		return setBirthday;
	}
}
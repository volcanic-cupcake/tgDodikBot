package tgAyeBot;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Session {
	
	private long authorId;
	private ZonedDateTime created;
	
	Session(long authorId, ZonedDateTime created) {
		setAuthorId(authorId);
		setCreated(created);
	}
	
	public long authorId() {
		return this.authorId;
	}
	public ZonedDateTime created() {
		return this.created;
	}
	
	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}
	public void setCreated(ZonedDateTime created) {
		this.created = created;
	}
}
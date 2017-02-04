package accountserver;

public class UserPack {

	private final String user, pass;
	
	private final int id;
	private final int banned, allog;
	private final int login_count;
	
	public UserPack(String user, String pass, int id, int banned, int allog, int login_count) {
		this.user = user;
		this.pass = pass;
		this.id = id;
		this.banned = banned;
		this.allog = allog;
		this.login_count = login_count;
	}
	
	@Override
	public String toString() {
		return String.format("%s,%s,%d,%d,%d", this.user, this.pass, this.id, this.banned, this.allog);
	}
	
	public String getUser() {
		return user;
	}
	
	public String getPass() {
		return pass;
	}
	
	public int getId() {
		return id;
	}
	
	public int getBanned() {
		return banned;
	}
	
	public int isAlreadyLogged() {
		return allog;
	}
	
	public int getLoginCount() {
		return login_count;
	}
	
}

package shared;

import java.io.PrintStream;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Logger {

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private PrintStream[] out = null;
	
	public Logger(PrintStream[] out) {
		this.out = out;
	}
	
	public void log(String key, String msg) {
		for (final PrintStream ps : out)
			ps.println("[" + key + ": " + format.format(new Date()) + "] " + msg);
	}
	
	public void logf(String key, String format, Object...args) {
		log(key, String.format(format, args));
	}
	
	public void logi(int i, String key, String msg) {
		out[i].println("[" + key + ": " + format.format(new Date()) + "] " + msg);
	}
	
	public void logfi(int i, String key, String format, Object...args) {
		logi(i, key, String.format(format, args));
	}
	
	public void flush(int i) {
		out[i].flush();
	}
	
	public void flushAll() {
		for (final PrintStream ps : out)
			ps.flush();
	}
	
	public void setDateFormat(SimpleDateFormat format) {
		this.format = format;
	}
	
	public void setStreams(PrintStream[] streams) {
		this.out = streams;
	}
	
	public void close(int i) {
		out[i].close();
		out[i] = null;
	}
	
	public void closeAll() {
		for (final PrintStream ps : out)
			ps.close();
		out = null;
	}
	
	public PrintStream getOutputStream(int i) {
		return out[i];
	}
	
	public PrintStream[] getStreams() {
		return out;
	}
	
	public int getStreamCount() {
		return out.length;
	}
	
}

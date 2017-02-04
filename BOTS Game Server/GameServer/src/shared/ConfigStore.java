package shared;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

public class ConfigStore {

	public static final class PropertyStructure extends Properties {
	
		private static final long serialVersionUID = 1L;
		
		public final int index;
		public final String path;
		
		protected PropertyStructure(int index, String path) {
			super();
			this.index = index;
			this.path = path;
		}
		
	}
	
	private static final Vector<PropertyStructure> props = new Vector<PropertyStructure>();
	
	public static PropertyStructure loadProperties(String path) throws IOException {
		final PropertyStructure struct = new PropertyStructure(props.size(), path);
		final FileInputStream fin = new FileInputStream(path);
		struct.load(fin);
		fin.close();
		props.add(struct);
		return struct;
	}
	
	public static void saveProperty(PropertyStructure struct) throws IOException {
		struct.store(new FileOutputStream(struct.path), struct.path);
	}
	
	public static void saveProperty(int index) throws IOException {
		saveProperty(props.get(index));
	}
	
	public static void saveProperties() throws IOException {
		for (int i=0; i<props.size(); i++)
			saveProperty(i);
	}
	
	public static String getPropertyAt(PropertyStructure struct, String key) {
		return struct.getProperty(key);
	}
	
	public static String getPropertyAt(int index, String key) {
		return getPropertyAt(props.get(index), key);
	}
	
	public static void setPropertyAt(PropertyStructure struct, String key, String value) {
		struct.setProperty(key, value);
	}
	
	public static void setPropertyAt(int index, String key, String value) {
		setPropertyAt(props.get(index), key, value);
	}
	
	public static PropertyStructure getStructureAt(int index) {
		return props.get(index);
	}
	
}

package tropico.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * This class contains methods to save an object into a file.
 * 
 * @author Corentin OGER and Sébastien DOS SANTOS
 *
 */
public class Backup {

	/**
	 * Saves the object to the file.
	 * 
	 * @param src The path where you want to save the game.
	 * @param obj The object you want to save.
	 * @throws IOExceptio
	 */
	public static void saveObject(String src, Object obj) throws IOException {
		Objects.requireNonNull(obj);
		Path path = Path.of(src);

		try (OutputStream back = Files.newOutputStream(path); ObjectOutputStream out = new ObjectOutputStream(back)) {
			out.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the object from the file.
	 *
	 * @param src The path where the game should be saved.
	 * @return Returns the object that was saved in the file.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object loadObject(String src) throws IOException, ClassNotFoundException {
		Path path = Path.of(src);

		try (InputStream back = Files.newInputStream(path); ObjectInputStream in = new ObjectInputStream(back)) {
			return in.readObject();
		}
	}
}
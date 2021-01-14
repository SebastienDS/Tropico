package tropico.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Backup {

    /**
     * serialize the object to file
     * @param src
     * @param obj
     * @throws IOException
     */
    public static void saveObject(String src, Object obj) throws IOException {
        File file = new File(src);
        // file.createNewFile();
//        file.getParentFile().mkdirs();
        Path path = Path.of(src);

        try (OutputStream back = Files.newOutputStream(path);
             ObjectOutputStream out = new ObjectOutputStream(back)) {
            out.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** load the object from file
     *
     * @param src
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object loadObject(String src) throws IOException, ClassNotFoundException {
        Path path = Path.of(src);

        try (InputStream back = Files.newInputStream(path);
             ObjectInputStream in = new ObjectInputStream(back)) {
            return in.readObject();
        }
    }
}
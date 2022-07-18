package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class Blob implements Serializable {

    private String sha1;
    private byte[] content;

    public static final File BLOB_FOLDER = Utils.join(".gitlet", "blobs");


    public Blob(byte[] content) {
        this.content = content;
        // calculate sha1 of the content
        this.sha1 = Utils.sha1(content);

    }

    public String sha() {
        return this.sha1;

    }

    public void save() {
        File blob = Utils.join(BLOB_FOLDER, this.sha1);

        if (!blob.exists()) {
            try {
                blob.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Utils.writeObject(blob, this);

    }

    private static Blob read(String file) {
        File blob = Utils.join(BLOB_FOLDER, file);

        return Utils.readObject(blob, Blob.class);
    }

    public static String contentHash(String file) {
        return read(file).sha1;
    }

    public static byte[] getContent(String file) {
        return read(file).content;
    }

    public static String getContentAsString(String file) {
        return new String(getContent(file), StandardCharsets.UTF_8);
    }
}

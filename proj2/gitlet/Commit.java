package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Commit implements Serializable {

    /**
     * The message of this Commit.
     */
    private final String message;
    // the hash of parent commit
    private final String parent;
    // the merge parent;
    private String mparent = null;
    // the timestamp of this commit
    private final Date timestamp;
    // the files of this commit
    private final HashMap<String, String> blobs;

    static final File COMMIT_FOLDER = Utils.join(".gitlet", "commits");

    public Commit(String message, String parent) {
        this.message = message;
        this.parent = parent;
        this.timestamp = new Date();

        if (parent == null) {
            this.timestamp.setTime(0);
            this.blobs = new HashMap<>();
        } else {
            this.blobs = readCommit(parent).getBlobs();
        }
    }

    public void saveCommit() {

        File commitObj = Utils.join(Commit.COMMIT_FOLDER, this.hashing());

        if (!commitObj.exists()) {
            try {
                commitObj.createNewFile();
                Utils.writeObject(commitObj, this);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Commit readCommit(String hash) {

        File toRead = Utils.join(COMMIT_FOLDER, hash);

        if (!toRead.exists()) {
            System.out.println("No commit with that id exists.");
            return null;
        }

        return Utils.readObject(toRead, Commit.class);

    }

    public void update(Stage.StagingArea files) {
        this.blobs.putAll(files.files2add);
        Set<String> set = files.files2rm.keySet();
        this.blobs.keySet().removeAll(set);
    }

    public String hashing() {
        byte[] h = Utils.serialize(this);
        return Utils.sha1((Object) h);
    }

    public String getParent() {
        return this.parent;
    }
    public String getMparent() {
        return this.mparent;
    }

    public static void log() {
        String head = Branch.getHead();

        while (head != null) {
            head = display(head);
        }
    }

    private static String display(String head) {
        Commit history = readCommit(head);
        String date = history.getDate();
        String message = history.getMessage();


        if (history.mparent != null) {
            System.out.println("==="
                    + "\ncommit " + head
                    + "\nMerge: " + history.parent.substring(0, 7)
                                  + " " + history.mparent.substring(0, 7)
                    + "\nDate: " + date
                    + "\n" + message + "\n");
        } else {
            System.out.println("==="
                    + "\ncommit " + head
                    + "\nDate: " + date
                    + "\n" + message + "\n");
        }
        return history.getParent();
    }

    public static void globalLog() {
        List<String> files = Utils.plainFilenamesIn(COMMIT_FOLDER);

        for (String hashing : files) {
            display(hashing);
        }
    }

    public HashMap<String, String> getBlobs() {
        return this.blobs;
    }

    public String getMessage() {
        return this.message;
    }

    private String getDate() {
        String pattern = "EEE MMM dd HH:mm:ss yyyy Z";

        DateFormat df = new SimpleDateFormat(pattern);

        return df.format(this.timestamp);
    }

    public static void find(String msg) {
        List<String> files = Utils.plainFilenamesIn(COMMIT_FOLDER);
        int flag = 0;

        for (String hashing : files) {
            Commit c = readCommit(hashing);
            if (c.message.equals(msg)) {
                System.out.println(hashing);
                flag = 1;
            }
        }

        if (flag == 0) {
            System.out.println("Found no commit with that message.");
        }
    }


    public static void makeCommit(String message, String parent, String mergeParent) {
        Commit newCommit = new Commit(message, parent);
        newCommit.mparent = mergeParent;
        Stage.StagingArea staged = Stage.getStaged();
        if (staged.size() == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }
        newCommit.update(staged);
        newCommit.saveCommit();
        //move pointer
        Branch.moveHead(newCommit.hashing());
        //clear staging area
        Stage.clean();

    }

    public static void makeCommit(String message, String parent) {
        makeCommit(message, parent, null);
    }


    public static String inCommit(String filename) {
        String head = Branch.getHead();
        return inCommit(filename, head);
    }

    public static String inCommit(String filename, String id) {
        Commit c = Commit.readCommit(id);
        assert c != null;
        if (!c.getBlobs().containsKey(filename)) {
            return null;
        }
        return c.getBlobs().get(filename);
    }

    public static boolean exists(String id) {
        File target = Utils.join(COMMIT_FOLDER, id);

        if (!target.exists()) {
            System.out.println("No commit with that id exists.");
            return false;
        }

        return true;
    }

}

package gitlet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static gitlet.Blob.BLOB_FOLDER;
import static gitlet.Repository.CWD;


public class Stage implements Serializable {

    // The staging area
    public static final File STAGE = Utils.join(".gitlet", "stage");

    private static StagingArea area = new StagingArea();

    public static void init() {

        if (!STAGE.exists()) {
            try {
                STAGE.createNewFile();
                Utils.writeObject(STAGE, area);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void add(String filename) {
        // get staged
        area = getStaged();
        HashMap<String, String> files = area.files2add;

        // read the target file, error if not exist
        File toAdd = Utils.join(CWD, filename);
        if (!toAdd.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        byte[] contents = Utils.readContents(toAdd);

        // making blob
        Blob tmp = new Blob(contents);

        /**  If the current working version of the file is identical
         * to the version in the current commit, do not stage it to be
         * added, and remove it from the staging area if it is already there.
         */
        String h = Commit.inCommit(filename);
        if (h != null && Objects.equals(h, tmp.sha())) {
            files.remove(filename);
            area.files2rm.remove(filename);
            Utils.writeObject(STAGE, area);
            return;
        }

        // overwrites the previous entry of staged files
        if (files.containsKey(filename) && !Objects.equals(files.get(filename), tmp.sha())) {
            File old = Utils.join(BLOB_FOLDER, files.get(filename));
            old.delete();
        }
        tmp.save();
        files.put(filename, tmp.sha());
        Utils.writeObject(STAGE, area);

    }

    public static StagingArea getStaged() {
        return Utils.readObject(STAGE, StagingArea.class);
    }

    public static List<String> files2add() {
        area = getStaged();
        HashMap<String, String> ad = area.files2add;

        return new ArrayList<>(ad.keySet());
    }

    public static List<String> files2rm() {
        area = getStaged();
        HashMap<String, String> rm = area.files2rm;

        return new ArrayList<>(rm.keySet());
    }

    public static void clean() {
        Utils.writeObject(STAGE, new StagingArea());
    }

    public static void removing(String filename) {

        area = getStaged();
        HashMap<String, String> ad = area.files2add;
        HashMap<String, String> rm = area.files2rm;

        /** If the file is neither staged nor tracked by the head commit,
         * print the error message "No reason to remove the file."*/
        String h = Commit.inCommit(filename);
        if (!ad.containsKey(filename) && h == null) {
            System.out.println("No reason to remove the file.");
            return;
        }

        /** If the file is tracked in the current commit, stage it for removal and
         * remove the file from the working directory if the user has not already done so
         * (do not remove it unless it is tracked in the current commit).*/

        if (h != null) {
            rm.put(filename, h);
            Utils.restrictedDelete(filename);
        } else {
            // Unstage the file if it is currently staged for addition
            File old = Utils.join(BLOB_FOLDER, ad.get(filename));
            old.delete();
            ad.remove(filename);
        }
        Utils.writeObject(STAGE, area);
    }

    public static class StagingArea implements Serializable {
        HashMap<String, String> files2add;
        HashMap<String, String> files2rm;

        public StagingArea() {
            files2add = new HashMap<>();
            files2rm = new HashMap<>();
        }

        public int size() {
            return files2add.size() + files2rm.size();
        }
    }

}

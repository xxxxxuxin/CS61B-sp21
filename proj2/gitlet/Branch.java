package gitlet;


import java.io.File;
import java.io.IOException;
import java.util.List;

import static gitlet.Repository.GITLET_DIR;

public class Branch {

    // The HEAD pointer
    public static final File HEAD = Utils.join(".gitlet", "HEAD");
    // The branches directory
    public static final File REF = Utils.join(GITLET_DIR, "refs");

    public static void init(String hash) {

        REF.mkdir();
        File branch = Utils.join(REF, "master");
        Utils.writeContents(branch, hash);

        if (!HEAD.exists()) {
            try {
                HEAD.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Utils.writeContents(HEAD, String.format("branch: %s", "master"));
    }


    public static void newBranch(String b) {
        File bran = Utils.join(REF, b);
        if (!bran.exists()) {
            Utils.writeContents(bran, getHead());
        } else {
            System.out.println("A branch with that name already exists.");
        }
    }

    // the branch name indicated by HEAD
    public static String currBranch() {
        String head = Utils.readContentsAsString(HEAD);
        return head.split(" ")[1];
    }

    // the id of current head pointer
    public static String getHead() {
        File pt = Utils.join(REF, currBranch());
        return Utils.readContentsAsString(pt);
    }

    // change branch by change HEAD file
    public static void moveBranch(String branch) {
        Utils.writeContents(HEAD, String.format("branch: %s", branch));
    }

    // change Head pointer
    public static void moveHead(String pointer) {
        File branch = Utils.join(REF, currBranch());
        Utils.writeContents(branch, pointer);
    }

    public static List<String> getBranches() {
        return Utils.plainFilenamesIn(REF);
    }

    public static String getBranch(String name) {
        File branch = Utils.join(REF, name);
        if (!branch.exists()) {
            System.out.println("No such branch exists.");
            return null;
        } else {
            return Utils.readContentsAsString(branch);
        }
    }

    public static void rmBranch(String name) {
        if (name.equals(currBranch())) {
            System.out.println("Cannot remove the current branch.");
        } else {
            File branch = Utils.join(REF, name);
            if (!branch.exists()) {
                System.out.println("A branch with that name does not exist.");
            } else {
                branch.delete();
            }
        }
    }

    public static boolean exist(String b) {
        File f = Utils.join(REF, b);
        if (!f.exists()) {
            System.out.println("A branch with that name does not exist.");
        }
        return f.exists();
    }

}

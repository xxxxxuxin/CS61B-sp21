package gitlet;

import java.io.File;
// import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static gitlet.Utils.*;
// import gitlet.Commit;
// import jdk.jshell.execution.Util;
// import org.checkerframework.checker.units.qual.C;
// import org.checkerframework.checker.units.qual.C;


public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");


    // initialize repository
    public static void setupPersistence() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        Commit.COMMIT_FOLDER.mkdir();
        Blob.BLOB_FOLDER.mkdir();

        Commit initial = new Commit("initial commit", null);
        initial.saveCommit();

        // empty stage
        Stage.init();
        // create branch master and head pointer
        Branch.init(initial.hashing());
    }


    public static void getStatus() {
        // get branches
        List<String> branches = Branch.getBranches();
        // sort
        branches.sort(null);
        //add "*" before current branch
        String head = Branch.currBranch();
        for (int i = 0; i < branches.size(); i += 1) {
            if (branches.get(i).equals(head)) {
                branches.set(i, "*" + head);
            }
        }

        // get staged files
        List<String> ad = Stage.files2add();
        List<String> rm = Stage.files2rm();

        ad.sort(null);
        rm.sort(null);

        // find changed and untracked files
        Map<String, List<String>> m = getChanged();
        List<String> changed = m.get("c");
        List<String> untracked = m.get("u");

        changed.sort(null);
        untracked.sort(null);

        // display
        System.out.println("=== Branches ===");
        displayList(branches);
        System.out.println("\n=== Staged Files ===");
        displayList(ad);
        System.out.println("\n=== Removed Files ===");
        displayList(rm);
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        displayList(changed);
        System.out.println("\n=== Untracked Files ===");
        displayList(untracked);

    }

    public static void displayList(List<String> l) {
        for (String x : l) {
            System.out.println(x);
        }
    }

    private static Map<String, List<String>> getChanged() {
        List<String> changed = new LinkedList<>();
        List<String> untracked = new LinkedList<>();
        // tracked files
        String head = Branch.getHead();
        // <filename, blobHash>
        HashMap<String, String> blobs = Commit.readCommit(head).getBlobs();

        // staged for add files
        HashMap<String, String> f2a = Stage.getStaged().files2add;
        HashMap<String, String> f2r = Stage.getStaged().files2rm;

        blobs.putAll(f2a);
        for (Map.Entry<String, String> set : blobs.entrySet()) {
            // find the file
            File f = Utils.join(CWD, set.getKey());
            if (f.exists()) {
                // compare the hash
                String old = Blob.contentHash(set.getValue());
                String cur = Utils.sha1(set.getKey(), Utils.readContents(f));
                if (!old.equals(cur)) {
                    changed.add(set.getKey() + " (modified)");
                }
            } else {
                // Not staged for removal, but tracked in the current commit
                // and deleted from the working directory.
                if (!f2r.containsKey(set.getKey())) {
                    changed.add(set.getKey() + " (deleted)");
                }
            }
        }

        /* files present in the working directory but neither staged for addition nor tracked
         * includes files that have been staged for removal,
         * but then re-created without Gitlet???s knowledge */
        List<String> files = Utils.plainFilenamesIn(CWD);
        if (files != null) {
            for (String f : files) {
                if (!blobs.containsKey(f) || f2r.containsKey(f)) {
                    untracked.add(f);
                }
            }
        }

        Map<String, List<String>> m = new HashMap<>();
        m.put("c", changed);
        m.put("u", untracked);
        return m;
    }

    public static void checkout(String[] args) {
        if (args[1].equals("--")) {
            String filename = args[2];
            String head = Branch.getHead();
            String blobId = Commit.inCommit(filename, head);
            checkoutFile(filename, blobId);
        } else if (args.length == 4 && args[2].equals("--")) {
            String commitID = idRecover(args[1]);

            String filename = args[3];
            if (Commit.exists(commitID)) {
                String blobId = Commit.inCommit(filename, commitID);
                checkoutFile(filename, blobId);
            }
        } else if (args.length == 2) {
            String branchName = args[1];
            if (Branch.currBranch().equals(branchName)) {
                System.out.println("No need to checkout the current branch.");
                return;
            }
            String branch = Branch.getBranch(branchName);
            if (branch != null) {
                if (untrackCheck()) {
                    idReset(branch);

                    Branch.moveBranch(branchName);
                    Branch.moveHead(branch);
                    Stage.clean();
                }
            }

        } else {
            System.out.println("Incorrect operands.");
        }
    }

    public static void reset(String commitID) {
        commitID = idRecover(commitID);
        if (untrackCheck()) {
            if (idReset(commitID)) {
                Branch.moveHead(commitID);
                Stage.clean();
            }
        }
    }

    // return false if it's a bad id
    private static boolean idReset(String commitID) {
        Commit c = Commit.readCommit(commitID);
        if (c != null)  {
            List<String> files = Utils.plainFilenamesIn(CWD);
            HashMap<String, String> blobs = c.getBlobs();
            if (files != null) {
                for (String file : files) {
                    if (!blobs.containsKey(file)) {
                        File tmp = Utils.join(CWD, file);
                        Utils.restrictedDelete(tmp);
                    }
                }
            }

            for (Map.Entry<String, String> set : blobs.entrySet()) {
                checkoutFile(set.getKey(), set.getValue());
            }

            return true;
        }
        return false;
    }

    private static boolean untrackCheck() {
        List<String> untracked = getChanged().get("u");
        if (untracked.size() > 0) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            return false;
        } else {
            return true;
        }
    }

    private static void checkoutFile(String filename, String blobID) {
        if (blobID != null) {
            File f = Utils.join(CWD, filename);
            Utils.writeContents(f, Blob.getContent(blobID));
        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    // if id is partial
    private static String idRecover(String id) {
        List<String> commits = Utils.plainFilenamesIn(Commit.COMMIT_FOLDER);
        assert commits != null;
        for (String x : commits) {
            if (Pattern.matches(String.format("%s\\w*?", id), x)) {
                return x;
            }
        }
        return id;
    }


    public static void merge(String branch) {
        //  If there are staged additions or removals present
        if (Stage.getStaged().size() > 0) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        if (untrackCheck() && Branch.exist(branch)) {
            // If attempting to merge a branch with itself
            if (branch.equals(Branch.currBranch())) {
                System.out.println("Cannot merge a branch with itself.");
                return;
            }

            String id = findSplit(branch);
            if (id.equals("c>b")) {
                System.out.println("Given branch is an ancestor of the current branch.");
                return;
            } else if (id.equals("c<b")) {
                checkout(new String[]{"checkout", branch});
                System.out.println("Current branch fast-forwarded.");
                return;
            }
            Commit split = Commit.readCommit(id);
            Commit br = Commit.readCommit(Branch.getBranch(branch));
            Commit cur = Commit.readCommit(Branch.getHead());
            HashMap<String, String> A, B, C;
            A = split.getBlobs();
            B = br.getBlobs();
            C = cur.getBlobs();

            List<String> files = new LinkedList<>(A.keySet());
            files.addAll(B.keySet());
            files.addAll(C.keySet());

            combine(files, A, B, C);

            String msg = "Merged %s into %s.";
            Commit.makeCommit(String.format(msg, branch, Branch.currBranch()),
                                Branch.getHead(), Branch.getBranch(branch));
        }
    }

    private static void combine(List<String> files, HashMap<String, String> A,
                                   HashMap<String, String> B, HashMap<String, String> C) {

        boolean flag = false;
        Stage.StagingArea area = Stage.getStaged();
        HashMap<String, String> res = area.files2add;
        for (String f : files) {
            if (B.containsKey(f) && C.containsKey(f)) {
                if (B.get(f).equals(C.get(f))) {
                    res.put(f, B.get(f));
                } else if (A.containsKey(f)) {
                    if (B.get(f).equals(A.get(f))) {
                        res.put(f, C.get(f));
                    } else if (C.get(f).equals(A.get(f))) {
                        res.put(f, B.get(f));
                    } else {
                        // conflict
                        res.put(f, conflict(f, B.get(f), C.get(f)));
                        flag = true;
                    }
                } else {
                    // conflict
                    res.put(f, conflict(f, B.get(f), C.get(f)));
                    flag = true;
                }
            } else if (!B.containsKey(f)) {
                if (C.containsKey(f)) {
                    if (A.containsKey(f)) {
                        if (A.get(f).equals(C.get(f))) {
                            // remove
                            File fi = Utils.join(CWD, f);
                            Utils.restrictedDelete(fi);
                            area.files2rm.put(f, C.get(f));
                        } else {
                            res.put(f, conflict(f, null, C.get(f)));
                            flag = true;
                        }
                    } else {
                        res.put(f, C.get(f));
                    }
                }
            } else if (!C.containsKey(f)) {
                if (B.containsKey(f)) {
                    if (A.containsKey(f)) {
                        if (!A.get(f).equals(B.get(f))) {
                            res.put(f, conflict(f, B.get(f), null));
                            flag = true;
                        }
                    } else {
                        res.put(f, B.get(f));
                    }
                }
            }
        }

        if (flag) {
            System.out.println("Encountered a merge conflict.");
        }
        for (Map.Entry<String, String> set : res.entrySet()) {
            checkoutFile(set.getKey(), set.getValue());
        }
        Utils.writeObject(Stage.STAGE, area);
    }

    private static String conflict(String f, String b, String c) {
        String out = "<<<<<<< HEAD\n%s=======\n%s>>>>>>>\n";
        File both = Utils.join(CWD, f);
        String curr = c == null ? "" : Blob.getContentAsString(c);
        String br = b == null ? "" : Blob.getContentAsString(b);
        String content = String.format(out, curr, br);
        Utils.writeContents(both, content);

        Blob conf = new Blob(content.getBytes(StandardCharsets.UTF_8), f);
        conf.save();
        return conf.sha();
    }

    private static String findSplit(String branch) {
        Set<String> b = new HashSet<>();

        String id = Branch.getBranch(branch);
        String cur = Branch.getHead();
        dfs(id, b);

        if (b.contains(cur)) {
            return "c<b";
        }
        String res = dfsp(cur, b);
        if (res.equals(id)) {
            return "c>b";
        }

        return res;
    }

    private static void dfs(String id, Set<String> parents) {
        if (id == null) {
            return;
        }
        parents.add(id);
        Commit tmp = Commit.readCommit(id);
        dfs(tmp.getParent(), parents);
        dfs(tmp.getMparent(), parents);
    }

    private static String dfsp(String id, Set<String> b) {
        Commit tmp = Commit.readCommit(id);
        String parent = tmp.getParent();
        String mParent = tmp.getMparent();
        if (parent == null) {
            return id;
        }
        if (!b.contains(id) && b.contains(parent)) {
            return parent;
        } else if (!b.contains(id) && b.contains(mParent)) {
            return mParent;
        }
        return dfsp(parent, b);
    }


    public static boolean initialized() {
        return GITLET_DIR.exists();
    }
}

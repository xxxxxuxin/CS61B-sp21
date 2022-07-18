package gitlet;

// import org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver;


import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        if (firstArg.equals("init")) {
            Repository.setupPersistence();
        } else if (Repository.initialized()) {
            switch (firstArg) {
                case "add" -> Stage.add(args[1]);
                case "commit" -> {
                    if (args.length == 1 || args[1].equals("")) {
                        System.out.println("Please enter a commit message.");
                        break;
                    }
                    String message = args[1];
                    Commit.makeCommit(message, Branch.getHead());
                }
                case "rm" -> Stage.removing(args[1]);
                case "log" -> Commit.log();
                case "global-log" -> Commit.globalLog();
                case "find" -> Commit.find(args[1]);
                case "status" -> Repository.getStatus();
                case "checkout" -> Repository.checkout(args);
                case "branch" -> Branch.newBranch(args[1]);
                case "rm-branch" -> Branch.rmBranch(args[1]);
                case "reset" -> Repository.reset(args[1]);
                case "merge" -> Repository.merge(args[1]);
                default -> System.out.println("No command with that name exists.");
            }
        } else {
            System.out.println("Not in an initialized Gitlet directory.");
        }
    }
}

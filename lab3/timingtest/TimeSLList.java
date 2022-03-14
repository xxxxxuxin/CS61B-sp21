package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        int start = 1000;
        int rows = 8;
        int M = 10000;
        for (int i = 1; i<=rows; i+=1){
            SLList<Integer> tester = new SLList<>();
            Ns.addLast(start);
            for (int j = 1; j<=start; j+=1){
                tester.addLast(0);
            }
            Stopwatch sw = new Stopwatch();
            for (int j = 1; j<=M; j+=1){
                tester.getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            opCounts.addLast(M);
            times.addLast(timeInSeconds);
            start *= 2;
        }

        printTimingTable(Ns, times, opCounts);
    }

}

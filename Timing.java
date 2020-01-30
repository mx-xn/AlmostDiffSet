import java.util.*;

public class Timing {
  public static void main(String[] args) {
    int v = 32;
    int t = 24;
    int lambda = 7;
    int k = 15;

    long startTime = System.nanoTime();
    for (int i = 0; i < 100; i++) {
      OneDimADSGenerator adsGen1 = new OneDimADSGenerator(v, lambda, t, k);
      int[][] arr = adsGen1.getAllGroupElements();
      Set<List<int[]>> candidates = adsGen1.getADSCandidates(arr);
      adsGen1.dummyPrint();
    }
    long endTime = System.nanoTime();
    long duration1 = (endTime - startTime);
    //adsGen1.printADS(adsGen1.getADS(candidates, arr));

    startTime = System.nanoTime();
    for (int i = 0; i < 100; i++) {
      ADSGen adsGen2 = new ADSGen(v, k, lambda, t);
      adsGen2.dummyPrint();
    }
    endTime = System.nanoTime();

    long duration2 = (endTime - startTime);
    //adsGen2.printADS(adsGen2.getADSCandidates());


    //adsGen.printADS(adsGen.getADSCandidates());
    System.out.printf("old method used %s time, \nnew method used %s time\n",
        duration1, duration2);
  }
}

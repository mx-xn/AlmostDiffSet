import java.util.*;
public class TestDiffLambda {
  public static void main(String[] args) {
    final int minLambda = 2;
    final int maxLambda = 5;
    final int groupOrder = 32;

    generateADSWithDiffLambda(minLambda, maxLambda, groupOrder);
  }

  /* given the cyclic group's num of elements, '
   * min lambda, and max lambda, try to find ADS
   * w/ from min lambda to max lambda
   * NOTE: only work on one dim right now
   */
  public static void generateADSWithDiffLambda(
    int minLambda, int maxLambda, int groupOrder) {
    for (int lambda = minLambda; lambda <= maxLambda; lambda++) {
      System.out.println("lambda: "+ lambda);
      int ADSOrder = findADSOrder(groupOrder, lambda);
      int numElemLambda = findNumElemLambda(groupOrder, lambda, ADSOrder);
      System.out.println("ADSOrder is " + ADSOrder);
      System.out.println("numElemCovered " + numElemLambda);

      OneDimADSGenerator adsGen = new OneDimADSGenerator(groupOrder,
          lambda, numElemLambda, ADSOrder);

      int[][] arr = adsGen.getAllGroupElements();
      Set<List<int[]>> candidates = adsGen.getADSCandidates(arr);

      Set<List<int[]>> ads = adsGen.getADS(candidates, arr);

      ads = adsGen.filterMultiples(ads);
      System.out.println("number of filtered ADS: "+ ads.size());
      adsGen.printADS(ads);
    }
  }
  
  public static int findNumElemLambda(int groupOrder, int lambda, int ADSOrder) {
    return (ADSOrder * (ADSOrder - 1)) - ((lambda - 1) * (groupOrder - 1));
  }

  public static int findADSOrder(int groupOrder, int lambda) {
    //System.out.println("groupOrder is " + groupOrder);
    // order * (order - 1) = numElemLambda + (lambda - 1) * groupOrder
    int order = 2;
    int subtract = (lambda - 1) * (groupOrder - 1);
    while (order * (order - 1) < subtract) {
      order++;
    } 
    return order;
  }
  

}

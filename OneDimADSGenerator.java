import java.util.*;
// goal for next week: debug to get 32, would be nice if get 64
// go back to Z_16, make |ds| = 11, lambda = (11 * 10) = 8x + (15 - x)*7 -> 5?

public class OneDimADSGenerator extends ADSGenerator {
  // additional field
  int[] coverTimesCntr;
  //static int numLambdaCovered;
  //int numNotLambdaCovered;

  // keep track of all elements that are generated lambda times by curr ads
  Map<String, int[]> lambdaElemTracker;

  /* constructor
   */
  public OneDimADSGenerator() {
    super();
    coverTimesCntr = new int[this.groupOrder];
    lambdaElemTracker = new HashMap<>();
  }

  public OneDimADSGenerator(int groupOrder, int lambda, int numElemLambda,
    int ADSOrder) {
    this.dimension = 1;
    array = new int[dimension];
    this.groupOrder = groupOrder;
    array[0] = this.groupOrder;
    this.lambda = lambda;
    this.numElemLambda = numElemLambda;
    this.ADSOrder = ADSOrder;
    this.validADSPreCheck();

    coverTimesCntr = new int[this.groupOrder];
    lambdaElemTracker = new HashMap<>();
  }

  public void dummyPrint() {
    System.out.println(1);
  }

  public void printADS(Set<List<int[]>> ads) {
    for (List<int[]> set : ads) {
      System.out.print("[");
      String encoding = "";
      for (int[] entry : set) {
        System.out.print(entry[0] + " ");
        encoding += encode(entry);
      }
      System.out.println("] - " + Arrays.toString(lambdaElemTracker.get(encoding)));
    }
  }

  /* generate all possible sets of order ADSOrder
   */
  public Set<List<int[]>> getADSCandidates(int[][] allGroupElem) {
    //System.out.println("in one-dim getADSCandidates");
    List<int[]> currSet = new ArrayList<>();
    int firstElemInd = 0;
    currSet.add(allGroupElem[firstElemInd]);
    firstElemInd++;

    Set<List<int[]>> allSets = new LinkedHashSet<>();
    getNext(firstElemInd, ADSOrder - 1, allGroupElem, currSet, allSets, 0);
    //System.out.println(allSets.size());
    return allSets;
  }

  /* recursive method to help generate next ADS candidate
   */
  private void getNext(int currInd, int numLeft, int[][] allGroupElem,
                       List<int[]> currSet, Set<List<int[]>> allSets,
                       int numLambdaCovered) {
    if (numLeft == 0) {
      List<int[]> newSet = new ArrayList<>(currSet);
      allSets.add(newSet);
      
      // add all the elements that are covered lambda times to the hashmap
      // tracker
      int i = 0;
      int[] elem = new int[this.numElemLambda];
      //System.out.println("set just added, and numLmbdaCover " + numLambdaCovered);
      for (int j = 0; j < coverTimesCntr.length; j++) {
        if (coverTimesCntr[j] == lambda) {
          elem[i++] = j;
        }
      }
      String encoding = "";
      for (int[] entry : newSet) {
        encoding += encode(entry);
      }
      lambdaElemTracker.put(encoding, elem);
      return;

    } else if (allGroupElem.length - currInd >= numLeft) {
      List<Integer> differences = new ArrayList<>();
      // attach current elem to currSet
      //for (int i = currInd; i < allGroupElem.length; i++) {
        int i = currInd;
        // add new to set, update ctnr
        boolean stop = false;
        for (int[] num : currSet) {
          int diff1 = (num[0] - allGroupElem[i][0] + groupOrder) % groupOrder;
          int diff2 = (allGroupElem[i][0] - num[0] + groupOrder) % groupOrder;
          differences.add(diff1);
          differences.add(diff2);
          coverTimesCntr[diff1]++;
          coverTimesCntr[diff2]++;
          if (coverTimesCntr[diff1] == lambda) {
            //System.out.println(diff1 + " reached lambda");
            numLambdaCovered++;
          }
          if (coverTimesCntr[diff2] == lambda && diff2 != diff1) {
            //System.out.println(diff2 + " reached lambda");
            numLambdaCovered++;
          }

          if (coverTimesCntr[diff1] > lambda || 
              coverTimesCntr[diff2] > lambda ||
              numLambdaCovered > this.numElemLambda) {
            // stop with this num
            /*
            if (numLambdaCovered > this.numElemLambda) {
              System.out.println("STOP b/c numLambdaCovered exceed");
            } else if (coverTimesCntr[diff1] > lambda) {
              System.out.println("STOP b/c " + diff1 + " covered " + 
                  coverTimesCntr[diff1] + " times");
            } else {
              System.out.println("STOP b/c " + diff2 + " covered " + 
                  coverTimesCntr[diff2] + " times");
            }
            */
            stop = true;
            break;
          }
        }
        if (!stop) {
          currSet.add(allGroupElem[i]);
          for (int k = i + 1; k < groupOrder; k++) {
            getNext(k, numLeft - 1, allGroupElem, currSet, allSets, numLambdaCovered);
            if (allSets.size() > 0)
              return;
          }
          currSet.remove(allGroupElem[i]);
        }
        /*
        System.out.print("before removing: ");
        for (int[] e : currSet) {
          System.out.print(e[0] + ", ");
        }
        System.out.println();
        */
        for (int diff : differences) {
          coverTimesCntr[diff]--;
          /*
          if (coverTimesCntr[diff] == lambda) {
            numLambdaCovered--;
            coverTimesCntr[diff]--;
            System.out.println("popping out " + diff + " covered lambda times, "
                + "now " + coverTimesCntr[diff]);
            System.out.println("numLambdaCovered " + numLambdaCovered);
          } else {
            coverTimesCntr[diff]--;
          }
            */
        }
      //}
      //System.out.println("numLambdaCovered " + numLambdaCovered);
    }
  }

  public Set<List<int[]>> getADS(Set<List<int[]>> candidates, 
    int[][] allGroupElements) {
    return candidates;
  }
}

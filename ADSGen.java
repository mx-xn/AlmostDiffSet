import java.util.*;
public class ADSGen {
  // fields
  protected int dimension;   // dimension of the space in concern
  protected int[] dim;     // array holding info for R_i in index i
  protected int v;           // v - |G|
  protected int t;           // number of elements to be covered for lambda times
  protected int lambda;      // t elements covered LAMBDA times
  protected int k;           // ADS order

  private final int A = (int)'A';
  
  // keep track of times each elem is covered
  protected Map<String, Integer> counter;   
  protected List<String> tElements;

  /* constructor
  public ADSGen() {
    //this.readInput();

    this.initCntr();
    for (String s: counter.keySet()) {
      System.out.println(s + ", " + counter.get(s));
    }

    // elements that are covered lambda times
    tElements = new ArrayList<>();
  }
  */

  /* another constructor
   */
  public ADSGen(int v, int k, int lambda, int t) {
    this.v = v;
    this.k = k;
    this.lambda = lambda;
    this.t = t;
    dim = new int[1];
    dim[0] = 32;
    dimension = 1;


    this.initCntr();
    /*
    for (String s: counter.keySet()) {
      System.out.println(s + ", " + counter.get(s));
    }
    */

    // elements that are covered lambda times
    tElements = new ArrayList<>();
  }

  public void dummyPrint() {
    System.out.println(2);
  }

  private void initCntr() {
    counter = new LinkedHashMap<>();

    Queue<String> q1 = new LinkedList<>();
    Queue<String> q2 = new LinkedList<>();
    q1.add("");

    for (int d = 0; d < dimension; d++) {
      while (!q1.isEmpty()) {
        String s = q1.poll();

        // for each str, attach next dim
        for (int i = 0; i < dim[d]; i++) {
          q2.add(new String(s + (char)((int)'A' + i)));
        }
      }
      q1 = q2;
      q2 = new LinkedList<>();
    }

    while (!q1.isEmpty()) {
      counter.put(q1.poll(), 0);
    }
  }

  /* readInput from user, error checking before initializing all fields
   * returns an array containing [dimension, dim[0]..[n - 1], v, k, lambda,
   * t]
   */
  private void readInput() throws IllegalArgumentException {
    Scanner in = new Scanner(System.in);
    System.out.println("input format:"); 
    System.out.println("dimension\n" + 
                       "n1 n2 n3 ... n_dimension\n" +
                       "k"); 

    dimension = in.nextInt();
    if (dimension < 1) {
      throw new IllegalArgumentException("dimension should be positive");
    }

    dim = new int[dimension];
    v = 1;
    for (int i = 0; i < dim.length; i++) {
      // read in n in Z_n, and store n in each index
      dim[i] = in.nextInt();
      if (dim[i] < 1) {
        throw new IllegalArgumentException("n should be positive");
      }
      v *= dim[i];
    }

    k = in.nextInt();
    if (k > v) {
      throw new IllegalArgumentException("k should be smaller than v");
    }
    lambda = findLambda(v, k);
    t = k * (k - 1) - (lambda - 1) * (v - 1);

    // print out all inputs
    for (int i = 0; i < dim.length - 1; i++) {
      System.out.print("Z_" + dim[i] + " * ");
    }
    System.out.println("Z_" + dim[dim.length - 1]);
    
    System.out.printf("ads: (%s, %s, %s, %s)\n", v, k, lambda, t);
  }

  /* given v and k, find corresponding lambda 
   */
  private int findLambda(int v, int k) {
    int l = 0;
    while ((v - 1) * l < k * (k - 1)) {
      l++;
    }
    return l;
  }

  /* print out all ADS
   */
  public void printADS(Set<List<String>> ads) {
    for (List<String> set : ads) {
      System.out.print("{ ");
      for (String str: set) {
        System.out.print("( ");
        for (char c : str.toCharArray()) {
          System.out.print((int)(c - 'A') + " ");
        }
        System.out.print(") ");
      }
      System.out.println("}");
    }
  }

  /* generate all possible sets of order ADSOrder
   */
  public Set<List<String>> getADSCandidates() {
    List<String> elemOfG = new ArrayList<>(this.counter.keySet());
    List<String> currSet = new ArrayList<>();
    Set<List<String>> allSets = new LinkedHashSet<>();

    int firstElemInd = 0;
    
    // fix 0 in the set
    currSet.add(elemOfG.get(firstElemInd));
    firstElemInd++;

    getNext(firstElemInd, k - 1, elemOfG, currSet, allSets, 0);

    System.out.println(allSets.size());
    return allSets;
  }

  /* recursive method to help generate next ADS candidate
   */
  private void getNext(int currInd, int numLeft, List<String> elemOfG,
                       List<String> currSet, Set<List<String>> allSets,
                       int numLambdaCovered) {
    /*System.out.println("currSet: ");
    for (String s : currSet) {
      System.out.print(s + " ");
    }
    

    System.out.println("numLeft " + numLeft);
    System.out.println();
    */
    if (numLeft <= 0) {
      allSets.add(new ArrayList<>(currSet));
      
      // put the t elements into record
      for (String diff : counter.keySet()) {
        if (counter.get(diff) == lambda) {
          tElements.add(diff);

          return;
        }
      }
    } else if (elemOfG.size() - currInd >= numLeft) {
      int i = currInd;
      boolean stop = false;
      //while (elemOfG.size() - i >= numLeft) {
        // the new differences caused by adding current element
        List<String> differences = new ArrayList<>();
        for (String prevElem: currSet) {
          String newDiff1 = new String();
          String newDiff2 = new String();
          for (int d = 0; d < dimension; d++) {
            char diff1Char = (char)(((int)(prevElem.charAt(d) - elemOfG.get(i).charAt(d)) +
                dim[d]) % dim[d] + A);
            char diff2Char = (char)(((int)(elemOfG.get(i).charAt(d) - prevElem.charAt(d)) +
                dim[d]) % dim[d] + A);


            newDiff1 += diff1Char;
            newDiff2 += diff2Char;
          }
          differences.add(newDiff1);
          differences.add(newDiff2);


          //System.out.printf("diff btw %s and %s : %s, %s\n", 
              //prevElem, elemOfG.get(i), newDiff1, newDiff2);

          int count1 = counter.put(newDiff1, counter.get(newDiff1) + 1) + 1;
          int count2 = counter.put(newDiff2, counter.get(newDiff2) + 1) + 1;

          /*
          System.out.printf("count for %s is %s\n", newDiff1, count1);
          System.out.printf("count for %s is %s\n", newDiff2, count2);
          */

          if (count1 == lambda) {
            //System.out.println(newDiff1 + " reached lambda");
            numLambdaCovered++;
          }

          if (!newDiff1.equals(newDiff2) && count2 == lambda) {
            numLambdaCovered++;
          }

          if (count1 > lambda || count2 > lambda || numLambdaCovered > this.t) {
            stop = true;
            break;
          }
        }
        if (!stop) {
          currSet.add(elemOfG.get(i));
          for (int m = i + 1; m <= v - (numLeft - 1); m++) {
            getNext(m, numLeft - 1, elemOfG, currSet, allSets, numLambdaCovered);
            if (allSets.size() > 0)
              return;
          }
          currSet.remove(elemOfG.get(i));
        }

        for (String diff: differences) {
          counter.put(diff, counter.get(diff) - 1);
        }
        //i++;
      //}
    }
  }

  /* generate all possible sets of order ADSOrder
  public Set<List<int[]>> getADSCandidates(int[][] allGroupElem) {
    List<int[]> currSet = new ArrayList<>();
    int firstElemInd = 0;
    currSet.add(allGroupElem[firstElemInd]);
    firstElemInd++;

    Set<List<int[]>> allSets = new LinkedHashSet<>();
    getNext(firstElemInd, ADSOrder - 1, allGroupElem, currSet, allSets);
    //System.out.println(allSets.size());
    return allSets;
  }

  /* go through the candidates, and return a set of candidates that are 
   * actually ADS
  public Set<List<int[]>> getADS(Set<List<int[]>> candidates, 
    int[][] allGroupElements) {
    Set<List<int[]>> adsSet = new LinkedHashSet<>();
    for (List<int[]> candidate : candidates) {
      if (this.isADS(candidate, allGroupElements)) {
        adsSet.add(candidate);
      }
    }

    return adsSet;
  }
   */

}

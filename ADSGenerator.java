import java.util.*;
public class ADSGenerator {
  // fields
  protected int dimension; // dimension of the space in concern
  protected int[] array; // array holding info for R_i in index i
  protected int groupOrder; // num elements in entire group
  protected int numElemLambda; // number of elements to be covered for timeCovered times
  protected int lambda;   // cover numElemCovered elements timesCovered times
  protected int ADSOrder; // ADS order

  /* constructor
   */
  public ADSGenerator() {
    this.readInput();
  }

  /* print out all the ADS
   */
  public void printADS(Set<List<int[]>> ads) {
    for (List<int[]> set : ads) {
      System.out.print("(");
      for (int[] entry : set) {
        System.out.print(Arrays.toString(entry) + " ");
      }
      System.out.println(")");
    }
  }

  /* filter out the multiples by removing them from the hashSet,
   * the multiple k is odd if groupOrder is even,
   *                is even if groupOrder is odd
   * TODO: maybe later change to find relatively Prime first???
  */
  public Set<List<int[]>> filterMultiples(Set<List<int[]>> ads) {
    System.out.println("in filter");
    HashMap<String, List<int[]>> hm = new LinkedHashMap<>();
    // put each ads into the hm, the key is the string concatenation of
    // all its bits
    for (List<int[]> eachADS : ads) {
      String encoding = "";
      for (int[] entry : eachADS) {
        encoding += encode(entry);
      }
      hm.put(encoding, eachADS);
    }
    System.out.println("hm created w/ size " + hm.size());

    int increment = 1;;
    int multiple = 2;
    if (groupOrder % 2 == 0) {
      increment++;
      multiple++;
    }

    List<String> encodingsToBeRemoved = new ArrayList<>();
    System.out.println("generating encodings to be removed");
    for (String encoding : hm.keySet()) {
      if (!encodingsToBeRemoved.contains(encoding)) {
        for (int i = multiple; i < groupOrder; i += increment) {
          String multEncoding = encodeMultiple(i, hm.get(encoding));
          encodingsToBeRemoved.add(multEncoding);
        }
      }
    }

    System.out.println("generated encodings to be removed w/ size " + 
        encodingsToBeRemoved.size() + ", now removing");
    for (String multEncoding : encodingsToBeRemoved) {
      hm.remove(multEncoding);
    }
    /*
    for (List<int[]> baseSet : hm.values()) {
      //int multiple = (groupOrder % 2 + 1) % 2 + 2;
      for (int i = multiple; i < groupOrder; i += increment) {
        String multEncoding = encodeMultiple(i, baseSet)
        if (!toBeRemoved.contains(hm.get(multEncoding))) {
          // TODO: if the multiple is marked to be removed, do not calculate 
          // the multiple of that marked multiple
        }
        toBeRemoved.add(hm.get())
      }
    }
    */

    return (new LinkedHashSet<List<int[]>>(hm.values()));
  }

  /* get multiple encoding
   */
  public String encodeMultiple(int multiply, List<int[]> currentSet) {
    String encoding = "";
    List<int[]> multipleSet = new ArrayList<>();

    for (int[] currEntry : currentSet) {
      int[] newEntry = new int[dimension];
      for (int digit = 0; digit < dimension; digit++) {
        newEntry[digit] = currEntry[digit] * multiply % array[digit];
      }
      multipleSet.add(newEntry);
      //encoding += ("," + encode(newEntry));
    }
    for (int i = dimension - 1; i >= 0; i--) {
      final int index = i;
      Collections.sort(multipleSet, (a, b) -> a[index] - b[index]);
    }
    for (int[] newEntry : multipleSet) {
      encoding += encode(newEntry);
    }

    //System.out.println(encoding);
    return encoding;
  }

  /* sort the ads generated
   */

  /* generate all possible sets of order ADSOrder
   */
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
   */
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

  /* convert int[] to String
   */
  protected String encode(int[] entry) {
    String encoding = "";
    for (int i : entry) {
      encoding = i + encoding;
    }
    return encoding;
  }

  /* check whether the input is ADS
   */
  public void isInputADS() {
    Scanner in = new Scanner(System.in);
    System.out.println("want to check ads? y/n");
    String ans = in.next();
    while (ans.equals("y")) {
      System.out.println("input the ADS candidate, each entry a line" + 
          ", numbers in each entry separated by space");
      List<int[]> candidate = new ArrayList<>();
      for (int order = 0; order < ADSOrder; order++) {
        int[] entry = new int[dimension];
        for (int dim = 0; dim < dimension; dim++) {
          entry[dim] = in.nextInt();
        }
        candidate.add(entry);
      }

      boolean res = this.isADS(candidate, getAllGroupElements());
      if (res) {
        System.out.println("input is ADS");
      } else {
        System.out.println("input is not ADS");
      }
      System.out.println("continue? y/n");
      ans = in.next();
    }
  }

  /* check whether a candidate is really an ADS
   */
  private boolean isADS(List<int[]> candidate, int[][] allGroupElements) {
    int lambdaCoverCounter = 0;
    Map<String, Integer> coverCounter = new HashMap<>();
    // initialize the time of coverage to 0 for all elements
    for (int[] elem : allGroupElements) {
      // convert each element into String
      coverCounter.put(encode(elem), 0);
    }
    
    // go through the candidate set, do corresponding subtractions, and
    // keep updating how many times each elem is covered, and increment lambda
    // counter if an elements gets covered for lambda times
    for (int[] first : candidate) {
      for (int[] second : candidate) {
        // first - second
        if (first != second) {
          int[] diffArr = new int[dimension];
          for (int digit = 0; digit < dimension; digit++) {
            diffArr[digit] = (first[digit] + array[digit] - second[digit]) % array[digit];
          }
          
          // if already reach lambda times
          String encodingOfDiffArr = encode(diffArr);
          int currCount = coverCounter.get(encodingOfDiffArr);
          if (currCount == lambda) {
            //System.out.println(encodingOfDiffArr + " covered " + (lambda + 1) + " times");
            return false;
          }
          else {
            if (currCount == lambda - 1) {
              lambdaCoverCounter++;
            }
            coverCounter.put(encodingOfDiffArr, currCount + 1);
          }
          if (lambdaCoverCounter > numElemLambda) {
            //System.out.println("more than numElemLambda covered lambda times");
            return false;
          }
        }
      }
    }
    //System.out.println("here returns true!!!!!");
    return true;
  }

  /* recursive method to help generate next ADS candidate
   */
  private void getNext(int currInd, int numLeft, int[][] allGroupElem,
                       List<int[]> currSet, Set<List<int[]>> allSets) {
    if (numLeft == 0) {
      List<int[]> newSet = new ArrayList<>(currSet);
      allSets.add(newSet);
    } else if (allGroupElem.length - currInd >= numLeft) {
      // attach current elem to currSet
      for (int i = currInd; i < allGroupElem.length; i++) {
        currSet.add(allGroupElem[i]);
        getNext(i + 1, numLeft - 1, allGroupElem, currSet, allSets);
        currSet.remove(allGroupElem[i]);
      }
    }
  }

  /* returns all group elements
   * [r]: each element
   * [c]: the number at each dimension of each element
   */
  public int[][] getAllGroupElements() {
    int[] divides = new int[dimension];
    divides[dimension - 1] = 1;
    for (int i = dimension - 2; i >= 0; i--) {
      divides[i] = divides[i + 1] * array[i + 1];
    }

    // / last digit, mod curr digit
    int[][] allElem = new int[groupOrder][dimension];
    for (int i = 0; i < groupOrder; i++) {
      for (int dim = 0; dim < dimension; dim++) {
        allElem[i][dim] = (i / divides[dim]) % array[dim];
      }
    }

    return allElem;
  }

  /* readInput from user, error checking before initializing all fields
   */
  public void readInput() throws IllegalArgumentException {
    Scanner in = new Scanner(System.in);
    System.out.println("input the dimension you want to search for");
    dimension = in.nextInt();
    if (dimension < 1) {
      throw new IllegalArgumentException("dimension should be positive");
    }

    array = new int[dimension];
    groupOrder = 1;
    for (int i = 0; i < array.length; i++) {
      // read in n in Z_n, and store n in each index
      System.out.println("input next n for Z_n");
      array[i] = in.nextInt();
      if (array[i] < 1) {
        throw new IllegalArgumentException("n should be positive");
      }
      groupOrder *= array[i];
    }

    System.out.println("input lambda");
    lambda = in.nextInt();

    System.out.println("input number of elements to cover lambda times");
    numElemLambda = in.nextInt();

    System.out.println("input ADS size in query");
    ADSOrder = in.nextInt();
    
    this.validADSPreCheck();

  }

  public void validADSPreCheck() throws IllegalArgumentException {
    if (lambda < 1)
      throw new IllegalArgumentException("lambda should be positive");
  
    if (numElemLambda < 0) {
      throw new IllegalArgumentException("input numElem should be non-negative");
    }

    if (ADSOrder < 0) {
      throw new IllegalArgumentException("ADS size should be non-negative");
    }

    if (ADSOrder * (ADSOrder - 1) !=
        numElemLambda * lambda + (groupOrder - 1 - numElemLambda) * (lambda - 1)) {
      throw new IllegalArgumentException("invalid input");
    }
  }

  public static void main(String[] args) {
    System.out.println("cyclc / 1-dim? Ans: y / n");
    Scanner in = new Scanner(System.in);
    ADSGenerator adsGen;
    if (in.next().equals("y")) {
      adsGen = new OneDimADSGenerator();
    } else {
      adsGen = new ADSGenerator();
    }
    int[][] arr = adsGen.getAllGroupElements();
    System.out.println(Arrays.deepToString(arr));
    Set<List<int[]>> candidates = adsGen.getADSCandidates(arr);

    Set<List<int[]>> ads = adsGen.getADS(candidates, arr);
    System.out.println("number of ADS: "+ ads.size());
    //adsGen.printADS(ads);
    
    ads = adsGen.filterMultiples(ads);
    System.out.println("number of filtered ADS: "+ ads.size());
    adsGen.printADS(ads);

    adsGen.isInputADS();


  }
}

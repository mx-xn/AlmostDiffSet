public class UnitTest {
  public static void main(String[] args) {
    ADSGen adsGen = new ADSGen(32, 15, 7, 24);
    adsGen.printADS(adsGen.getADSCandidates());
  }
}

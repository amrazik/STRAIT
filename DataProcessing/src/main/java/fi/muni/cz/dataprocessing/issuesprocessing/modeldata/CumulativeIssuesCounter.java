package fi.muni.cz.dataprocessing.issuesprocessing.modeldata;

import fi.muni.cz.dataprovider.GeneralIssue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.math3.util.Pair;

/** @author Radoslav Micko, 445611@muni.cz */
public class CumulativeIssuesCounter implements IssuesCounter {

  private IssuesCounter counter;

  /** Initialize attributes to default value. Defaul value is one week. */
  public CumulativeIssuesCounter() {
    counter = new IntervalIssuesCounter(WEEKS);
  }

  /**
   * Initialize attributes to certain values.
   *
   * @param typeOfTimeToAdd type of Calendar enum
   */
  public CumulativeIssuesCounter(String typeOfTimeToAdd) {
    counter = new IntervalIssuesCounter(typeOfTimeToAdd);
  }

  @Override
  public List<Pair<Integer, Integer>> countIssues(
      List<GeneralIssue> listOfIssues, Date startOfTesting, Date endOfTesting) {
    List<Pair<Integer, Integer>> spreadedIssues =
        getIntervalIssues(listOfIssues, startOfTesting, endOfTesting);
    Integer totalNumber = 0;
    List<Pair<Integer, Integer>> listOfTotalIssues = new ArrayList<>();
    for (Pair<Integer, Integer> pair : spreadedIssues) {
      totalNumber = totalNumber + pair.getSecond();
      listOfTotalIssues.add(Pair.create(pair.getFirst(), totalNumber));
    }
    return listOfTotalIssues;
  }

  private List<Pair<Integer, Integer>> getIntervalIssues(
      List<GeneralIssue> listOfIssues, Date startOfTesting, Date endOfTesting) {
    return counter.countIssues(listOfIssues, startOfTesting, endOfTesting);
  }
}

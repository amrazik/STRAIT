package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Remove GeneralIssue objects that have not received a fix
 *
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class FilterOutIssuesWithoutFix implements Filter {

  private static final List<String> FILTERING_WORDS =
      Arrays.asList(
          "will-not-fix",
          "working-as-intended",
          "wip",
          "wontfix",
          "wont-fix",
          "won't fix",
          "working as intended",
          "working as designed",
          "closed:question",
          "cannot reproduce");
  private static final FilterByLabel FILTER_BY_LABELS = new FilterByLabel(FILTERING_WORDS, true);
  private static final FilterDefects FILTER_DEFECTS = new FilterDefects();

  private int issueAmountBefore;
  private int issueAmountAfter;

  @Override
  public List<GeneralIssue> apply(
      List<GeneralIssue> list, RepositoryInformation repositoryInformation) {
    issueAmountBefore = list.size();
    Set<GeneralIssue> filteredList =
        new HashSet<>(FILTER_BY_LABELS.apply(FILTER_DEFECTS.apply(list, null), null));
    issueAmountAfter = filteredList.size();
    return filteredList.stream()
        .sorted(Comparator.comparing(GeneralIssue::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public String infoAboutIssueProcessingAction() {
    return "FilterOutIssuesWithoutFix used to remove issue reports which have not received a fix";
  }

  @Override
  public String infoAboutApplicationResult() {
    return String.format("Removed %d issue reports", issueAmountBefore - issueAmountAfter);
  }

  @Override
  public String toString() {
    return "FilterOutIssuesWithoutFix";
  }
}

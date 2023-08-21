package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Filtering out opened issues from list of 
 * {@link fi.muni.cz.reliability.tool.dataprovider.GeneralIssue GeneralIssue} 
 * 
 * @author Radoslav Micko, 445611@muni.cz
 */
public class FilterClosed implements Filter, Serializable {

    @Override
    public List<GeneralIssue> apply(List<GeneralIssue> list) {
        List<GeneralIssue> cloesedIssues = new ArrayList<>();
        for (GeneralIssue issue: list) {
            if (issue.getState().equals("closed")) {
                cloesedIssues.add(issue);
            }
        }
        return cloesedIssues;
    }

    @Override
    public String infoAboutIssueProcessingAction() {
        return "FilterClosed used to filter out opened issues.";
    }
    
    @Override
    public String toString() {
        return "FilterClosed";
    }
}

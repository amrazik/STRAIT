package fi.muni.cz.core.dto;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.core.analysis.phases.modelfitting.TrendTestResult;
import fi.muni.cz.core.analysis.phases.output.writers.ModelResult;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.dataprovider.Release;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** @author Valtteri Valtonen, valtonenvaltteri@gmail.com */
public class ReliabilityAnalysisDto {

  // Configuration
  private ArgsParser configuration;

  private Date analysisDate;

  // Project metadata
  private String projectName;
  private String projectUrl;
  private String projectUser;
  private String projectDescription;
  private int projectSize;
  private int projectContributors;
  private int projectWatchers;
  private int projectForks;
  private Date projectLastPushedAt;
  private Date projectFirstPushedAt;
  private Date testingPeriodStartDate;
  private Date testingPeriodEndDate;

  private long projectDevelopmentDays;
  private List<Release> releases;

  // Analysis metadata
  private String solver;

  // Analysis issue data
  private int issueReportAmountBeforeProcessing;
  private int issueReportAmountAfterProcessing;

  private List<Map<String, String>> issueProcessingResults;
  private List<GeneralIssuesCollection> issueReportSets;

  // Model input data points
  private List<DataPointCollection> cumulativeIssueReportCollections;
  private String cumulativeTimePeriodUnit;
  private List<DataPointCollection> timeBetweenDefectsCollections;
  private String timeBetweenDefectsUnit;

  // Model analysis results
  private List<TrendTestResult> trendTestResults;
  private List<List<ModelResult>> modelResults;

  // Analysis step results
  private List<ReliabilityAnalysisStepResult> analysisStepResults;

  /**
   * Create new reliability analysis dtp
   *
   * @param configuration Command line configuration
   */
  public ReliabilityAnalysisDto(ArgsParser configuration) {

    this.configuration = configuration;

    this.analysisDate = new Date();
  }

  /**
   * Populate repository information related fields
   *
   * @param repositoryInformation repository information object
   * @param projectUrlString project url
   * @param projectUserString project username
   */
  public void addRepositoryInformationData(
      RepositoryInformation repositoryInformation,
      String projectUrlString,
      String projectUserString) {
    projectName = repositoryInformation.getName();
    projectUrl = projectUrlString;
    projectUser = projectUserString;
    projectDescription = repositoryInformation.getDescription();
    projectContributors = repositoryInformation.getContributors();
    projectSize = repositoryInformation.getSize();
    projectWatchers = repositoryInformation.getWatchers();
    projectForks = repositoryInformation.getForks();
    releases = repositoryInformation.getListOfReleases();
    projectFirstPushedAt = repositoryInformation.getPushedAtFirst();
    projectLastPushedAt = repositoryInformation.getPushedAt();

    projectDevelopmentDays =
        TimeUnit.DAYS.convert(
            repositoryInformation.getPushedAt().getTime()
                - repositoryInformation.getPushedAtFirst().getTime(),
            TimeUnit.MILLISECONDS);
  }

  /** Empty issue report list and data point collections in order to save memory */
  public void clearIssueReportsAndDataPoints() {
    setIssueReportSets(new ArrayList<>());
    setCumulativeIssueReportCollections(new ArrayList<>());
    setTimeBetweenDefectsCollections(new ArrayList<>());
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getProjectUrl() {
    return projectUrl;
  }

  public void setProjectUrl(String projectUrl) {
    this.projectUrl = projectUrl;
  }

  public String getProjectUser() {
    return projectUser;
  }

  public void setProjectUser(String projectUser) {
    this.projectUser = projectUser;
  }

  public String getProjectDescription() {
    return projectDescription;
  }

  public void setProjectDescription(String projectDescription) {
    this.projectDescription = projectDescription;
  }

  public int getProjectSize() {
    return projectSize;
  }

  public void setProjectSize(int projectSize) {
    this.projectSize = projectSize;
  }

  public int getProjectContributors() {
    return projectContributors;
  }

  public void setProjectContributors(int projectContributors) {
    this.projectContributors = projectContributors;
  }

  public int getProjectWatchers() {
    return projectWatchers;
  }

  public void setProjectWatchers(int projectWatchers) {
    this.projectWatchers = projectWatchers;
  }

  public int getProjectForks() {
    return projectForks;
  }

  public void setProjectForks(int projectForks) {
    this.projectForks = projectForks;
  }

  public Date getProjectLastPushedAt() {
    return projectLastPushedAt;
  }

  public void setProjectLastPushedAt(Date projectLastPushedAt) {
    this.projectLastPushedAt = projectLastPushedAt;
  }

  public Date getProjectFirstPushedAt() {
    return projectFirstPushedAt;
  }

  public void setProjectFirstPushedAt(Date projectFirstPushedAt) {
    this.projectFirstPushedAt = projectFirstPushedAt;
  }

  public long getProjectDevelopmentDays() {
    return projectDevelopmentDays;
  }

  public void setProjectDevelopmentDays(long projectDevelopmentDays) {
    this.projectDevelopmentDays = projectDevelopmentDays;
  }

  public List<Release> getReleases() {
    return releases;
  }

  public void setReleases(List<Release> releases) {
    this.releases = releases;
  }

  public String getSolver() {
    return solver;
  }

  public void setSolver(String solver) {
    this.solver = solver;
  }

  public int getIssueReportAmountBeforeProcessing() {
    return issueReportAmountBeforeProcessing;
  }

  public void setIssueReportAmountBeforeProcessing(int issueReportAmountBeforeProcessing) {
    this.issueReportAmountBeforeProcessing = issueReportAmountBeforeProcessing;
  }

  public int getIssueReportAmountAfterProcessing() {
    return issueReportAmountAfterProcessing;
  }

  public void setIssueReportAmountAfterProcessing(int issueReportAmountAfterProcessing) {
    this.issueReportAmountAfterProcessing = issueReportAmountAfterProcessing;
  }

  public List<GeneralIssuesCollection> getIssueReportSets() {
    return issueReportSets;
  }

  public void setIssueReportSets(List<GeneralIssuesCollection> issueReportSets) {
    this.issueReportSets = issueReportSets;
  }

  public List<DataPointCollection> getCumulativeIssueReportCollections() {
    return cumulativeIssueReportCollections;
  }

  public void setCumulativeIssueReportCollections(
      List<DataPointCollection> cumulativeIssueReportCollections) {
    this.cumulativeIssueReportCollections = cumulativeIssueReportCollections;
  }

  public List<DataPointCollection> getTimeBetweenDefectsCollections() {
    return timeBetweenDefectsCollections;
  }

  public void setTimeBetweenDefectsCollections(
      List<DataPointCollection> timeBetweenDefectsCollections) {
    this.timeBetweenDefectsCollections = timeBetweenDefectsCollections;
  }

  public String getTimeBetweenDefectsUnit() {
    return timeBetweenDefectsUnit;
  }

  public void setTimeBetweenDefectsUnit(String timeBetweenDefectsUnit) {
    this.timeBetweenDefectsUnit = timeBetweenDefectsUnit;
  }

  public List<List<ModelResult>> getModelResults() {
    return modelResults;
  }

  public void setModelResults(List<List<ModelResult>> modelResults) {
    this.modelResults = modelResults;
  }

  public List<ReliabilityAnalysisStepResult> getAnalysisStepResults() {
    return analysisStepResults;
  }

  public void setAnalysisStepResults(List<ReliabilityAnalysisStepResult> analysisStepResults) {
    this.analysisStepResults = analysisStepResults;
  }

  public ArgsParser getConfiguration() {
    return configuration;
  }

  public void setConfiguration(ArgsParser configuration) {
    this.configuration = configuration;
  }

  public List<TrendTestResult> getTrendTestResults() {
    return trendTestResults;
  }

  public void setTrendTestResults(List<TrendTestResult> trendTestResults) {
    this.trendTestResults = trendTestResults;
  }

  public List<Map<String, String>> getIssueProcessingResults() {
    return issueProcessingResults;
  }

  public void setIssueProcessingResults(List<Map<String, String>> issueProcessingResults) {
    this.issueProcessingResults = issueProcessingResults;
  }

  public Date getAnalysisDate() {
    return analysisDate;
  }

  public void setAnalysisDate(Date analysisDate) {
    this.analysisDate = analysisDate;
  }

  public String getCumulativeTimePeriodUnit() {
    return cumulativeTimePeriodUnit;
  }

  public void setCumulativeTimePeriodUnit(String cumulativeTimePeriodUnit) {
    this.cumulativeTimePeriodUnit = cumulativeTimePeriodUnit;
  }

  public Date getTestingPeriodStartDate() {
    return testingPeriodStartDate;
  }

  public void setTestingPeriodStartDate(Date testingPeriodStartDate) {
    this.testingPeriodStartDate = testingPeriodStartDate;
  }

  public Date getTestingPeriodEndDate() {
    return testingPeriodEndDate;
  }

  public void setTestingPeriodEndDate(Date testingPeriodEndDate) {
    this.testingPeriodEndDate = testingPeriodEndDate;
  }
}

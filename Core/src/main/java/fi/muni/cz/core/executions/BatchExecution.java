package fi.muni.cz.core.executions;

import static fi.muni.cz.dataprocessing.issuesprocessing.modeldata.IssuesCounter.HOURS;
import static fi.muni.cz.dataprocessing.issuesprocessing.modeldata.IssuesCounter.WEEKS;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.core.analysis.ReliabilityAnalysis;
import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.analysis.phases.datacollection.BugzillaDataCollectionPhase;
import fi.muni.cz.core.analysis.phases.datacollection.GithubDataCollectionPhase;
import fi.muni.cz.core.analysis.phases.datacollection.JiraDataCollectionPhase;
import fi.muni.cz.core.analysis.phases.dataprocessing.CumulativeIssueAmountCalculationPhase;
import fi.muni.cz.core.analysis.phases.dataprocessing.IssueReportProcessingPhase;
import fi.muni.cz.core.analysis.phases.dataprocessing.MovingAveragePhase;
import fi.muni.cz.core.analysis.phases.dataprocessing.TimeBetweenIssuesCalculationPhase;
import fi.muni.cz.core.analysis.phases.modelfitting.ModelFittingAndGoodnessOfFitTestPhase;
import fi.muni.cz.core.analysis.phases.modelfitting.TrendTestPhase;
import fi.muni.cz.core.analysis.phases.output.HtmlReportOutputPhase;
import fi.muni.cz.core.analysis.phases.output.writers.CsvFileBatchAnalysisReportWriter;
import fi.muni.cz.core.dto.BatchAnalysisConfiguration;
import fi.muni.cz.core.dto.DataSource;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.core.factory.ModelFactory;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDao;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDaoImpl;
import fi.muni.cz.dataprovider.GitHubGeneralIssueDataProvider;
import fi.muni.cz.dataprovider.GitHubRepositoryInformationDataProvider;
import fi.muni.cz.dataprovider.authenticationdata.GitHubAuthenticationDataProvider;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.egit.github.core.client.GitHubClient;

/** @author Valtteri Valtonen valtonenvaltteri@gmail.com */
public class BatchExecution extends StraitExecution {

  private List<ReliabilityAnalysis> analyses;
  private List<ReliabilityAnalysisDto> analysisData;
  private GitHubGeneralIssueDataProvider githubIssueDataProvider;
  private GitHubRepositoryInformationDataProvider githubRepositoryDataProvider;
  private GeneralIssuesSnapshotDao dao;
  private CsvFileBatchAnalysisReportWriter fileWriter;

  private ArgsParser configuration;

  /** Create new batch execution */
  public BatchExecution() {
    GitHubClient gitHubClient =
        new GitHubAuthenticationDataProvider().getGitHubClientWithCreditials();

    this.githubIssueDataProvider = new GitHubGeneralIssueDataProvider(gitHubClient);
    this.githubRepositoryDataProvider = new GitHubRepositoryInformationDataProvider(gitHubClient);
    this.dao = new GeneralIssuesSnapshotDaoImpl();
    this.analyses = new ArrayList<>();
    this.analysisData = new ArrayList<>();
    this.fileWriter = new CsvFileBatchAnalysisReportWriter();
  }

  @Override
  public void initializeAnalyses(ArgsParser configuration) {

    List<DataSource> dataSources = getDataSourcesFromConfiguration(configuration);
    for (DataSource dataSource : dataSources) {
      analyses.add(getAnalysisBasedOnConfiguration(configuration, dataSource));
    }
    this.configuration = configuration;
  }

  @Override
  public void execute(ArgsParser configuration) {
    System.out.println("Executing STRAIT in batch mode");
    for (ReliabilityAnalysis analysis : analyses) {
      ReliabilityAnalysisDto dto = new ReliabilityAnalysisDto(configuration);
      dto.setConfiguration(this.configuration);
      analysisData.add(analysis.performAnalysis(dto));
    }

    fileWriter.writeBatchOutputDataToFile(analysisData);
  }

  private ReliabilityAnalysis getAnalysisBasedOnConfiguration(
      ArgsParser configuration, DataSource dataSource) {

    String periodOfTestingValue =
        configuration.getOptionValuePeriodOfTesting() != null
            ? configuration.getOptionValuePeriodOfTesting()
            : WEEKS;

    String timeBetweenIssuesUnitValue =
        configuration.getOptionValueTimeBetweenIssuesUnit() != null
            ? configuration.getOptionValueTimeBetweenIssuesUnit()
            : HOURS;

    List<ReliabilityAnalysisPhase> analysisPhases = new ArrayList<>();

    List<DataSource> dataSources = new ArrayList<>();
    dataSources.add(dataSource);

    if (dataSources.get(0).getType().equals("github")) {
      analysisPhases.add(
          new GithubDataCollectionPhase(
              dataSources,
              getDataCollectionCacheModeFromConfiguration(configuration),
              githubIssueDataProvider,
              githubRepositoryDataProvider,
              dao));
    }

    if (dataSources.get(0).getType().equals("jira")) {
      analysisPhases.add(new JiraDataCollectionPhase(dataSources));
    }

    if (dataSources.get(0).getType().equals("bugzilla")) {
      analysisPhases.add(new BugzillaDataCollectionPhase(dataSources));
    }

    analysisPhases.add(new IssueReportProcessingPhase(getStrategyFromConfiguration(configuration)));

    analysisPhases.add(new CumulativeIssueAmountCalculationPhase(periodOfTestingValue));

    if (configuration.hasOptionMovingAverage()) {
      analysisPhases.add(new MovingAveragePhase());
    }

    analysisPhases.add(new TimeBetweenIssuesCalculationPhase(timeBetweenIssuesUnitValue));

    analysisPhases.add(new TrendTestPhase());

    analysisPhases.add(new ModelFittingAndGoodnessOfFitTestPhase(ModelFactory.getREngine()));

    analysisPhases.add(new HtmlReportOutputPhase());

    ReliabilityAnalysis reliabilityAnalysis = new ReliabilityAnalysis(analysisPhases);

    return reliabilityAnalysis;
  }

  private List<DataSource> getDataSourcesFromConfiguration(ArgsParser configuration) {

    List<String> errors = List.of();
    BatchAnalysisConfiguration batchConfiguration =
        configuration.parseBatchAnalysisConfigurationFromFile(
            configuration.getOptionValueBatchConfigurationFile(), errors);

    return batchConfiguration.getDataSources();
  }
}

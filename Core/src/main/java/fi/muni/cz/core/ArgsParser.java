package fi.muni.cz.core;

import static fi.muni.cz.core.executions.RunConfiguration.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.muni.cz.core.dto.BatchAnalysisConfiguration;
import fi.muni.cz.core.exception.InvalidInputException;
import fi.muni.cz.core.executions.RunConfiguration;
import fi.muni.cz.core.factory.FilterFactory;
import fi.muni.cz.dataprocessing.issuesprocessing.modeldata.IssuesCounter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.cli.*;

/** @author Radoslav Micko, 445611@muni.cz */
public class ArgsParser {

  // Initial Mandatory options
  public static final String OPT_URL = "url";
  public static final String OPT_SNAPSHOT_NAME = "sn";
  public static final String OPT_LIST_ALL_SNAPSHOTS = "asl";
  public static final String OPT_HELP = "h";
  public static final String OPT_CONFIG_FILE = "cf";

  public static final String OPT_BATCH_CONFIG_FILE = "bcf";

  // Rest of Mandatory options
  public static final String OPT_LIST_SNAPSHOTS = "sl";
  public static final String OPT_EVALUATE = "e";
  public static final String OPT_PREDICT = "p";
  public static final String OPT_FILTER_LABELS = "fl";
  public static final String OPT_FILTER_CLOSED = "fc";
  public static final String OPT_FILTER_TIME = "ft";
  public static final String OPT_FILTER_DUPLICATIONS = "fdu";
  public static final String OPT_FILTER_DEFECTS = "fde";

  public static final String OPT_FILTER_INVALID_ISSUES = "fi";
  public static final String OPT_FILTER_ISSUES_WITH_LOW_CRITICALITY = "flc";
  public static final String OPT_FILTER_ISSUES_WITH_NO_FIX = "fnf";
  public static final String OPT_FILTER_TEST_RELATED_ISSUES = "fte";

  public static final String OPT_FILTER_LATEST_RELEASE = "flr";
  public static final String OPT_FILTER_BEFORE_FIRST_RELEASE = "fbfr";

  public static final String OPT_MODELS = "ms";
  public static final String OPT_MOVING_AVERAGE = "ma";
  public static final String OPT_OUT = "out";
  public static final String OPT_GRAPH_MULTIPLE = "gm";
  public static final String OPT_PERIOD_OF_TESTING = "pt";
  public static final String OPT_TIME_BETWEEN_ISSUES_UNIT = "tb";
  public static final String OPT_SOLVER = "so";

  public static final String OPT_NO_DATABASE = "ndb";
  public static final String OPT_OVERWRITE_DATABASE = "odb";

  public static final String OPT_ROUNDING = "rd";

  // Configuraton file option
  private static final String FLAG_CONFIG_FILE = "-cf";

  private static final String FLAG_BATCH_CONFIG_FILE = "-bcf";

  private CommandLine cmdl;
  private Options options;

  /**
   * Parse and check input arguments.
   *
   * @param args arguments to check and parse.
   * @throws InvalidInputException If some error occurs.
   */
  public void parse(String[] args) throws InvalidInputException {
    List<String> errors = new ArrayList<>();
    getConfiguredOptions();
    if (checkToReadFromConfigFile(args, errors)) {
      cmdl = parseArgumentsFromConfigFile(args[1], options, errors);
    } else {
      cmdl = parseArgumentsFromCommandLine(args, options, errors);
    }

    if (!errors.isEmpty()) {
      throw new InvalidInputException(errors);
    }
  }

  /** Print argument options. */
  public void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("Help:", options);
  }

  private CommandLine parseArgumentsFromConfigFile(
      String path, Options options, List<String> errors) {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
      String[] args = reader.readLine().split(" ");
      return parseArgumentsFromCommandLine(args, options, errors);
    } catch (IOException ex) {
      errors.add(ex.getMessage());
    }
    return null;
  }

  private CommandLine parseArgumentsFromCommandLine(
      String[] args, Options options, List<String> errors) {
    try {
      return new DefaultParser().parse(options, args);
    } catch (ParseException ex) {
      errors.add(ex.getMessage());
    }
    return null;
  }

  private boolean checkToReadFromConfigFile(String[] args, List<String> errors) {
    if (args.length < 1) {
      errors.add("Missing options.");
      return false;
    }
    if (args[0].equals(ArgsParser.FLAG_CONFIG_FILE) && args.length != 2) {
      errors.add("Missing options.");
      return false;
    }
    return args[0].equals(ArgsParser.FLAG_CONFIG_FILE);
  }

  /**
   * @param filePath file path
   * @param errors errors
   * @return Batch analysis configuration
   */
  public BatchAnalysisConfiguration parseBatchAnalysisConfigurationFromFile(
      String filePath, List<String> errors) {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath))))) {
      String fileContent = reader.lines().collect(Collectors.joining());
      return new ObjectMapper().readerFor(BatchAnalysisConfiguration.class).readValue(fileContent);
    } catch (IOException ex) {
      errors.add(ex.getMessage());
      return null;
    }
  }

  @SuppressWarnings("checkstyle:methodlength")
  private void getConfiguredOptions() {
    options = new Options();
    OptionGroup mandatoryOptionGroup = new OptionGroup();
    Option option =
        Option.builder(OPT_CONFIG_FILE)
            .hasArg()
            .argName("Path to file")
            .desc("Configuration file.")
            .build();
    mandatoryOptionGroup.addOption(option);
    option =
        Option.builder(OPT_BATCH_CONFIG_FILE)
            .hasArg()
            .argName("Path to file")
            .desc("Configuration file for batch analysis")
            .build();
    mandatoryOptionGroup.addOption(option);
    option = Option.builder(OPT_URL).hasArg().argName("URL of repository").build();
    mandatoryOptionGroup.addOption(option);
    option =
        Option.builder(OPT_SNAPSHOT_NAME)
            .longOpt("snapshotName")
            .hasArg()
            .argName("Name of snapshot")
            .build();
    mandatoryOptionGroup.addOption(option);
    option = Option.builder(OPT_LIST_ALL_SNAPSHOTS).longOpt("allSnapshotsList").build();
    mandatoryOptionGroup.addOption(option);
    option = Option.builder(OPT_HELP).longOpt("help").build();
    mandatoryOptionGroup.addOption(option);
    mandatoryOptionGroup.isRequired();
    options.addOptionGroup(mandatoryOptionGroup);

    mandatoryOptionGroup = new OptionGroup();
    option = Option.builder(OPT_LIST_SNAPSHOTS).longOpt("snapshotsList").build();
    mandatoryOptionGroup.addOption(option);
    option =
        Option.builder(OPT_EVALUATE)
            .longOpt("evaluate")
            .optionalArg(true)
            .hasArg()
            .argName("Output file name")
            .desc("Evaluate repository data and persist to new snapshot with name.")
            .build();
    mandatoryOptionGroup.addOption(option);
    options.addOptionGroup(mandatoryOptionGroup);

    option =
        Option.builder(OPT_PREDICT)
            .longOpt("predict")
            .type(Number.class)
            .hasArg()
            .argName("Number")
            .desc("Number of test periods to predict.")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_FILTER_LABELS)
            .longOpt("filterLabel")
            .optionalArg(true)
            .hasArgs()
            .argName("Filtering labels")
            .desc("Filter by specified labels.")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_FILTER_CLOSED).longOpt("filterClosed").desc("Filter closed.").build();
    options.addOption(option);
    option =
        Option.builder(OPT_FILTER_TIME)
            .longOpt("filterTime")
            .hasArgs()
            .argName("Time")
            .numberOfArgs(2)
            .desc("Filter by start time and end time. Format: " + FilterFactory.DATE_FORMAT)
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_FILTER_INVALID_ISSUES)
            .longOpt("filterInvalid")
            .desc("Filter out invalid issues based on labels and creation / closing time")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_FILTER_ISSUES_WITH_LOW_CRITICALITY)
            .longOpt("filterLowCriticality")
            .desc("Filter out low criticality issues based on labels")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_FILTER_TEST_RELATED_ISSUES)
            .longOpt("filterTestRelatedIssues")
            .desc("Filter out test related issues based on labels")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_FILTER_ISSUES_WITH_NO_FIX)
            .longOpt("filterIssuesWithNoFix")
            .desc("Filter out issues with no fix based on labels")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_FILTER_DUPLICATIONS)
            .longOpt("filterDuplications")
            .desc("Filter out duplications.")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_FILTER_DEFECTS).longOpt("filterDefects").desc("Filter defects.").build();
    options.addOption(option);
    option =
        Option.builder(OPT_FILTER_LATEST_RELEASE)
            .longOpt("filterLatestRelease")
            .desc("Filter issue reports from most recent release period.")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_FILTER_BEFORE_FIRST_RELEASE)
            .longOpt("filterBeforeFirstRelease")
            .desc("Filter out issue reports that are from time before first release publish date")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_MOVING_AVERAGE)
            .longOpt("--movingAverage")
            .desc("Use moving average on cumulative issue counts before fitting model.")
            .hasArgs()
            .argName("Moving average window size")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_MODELS)
            .longOpt("models")
            .hasArgs()
            .argName("Model name")
            .desc("Models to evaluate. Available models: go, gos, mo, du, hd, we, ye, yr, ll, em.")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_PERIOD_OF_TESTING)
            .longOpt("periodOfTesting")
            .hasArg()
            .argName("Period")
            .desc("Period of testing. e.g.: " + IssuesCounter.WEEKS + ", " + IssuesCounter.MONTHS)
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_TIME_BETWEEN_ISSUES_UNIT)
            .longOpt("timBetweenIssues")
            .hasArg()
            .argName("Time unit")
            .desc("Period of testing. e.g.: " + IssuesCounter.WEEKS + ", " + IssuesCounter.MONTHS)
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_GRAPH_MULTIPLE)
            .longOpt("graphMultiple")
            .desc("Data show in multiple graphs.")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_SOLVER)
            .longOpt("solver")
            .hasArg()
            .desc(
                "Solver to evaluate model parameters. Available solvers: ls, ml (Not implemented).")
            .build();
    options.addOption(option);
    option = Option.builder(OPT_OUT).hasArg().desc("Output type.").build();
    options.addOption(option);
    option =
        Option.builder(OPT_ROUNDING)
            .longOpt("rounding")
            .hasArg()
            .desc("Amount of decimals result should be rounded to")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_NO_DATABASE)
            .longOpt("noDatabase")
            .desc(
                "Disable use of database when collecting issues from GitHub. Meant for use in batch mode.")
            .build();
    options.addOption(option);
    option =
        Option.builder(OPT_OVERWRITE_DATABASE)
            .longOpt("overwriteDatabase")
            .desc(
                "Overwrite snapshots in database when collecting issues from Github. Meant for use in batch mode.")
            .build();
    options.addOption(option);
  }

  /**
   * Parsed command line.
   *
   * @return CommandLine
   */
  public CommandLine getCmdl() {
    return cmdl;
  }

  /**
   * Configured options.
   *
   * @return Options
   */
  public Options getOptions() {
    return options;
  }

  /**
   * Get number representation of options from command line.
   *
   * @return number representation.
   */
  public RunConfiguration getRunConfiguration() {
    if (hasOptionListAllSnapshots()) {
      return LIST_ALL_SNAPSHOTS;
    } else if (hasOptionHelp()) {
      return HELP;
    } else if (hasOptionBatchConfigFile() && hasOptionEvaluate()) {
      return BATCH_AND_EVALUATE;
    } else if (hasOptionUrl() && hasOptionListSnapshots()) {
      return URL_AND_LIST_SNAPSHOTS;
    } else if (hasOptionUrl() && hasOptionEvaluate()) {
      return URL_AND_EVALUATE;
    } else if (hasOptionSnapshotName()
        && hasOptionEvaluate()
        && !(hasOptionNoDatabase() || hasOptionOverwriteDatabase())) {
      return SNAPSHOT_NAME_AND_EVALUATE;
    } else if (hasOptionSnapshotName()
        && hasOptionListSnapshots()
        && !(hasOptionNoDatabase() || hasOptionOverwriteDatabase())) {
      return SNAPSHOT_NAME_AND_LIST_SNAPSHOTS;
    } else if (hasOptionSnapshotName()) {
      return NOT_SUPPORTED;
    } else {
      return UNSPECIFIED;
    }
  }

  /**
   * Check if option 'cf' is on command line.
   *
   * @return true if there is 'cf', false otherwise.
   */
  public boolean hasOptionConfigFile() {
    return cmdl.hasOption(OPT_CONFIG_FILE);
  }

  /**
   * Check if option 'bcf' is on command line.
   *
   * @return true if there is 'bcf', false otherwise.
   */
  public boolean hasOptionBatchConfigFile() {
    return cmdl.hasOption(OPT_BATCH_CONFIG_FILE);
  }

  /**
   * Check if option 'so' is on command line.
   *
   * @return true if there is 'so', false otherwise.
   */
  public boolean hasOptionSolver() {
    return cmdl.hasOption(OPT_SOLVER);
  }

  /**
   * Check if option 'url' is on command line.
   *
   * @return true if there is 'url' on command line, false otherwise.
   */
  public boolean hasOptionUrl() {
    return cmdl.hasOption(OPT_URL);
  }

  /**
   * Check if option 'cf' is on command line.
   *
   * @return true if there is 'cf' command line, false otherwise.
   */
  public boolean hasOptionSnapshotName() {
    return cmdl.hasOption(OPT_SNAPSHOT_NAME);
  }

  /**
   * Check if option 'asl' is on command line.
   *
   * @return true if there is 'asl' command line, false otherwise.
   */
  public boolean hasOptionListAllSnapshots() {
    return cmdl.hasOption(OPT_LIST_ALL_SNAPSHOTS);
  }

  /**
   * Check if option 'h' is on command line.
   *
   * @return true if there is 'h' command line, false otherwise.
   */
  public boolean hasOptionHelp() {
    return cmdl.hasOption(OPT_HELP);
  }

  /**
   * Check if option 'ls' is on command line.
   *
   * @return true if there is 'ls' command line, false otherwise.
   */
  public boolean hasOptionListSnapshots() {
    return cmdl.hasOption(OPT_LIST_SNAPSHOTS);
  }

  /**
   * Check if option 'e' is on command line.
   *
   * @return true if there is 'e' command line, false otherwise.
   */
  public boolean hasOptionEvaluate() {
    return cmdl.hasOption(OPT_EVALUATE);
  }

  /**
   * Check if option 'p' is on command line.
   *
   * @return true if there is 'p' command line, false otherwise.
   */
  public boolean hasOptionPredict() {
    return cmdl.hasOption(OPT_PREDICT);
  }

  /**
   * Check if option 'fc' is on command line.
   *
   * @return true if there is 'fc' command line, false otherwise.
   */
  public boolean hasOptionFilterClosed() {
    return cmdl.hasOption(OPT_FILTER_CLOSED);
  }

  /**
   * Check if option 'ft' is on command line.
   *
   * @return true if there is 'ft', false otherwise.
   */
  public boolean hasOptionFilterTime() {
    return cmdl.hasOption(OPT_FILTER_TIME);
  }

  /**
   * Check if option 'fl' is on command line.
   *
   * @return true if there is 'fl' command line, false otherwise.
   */
  public boolean hasOptionFilterLabels() {
    return cmdl.hasOption(OPT_FILTER_LABELS);
  }

  /**
   * Check if option 'fdu' is on command line.
   *
   * @return true if there is 'fdu' command line, false otherwise.
   */
  public boolean hasOptionFilterDuplications() {
    return cmdl.hasOption(OPT_FILTER_DUPLICATIONS);
  }

  /**
   * Check if option 'fde' is on command line.
   *
   * @return true if there is 'fde' command line, false otherwise.
   */
  public boolean hasOptionFilterDefects() {
    return cmdl.hasOption(OPT_FILTER_DEFECTS);
  }

  /**
   * Check if option 'fi' is on command line.
   *
   * @return true if there is 'fi' command line, false otherwise.
   */
  public boolean hasOptionFilterInvalidIssues() {
    return cmdl.hasOption(OPT_FILTER_INVALID_ISSUES);
  }

  /**
   * Check if option 'fte' is on command line.
   *
   * @return true if there is 'fte' command line, false otherwise.
   */
  public boolean hasOptionFilterTestRelatedissues() {
    return cmdl.hasOption(OPT_FILTER_TEST_RELATED_ISSUES);
  }

  /**
   * Check if option 'fnf' is on command line.
   *
   * @return true if there is 'fnf' command line, false otherwise.
   */
  public boolean hasOptionFilterIssuesWithoutFix() {
    return cmdl.hasOption(OPT_FILTER_ISSUES_WITH_NO_FIX);
  }

  /**
   * Check if option 'fbfr' is on command line.
   *
   * @return true if there is 'fbfr' command line, false otherwise.
   */
  public boolean hasOptionFilterBeforeFirstRelease() {
    return cmdl.hasOption(OPT_FILTER_BEFORE_FIRST_RELEASE);
  }

  /**
   * Check if option 'flc' is on command line.
   *
   * @return true if there is 'flc' command line, false otherwise.
   */
  public boolean hasOptionFilterIssuesWithLowCriticality() {
    return cmdl.hasOption(OPT_FILTER_ISSUES_WITH_LOW_CRITICALITY);
  }

  /**
   * Check if option 'flr' is on command line.
   *
   * @return true if there is 'flr' command line, false otherwise.
   */
  public boolean hasOptionFilterLatestRelease() {
    return cmdl.hasOption(OPT_FILTER_LATEST_RELEASE);
  }

  /**
   * Check if option 'ms' is on command line.
   *
   * @return true if there is 'ms' command line, false otherwise.
   */
  public boolean hasOptionModels() {
    return cmdl.hasOption(OPT_MODELS);
  }

  /**
   * Check if option 'out' is on command line.
   *
   * @return true if there is 'out' command line, false otherwise.
   */
  public boolean hasOptionOut() {
    return cmdl.hasOption(OPT_OUT);
  }

  /**
   * Check if option ma is on command line.
   *
   * @return true if there is 'ma' command line, false otherwise.
   */
  public boolean hasOptionMovingAverage() {
    return cmdl.hasOption(OPT_MOVING_AVERAGE);
  }

  /**
   * Check if option 'pt' is on command line.
   *
   * @return true if there is 'pt' command line, false otherwise.
   */
  public boolean hasOptionPeriodOfTestiong() {
    return cmdl.hasOption(OPT_PERIOD_OF_TESTING);
  }

  /**
   * Check if option 'tb' is on command line.
   *
   * @return true if there is 'tb' command line, false otherwise.
   */
  public boolean hasOptionTimeBetweenIssuesUnit() {
    return cmdl.hasOption(OPT_TIME_BETWEEN_ISSUES_UNIT);
  }

  /**
   * Check if option 'gm' is on command line.
   *
   * @return true if there is 'gm' command line, false otherwise.
   */
  public boolean hasOptionGraphMultiple() {
    return cmdl.hasOption(OPT_GRAPH_MULTIPLE);
  }

  /**
   * Check if option 'ndb' is on command line.
   *
   * @return true if there is 'ndb' command line, false otherwise.
   */
  public boolean hasOptionNoDatabase() {
    return cmdl.hasOption(OPT_NO_DATABASE);
  }

  /**
   * Check if option 'odb' is on command line.
   *
   * @return true if there is 'ndb' command line, false otherwise.
   */
  public boolean hasOptionOverwriteDatabase() {
    return cmdl.hasOption(OPT_OVERWRITE_DATABASE);
  }

  /**
   * Check if option 'rd' is on command line.
   *
   * @return true if there is 'rd' command line, false otherwise.
   */
  public boolean hasOptionRounding() {
    return cmdl.hasOption(OPT_ROUNDING);
  }

  /**
   * Get argument value for 'url'.
   *
   * @return argument value.
   */
  public String getOptionValueUrl() {
    return cmdl.getOptionValue(OPT_URL);
  }

  /**
   * Get argument value for 'so'.
   *
   * @return argument value.
   */
  public String getOptionValueSolver() {
    return cmdl.getOptionValue(OPT_SOLVER);
  }

  /**
   * Get argument value for 'pt'.
   *
   * @return argument value.
   */
  public String getOptionValuePeriodOfTesting() {
    return cmdl.getOptionValue(OPT_PERIOD_OF_TESTING);
  }

  /**
   * Get argument value for 'tb'.
   *
   * @return argument value.
   */
  public String getOptionValueTimeBetweenIssuesUnit() {
    return cmdl.getOptionValue(OPT_TIME_BETWEEN_ISSUES_UNIT);
  }

  /**
   * Get argument value for 'sn'.
   *
   * @return argument value.
   */
  public String getOptionValueSnapshotName() {
    return cmdl.getOptionValue(OPT_SNAPSHOT_NAME);
  }

  /**
   * Get argument value for 'p'.
   *
   * @return argument value.
   */
  public String getOptionValuePredict() {
    return cmdl.getOptionValue(OPT_PREDICT);
  }

  /**
   * Get argument value for 'e'.
   *
   * @return argument value.
   */
  public String getOptionValueEvaluation() {
    return cmdl.getOptionValue(OPT_EVALUATE);
  }

  /**
   * Get argument values for 'fl'.
   *
   * @return argument values.
   */
  public String[] getOptionValuesFilterLabels() {
    return cmdl.getOptionValues(OPT_FILTER_LABELS);
  }

  /**
   * Get argument values for 'ft'.
   *
   * @return argument values.
   */
  public String[] getOptionValuesFilterTime() {
    return cmdl.getOptionValues(OPT_FILTER_TIME);
  }

  /**
   * Get argument value for 'ms'.
   *
   * @return argument values.
   */
  public String[] getOptionValuesModels() {
    return cmdl.getOptionValues(OPT_MODELS);
  }

  /**
   * Get argument value for 'out'.
   *
   * @return argument value.
   */
  public String getOptionValueOut() {
    return cmdl.getOptionValue(OPT_OUT);
  }

  /**
   * Get argument value for 'bcf'.
   *
   * @return argument value
   */
  public String getOptionValueBatchConfigurationFile() {
    return cmdl.getOptionValue(OPT_BATCH_CONFIG_FILE);
  }

  /**
   * Get argument value for 'ma'.
   *
   * @return argument value
   */
  public String getOptionValueMovingAverage() {
    return cmdl.getOptionValue(OPT_MOVING_AVERAGE);
  }

  /**
   * Get argument value for 'rd'.
   *
   * @return argument value
   */
  public String getOptionValueRounding() {
    return cmdl.getOptionValue(OPT_ROUNDING);
  }
}

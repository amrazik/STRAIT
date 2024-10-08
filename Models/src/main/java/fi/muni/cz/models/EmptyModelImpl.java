package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.math3.util.Pair;

/** @author Radoslav Micko, 445611@muni.cz */
public class EmptyModelImpl extends ModelAbstract {

  /**
   * Initialize model attributes.
   *
   * @param trainingData list of issues.
   * @param testData list of issues.
   * @param solver Solver to estimate model parameters.
   */
  public EmptyModelImpl(
      List<Pair<Integer, Integer>> trainingData,
      List<Pair<Integer, Integer>> testData,
      Solver solver) {
    super(trainingData, testData, solver);
  }

  @Override
  protected double getFunctionValue(Integer testPeriod) {
    return 0;
  }

  @Override
  protected void setParametersToMap(double[] params) {
    modelParameters = new HashMap<>();
  }

  @Override
  protected int[] getInitialParametersValue() {
    return new int[] {};
  }

  @Override
  public String getTextFormOfTheFunction() {
    return "μ(t) =";
  }

  @Override
  public String toString() {
    return "Empty model";
  }

  @Override
  protected String getModelShortName() {
    return "EM";
  }
}

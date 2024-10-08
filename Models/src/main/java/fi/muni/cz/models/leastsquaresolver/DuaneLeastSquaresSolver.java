package fi.muni.cz.models.leastsquaresolver;

import java.util.List;
import java.util.Locale;
import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/** @author Radoslav Micko, 445611@muni.cz */
public class DuaneLeastSquaresSolver extends SolverAbstract {

  private static final String MODEL_FUNCTION = "a*(xvalues ^ b)";
  private static final String MODEL_NAME = "modelDuane";

  /**
   * Initialize Rengine.
   *
   * @param rEngine Rengine.
   */
  public DuaneLeastSquaresSolver(Rengine rEngine) {
    super(rEngine);
  }

  @Override
  public SolverResult optimize(int[] startParameters, List<Pair<Integer, Integer>> listOfData) {
    initializeOptimizationInR(listOfData);
    rEngine.eval(
        "modelDuane2 <- nls2(yvalues ~ "
            + MODEL_FUNCTION
            + ", "
            + "start = data.frame(a = c(0.00001, 500),b = c(0.00001, 100)), "
            + "algorithm = \"brute-force\", control = list(warnOnly = TRUE, maxiter = 100000))");
    REXP intermediate = rEngine.eval("coef(" + MODEL_NAME + "2)");
    if (intermediate == null) {
      return new SolverResult();
    }
    rEngine.eval(
        String.format(
            Locale.US,
            "modelDuane <- nls(yvalues ~ "
                + MODEL_FUNCTION
                + ", "
                + "start = list(a = %.10f,b = %.10f), "
                + "lower = list(a = 0, b = 0), "
                + "control = list(warnOnly = TRUE, maxiter = 100000), "
                + "algorithm = \"port\")",
            intermediate.asDoubleArray()[0],
            intermediate.asDoubleArray()[1]));
    REXP result = rEngine.eval("coef(" + MODEL_NAME + ")");

    rEngine.eval("library(broom)");
    REXP aic = rEngine.eval(String.format("glance(%s)$AIC", MODEL_NAME));
    REXP bic = rEngine.eval(String.format("glance(%s)$BIC", MODEL_NAME));

    rEngine.eval("library(aomisc)");
    REXP pseudoRSquared = rEngine.eval(String.format("R2nls(%s)$PseudoR2", MODEL_NAME));

    rEngine.end();
    if (result == null || result.asDoubleArray().length < 2) {
      return new SolverResult();
    }
    double[] d = result.asDoubleArray();

    // REXP rse = rEngine.eval(String.format("glance(%s)$sigma", MODEL_NAME));

    SolverResult solverResult = new SolverResult();
    solverResult.setParameters(new double[] {d[0], d[1]});
    solverResult.setAic(aic.asDoubleArray()[0]);
    solverResult.setBic(bic.asDoubleArray()[0]);
    solverResult.setPseudoRSquared(pseudoRSquared.asDoubleArray()[0]);

    return solverResult;
  }
}

package fi.muni.cz.core.exception;

import java.util.List;

/** @author Radoslav Micko, 445611@muni.cz */
public class InvalidInputException extends Exception {

  private final List<String> causes;

  /**
   * Constructor of <code>InvalidInputException</code>.
   *
   * @param causes causes of exception
   */
  public InvalidInputException(List<String> causes) {
    super();
    this.causes = List.copyOf(causes);
  }

  /** @return causes of exception. */
  public List<String> causes() {
    return this.causes;
  }
}

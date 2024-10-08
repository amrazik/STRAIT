package fi.muni.cz.dataprovider;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang3.StringUtils;

/** @author Radoslav Micko, 445611@muni.cz */
@Entity
public class Release implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  @Column(name = "repo_info_id")
  private Long repositoryInformationId;

  private String name;

  @Temporal(TemporalType.TIMESTAMP)
  private Date publishedAt;

  public Long getRepositoryInformationId() {
    return repositoryInformationId;
  }

  public void setRepositoryInformationId(Long repositoryInformationId) {
    this.repositoryInformationId = repositoryInformationId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(Date publishedAt) {
    this.publishedAt = publishedAt;
  }

  /**
   * Convert to DTO.
   *
   * @return DTO
   */
  public ReleaseDTO toDto() {
    return new ReleaseDTO()
        .setName(StringUtils.isBlank(name) ? "Release" : name)
        .setPublishedAt(publishedAt);
  }
}

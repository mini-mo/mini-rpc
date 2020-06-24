package com.mng.rpc.example.api;

import java.io.Serializable;
import java.util.Date;

public class AccountDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  private Long id;
  private String username;
  private Date createAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Date getCreateAt() {
    return createAt;
  }

  public void setCreateAt(Date createAt) {
    this.createAt = createAt;
  }

  @Override
  public String toString() {
    return "AccountDTO{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", createAt=" + createAt +
        '}';
  }
}

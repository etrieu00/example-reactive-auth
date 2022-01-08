package com.etrieu00.examplereactiveauth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("app_user")
public class AppUser {
  @Id
  private Long id;
  private String uuid;
  private String userRole;
  private String userEmail;
  private String userPassword;
  @CreatedBy
  private String ufc;
  @LastModifiedBy
  private String ulm;
  @CreatedDate
  private LocalDateTime dtc;
  @LastModifiedDate
  private LocalDateTime dtm;

  public AppUser setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public AppUser setUserRole(String role) {
    this.userRole = role;
    return this;
  }

  public AppUser setUserEmail(String userEmail) {
    this.userEmail = userEmail;
    return this;
  }

  public AppUser setUserPassword(String userPassword) {
    this.userPassword = userPassword;
    return this;
  }

  public static AppUser build(Function<AppUser, AppUser> builder) {
    return builder.apply(new AppUser());
  }
}

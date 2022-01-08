package com.etrieu00.examplereactiveauth.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("app_profile")
public class AppProfile {
  @Id
  private Long id;
  private String uuid;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String gender;
  private LocalDate dateOfBirth;

  public AppProfile setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public AppProfile setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public AppProfile setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public AppProfile setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public AppProfile setGender(String gender) {
    this.gender = gender;
    return this;
  }

  public AppProfile setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  public static AppProfile build(Function<AppProfile, AppProfile> builder) {
    return builder.apply(new AppProfile());
  }

}

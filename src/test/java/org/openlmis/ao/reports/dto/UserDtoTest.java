package org.openlmis.ao.reports.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.openlmis.ao.reports.dto.external.UserDto;

public class UserDtoTest {

  private UserDto userDto = new UserDto();

  @Before
  public void setUp() {
    userDto.setUsername("jdoe");
  }

  @Test
  public void shouldPrintNameAsFirstLastName() {
    userDto.setFirstName("John");
    userDto.setLastName("Doe");

    assertEquals("John Doe", userDto.printName());
  }

  @Test
  public void shouldPrintNameAsOnlyFirstName() {
    userDto.setFirstName("John");

    assertEquals("John", userDto.printName());
  }

  @Test
  public void shouldPrintNameAsOnlyLastName() {
    userDto.setLastName("Doe");

    assertEquals("Doe", userDto.printName());
  }

  @Test
  public void shouldPrintNameAsUsername() {
    assertEquals("jdoe", userDto.printName());
  }
}

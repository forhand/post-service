package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.contact.PreferredContact;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto implements Serializable {

  private Long id;
  @NotBlank
  private String username;
  private int age;
  @NotBlank
  private String email;
  private PreferredContact preferredContact;
  private boolean active;
  private List<Long> followerIds;
  private List<Long> followeeIds;
}

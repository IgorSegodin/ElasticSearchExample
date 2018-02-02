package org.isegodin.example.elastic.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author i.segodin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Identifier<Long> {
    Long id;
    String name;
    String surname;
    String email;
    String telephone;
    String jobTitle;
}

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
public class PropertyDto implements Identifier<Long> {
    Long id;
    String address1;
    String address2;
    String address3;
    String address4;
    String address5;
    String postCode;
    String email;
    String telephone;
    String fax;
    String note;
}

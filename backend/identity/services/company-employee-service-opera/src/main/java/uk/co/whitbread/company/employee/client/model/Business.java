package uk.co.whitbread.company.employee.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Business implements Serializable {

    private AccessLevel accessLevel;

    private String employeeId;
}

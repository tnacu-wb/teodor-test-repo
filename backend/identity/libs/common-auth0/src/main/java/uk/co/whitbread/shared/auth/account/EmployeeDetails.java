package uk.co.whitbread.shared.auth.account;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetails implements Serializable {
	
	private String companyId;
	private String employeeId;
}

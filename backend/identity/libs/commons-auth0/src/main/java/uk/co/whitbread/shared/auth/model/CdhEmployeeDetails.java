package uk.co.whitbread.shared.auth.model;

import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class CdhEmployeeDetails implements Serializable {
	
	private String companyAccountId;
	private String employeeAccountId;
	private String userEmail;
	private String accessLevel;
}

package uk.co.whitbread.shared.auth.account;

import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class CCUIDetails implements Serializable {
	
	private String email;
	private String bookingFlow;
}

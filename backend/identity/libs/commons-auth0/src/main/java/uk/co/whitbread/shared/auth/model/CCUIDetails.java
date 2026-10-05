package uk.co.whitbread.shared.auth.model;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class CCUIDetails implements Serializable {
	
	private String email;
	
	private String bookingFlow;
	
}

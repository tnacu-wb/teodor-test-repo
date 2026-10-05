package uk.co.whitbread.contentservice.roomtypes.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CookiePoliciesResponse implements Serializable {
    private static final long serialVersionUID = 7420231625318575251L;
    private CookiePolicies cookiePolicies;
}

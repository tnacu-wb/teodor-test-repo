package uk.co.whitbread.shared.auth.jwt;


import lombok.Data;

@Data
public class AuthProvider {

    private String domain;
    private String host;
    private String issuer;
}

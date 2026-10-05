package uk.co.whitbread.piba.account.model;

public record WorldLineTcpHeaders(
    String companyNumber,
    TrustedPartnerCredentials trustedPartnerCredentials,
    String cultureCode,
    String ipAddress,
    String tetheredUserGuid) {}

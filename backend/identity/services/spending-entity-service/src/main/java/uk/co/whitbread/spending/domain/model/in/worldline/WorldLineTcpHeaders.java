package uk.co.whitbread.spending.domain.model.in.worldline;

public record WorldLineTcpHeaders(
    String companyNumber,
    TrustedPartnerCredentials trustedPartnerCredentials,
    String cultureCode,
    String ipAddress,
    String tetheredUserGuid) {}

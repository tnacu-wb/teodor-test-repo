package uk.co.whitbread.shared.auth.constants;

/**
 * JWT claim name constants shared across TokenExtractor and JwtParser.
 * <p>
 * Namespace-prefixed claims (CUSTOMER_ACCOUNT_ID, COMPANY_ACCOUNT_ID, etc.)
 * are stored as suffixes — prepend {@code namespace + "/"} at runtime using
 * the tenant's configured namespace.
 */
public final class ClaimNames {

    // Direct top-level claims
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PROFILE = "profile";
    public static final String SESSION_ID = "sessionId";
    public static final String BOOKING_FLOW = "bookingFlow";

    // Claims nested under the "profile" object
    public static final String COMPANY_ID = "companyId";
    public static final String EMPLOYEE_ID = "employeeId";
    public static final String ACCESS_LEVEL = "accessLevel";

    // Namespace-prefixed claim suffixes (use as: namespace + "/" + CUSTOMER_ACCOUNT_ID)
    public static final String CUSTOMER_ACCOUNT_ID = "customerAccountId";
    public static final String EMPLOYEE_ACCOUNT_ID = "employeeAccountId";
    public static final String COMPANY_ACCOUNT_ID = "companyAccountId";
    public static final String OPERA_COMPANY_ID = "operaCompanyId";

    private ClaimNames() {
    }
}


package uk.co.whitbread.qas.addresslookup.api;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.co.whitbread.qas.addresslookup.api package. 
 * <p>An ObjectFactory allows you to programmatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _UsernameToken_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "UsernameToken");
    private static final QName _BinarySecurityToken_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "BinarySecurityToken");
    private static final QName _Reference_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Reference");
    private static final QName _Embedded_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Embedded");
    private static final QName _KeyIdentifier_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "KeyIdentifier");
    private static final QName _SecurityTokenReference_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "SecurityTokenReference");
    private static final QName _Security_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
    private static final QName _TransformationParameters_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "TransformationParameters");
    private static final QName _Password_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Password");
    private static final QName _Nonce_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Nonce");
    private static final QName _Timestamp_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Timestamp");
    private static final QName _Expires_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Expires");
    private static final QName _Created_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Created");
    private static final QName _QAAuthentication_QNAME = new QName("http://www.qas.com/OnDemand-2011-03", "QAAuthentication");
    private static final QName _QAQueryHeader_QNAME = new QName("http://www.qas.com/OnDemand-2011-03", "QAQueryHeader");
    private static final QName _QAInformation_QNAME = new QName("http://www.qas.com/OnDemand-2011-03", "QAInformation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.co.whitbread.qas.addresslookup.api
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UsernameTokenType }
     * 
     * @return
     *     the new instance of {@link UsernameTokenType }
     */
    public UsernameTokenType createUsernameTokenType() {
        return new UsernameTokenType();
    }

    /**
     * Create an instance of {@link BinarySecurityTokenType }
     * 
     * @return
     *     the new instance of {@link BinarySecurityTokenType }
     */
    public BinarySecurityTokenType createBinarySecurityTokenType() {
        return new BinarySecurityTokenType();
    }

    /**
     * Create an instance of {@link ReferenceType }
     * 
     * @return
     *     the new instance of {@link ReferenceType }
     */
    public ReferenceType createReferenceType() {
        return new ReferenceType();
    }

    /**
     * Create an instance of {@link EmbeddedType }
     * 
     * @return
     *     the new instance of {@link EmbeddedType }
     */
    public EmbeddedType createEmbeddedType() {
        return new EmbeddedType();
    }

    /**
     * Create an instance of {@link KeyIdentifierType }
     * 
     * @return
     *     the new instance of {@link KeyIdentifierType }
     */
    public KeyIdentifierType createKeyIdentifierType() {
        return new KeyIdentifierType();
    }

    /**
     * Create an instance of {@link SecurityTokenReferenceType }
     * 
     * @return
     *     the new instance of {@link SecurityTokenReferenceType }
     */
    public SecurityTokenReferenceType createSecurityTokenReferenceType() {
        return new SecurityTokenReferenceType();
    }

    /**
     * Create an instance of {@link SecurityHeaderType }
     * 
     * @return
     *     the new instance of {@link SecurityHeaderType }
     */
    public SecurityHeaderType createSecurityHeaderType() {
        return new SecurityHeaderType();
    }

    /**
     * Create an instance of {@link TransformationParametersType }
     * 
     * @return
     *     the new instance of {@link TransformationParametersType }
     */
    public TransformationParametersType createTransformationParametersType() {
        return new TransformationParametersType();
    }

    /**
     * Create an instance of {@link PasswordString }
     * 
     * @return
     *     the new instance of {@link PasswordString }
     */
    public PasswordString createPasswordString() {
        return new PasswordString();
    }

    /**
     * Create an instance of {@link EncodedString }
     * 
     * @return
     *     the new instance of {@link EncodedString }
     */
    public EncodedString createEncodedString() {
        return new EncodedString();
    }

    /**
     * Create an instance of {@link AttributedString }
     * 
     * @return
     *     the new instance of {@link AttributedString }
     */
    public AttributedString createAttributedString() {
        return new AttributedString();
    }

    /**
     * Create an instance of {@link TimestampType }
     * 
     * @return
     *     the new instance of {@link TimestampType }
     */
    public TimestampType createTimestampType() {
        return new TimestampType();
    }

    /**
     * Create an instance of {@link AttributedDateTime }
     * 
     * @return
     *     the new instance of {@link AttributedDateTime }
     */
    public AttributedDateTime createAttributedDateTime() {
        return new AttributedDateTime();
    }

    /**
     * Create an instance of {@link AttributedURI }
     * 
     * @return
     *     the new instance of {@link AttributedURI }
     */
    public AttributedURI createAttributedURI() {
        return new AttributedURI();
    }

    /**
     * Create an instance of {@link QAAuthentication }
     * 
     * @return
     *     the new instance of {@link QAAuthentication }
     */
    public QAAuthentication createQAAuthentication() {
        return new QAAuthentication();
    }

    /**
     * Create an instance of {@link QAQueryHeader }
     * 
     * @return
     *     the new instance of {@link QAQueryHeader }
     */
    public QAQueryHeader createQAQueryHeader() {
        return new QAQueryHeader();
    }

    /**
     * Create an instance of {@link QAInformation }
     * 
     * @return
     *     the new instance of {@link QAInformation }
     */
    public QAInformation createQAInformation() {
        return new QAInformation();
    }

    /**
     * Create an instance of {@link QASearch }
     * 
     * @return
     *     the new instance of {@link QASearch }
     */
    public QASearch createQASearch() {
        return new QASearch();
    }

    /**
     * Create an instance of {@link EngineType }
     * 
     * @return
     *     the new instance of {@link EngineType }
     */
    public EngineType createEngineType() {
        return new EngineType();
    }

    /**
     * Create an instance of {@link QASearchResult }
     * 
     * @return
     *     the new instance of {@link QASearchResult }
     */
    public QASearchResult createQASearchResult() {
        return new QASearchResult();
    }

    /**
     * Create an instance of {@link QAPicklistType }
     * 
     * @return
     *     the new instance of {@link QAPicklistType }
     */
    public QAPicklistType createQAPicklistType() {
        return new QAPicklistType();
    }

    /**
     * Create an instance of {@link QAAddressType }
     * 
     * @return
     *     the new instance of {@link QAAddressType }
     */
    public QAAddressType createQAAddressType() {
        return new QAAddressType();
    }

    /**
     * Create an instance of {@link VerificationFlagsType }
     * 
     * @return
     *     the new instance of {@link VerificationFlagsType }
     */
    public VerificationFlagsType createVerificationFlagsType() {
        return new VerificationFlagsType();
    }

    /**
     * Create an instance of {@link QARefine }
     * 
     * @return
     *     the new instance of {@link QARefine }
     */
    public QARefine createQARefine() {
        return new QARefine();
    }

    /**
     * Create an instance of {@link Picklist }
     * 
     * @return
     *     the new instance of {@link Picklist }
     */
    public Picklist createPicklist() {
        return new Picklist();
    }

    /**
     * Create an instance of {@link QAGetAddress }
     * 
     * @return
     *     the new instance of {@link QAGetAddress }
     */
    public QAGetAddress createQAGetAddress() {
        return new QAGetAddress();
    }

    /**
     * Create an instance of {@link Address }
     * 
     * @return
     *     the new instance of {@link Address }
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link QAData }
     * 
     * @return
     *     the new instance of {@link QAData }
     */
    public QAData createQAData() {
        return new QAData();
    }

    /**
     * Create an instance of {@link QADataSet }
     * 
     * @return
     *     the new instance of {@link QADataSet }
     */
    public QADataSet createQADataSet() {
        return new QADataSet();
    }

    /**
     * Create an instance of {@link QAGetData }
     * 
     * @return
     *     the new instance of {@link QAGetData }
     */
    public QAGetData createQAGetData() {
        return new QAGetData();
    }

    /**
     * Create an instance of {@link QAGetLicenseInfo }
     * 
     * @return
     *     the new instance of {@link QAGetLicenseInfo }
     */
    public QAGetLicenseInfo createQAGetLicenseInfo() {
        return new QAGetLicenseInfo();
    }

    /**
     * Create an instance of {@link QAGetSystemInfo }
     * 
     * @return
     *     the new instance of {@link QAGetSystemInfo }
     */
    public QAGetSystemInfo createQAGetSystemInfo() {
        return new QAGetSystemInfo();
    }

    /**
     * Create an instance of {@link QAGetDataMapDetail }
     * 
     * @return
     *     the new instance of {@link QAGetDataMapDetail }
     */
    public QAGetDataMapDetail createQAGetDataMapDetail() {
        return new QAGetDataMapDetail();
    }

    /**
     * Create an instance of {@link QADataMapDetail }
     * 
     * @return
     *     the new instance of {@link QADataMapDetail }
     */
    public QADataMapDetail createQADataMapDetail() {
        return new QADataMapDetail();
    }

    /**
     * Create an instance of {@link QALicensedSet }
     * 
     * @return
     *     the new instance of {@link QALicensedSet }
     */
    public QALicensedSet createQALicensedSet() {
        return new QALicensedSet();
    }

    /**
     * Create an instance of {@link QALicenceInfo }
     * 
     * @return
     *     the new instance of {@link QALicenceInfo }
     */
    public QALicenceInfo createQALicenceInfo() {
        return new QALicenceInfo();
    }

    /**
     * Create an instance of {@link QASystemInfo }
     * 
     * @return
     *     the new instance of {@link QASystemInfo }
     */
    public QASystemInfo createQASystemInfo() {
        return new QASystemInfo();
    }

    /**
     * Create an instance of {@link QAGetExampleAddresses }
     * 
     * @return
     *     the new instance of {@link QAGetExampleAddresses }
     */
    public QAGetExampleAddresses createQAGetExampleAddresses() {
        return new QAGetExampleAddresses();
    }

    /**
     * Create an instance of {@link QAExampleAddresses }
     * 
     * @return
     *     the new instance of {@link QAExampleAddresses }
     */
    public QAExampleAddresses createQAExampleAddresses() {
        return new QAExampleAddresses();
    }

    /**
     * Create an instance of {@link QAExampleAddress }
     * 
     * @return
     *     the new instance of {@link QAExampleAddress }
     */
    public QAExampleAddress createQAExampleAddress() {
        return new QAExampleAddress();
    }

    /**
     * Create an instance of {@link QAGetLayouts }
     * 
     * @return
     *     the new instance of {@link QAGetLayouts }
     */
    public QAGetLayouts createQAGetLayouts() {
        return new QAGetLayouts();
    }

    /**
     * Create an instance of {@link QALayouts }
     * 
     * @return
     *     the new instance of {@link QALayouts }
     */
    public QALayouts createQALayouts() {
        return new QALayouts();
    }

    /**
     * Create an instance of {@link QALayout }
     * 
     * @return
     *     the new instance of {@link QALayout }
     */
    public QALayout createQALayout() {
        return new QALayout();
    }

    /**
     * Create an instance of {@link QAGetPromptSet }
     * 
     * @return
     *     the new instance of {@link QAGetPromptSet }
     */
    public QAGetPromptSet createQAGetPromptSet() {
        return new QAGetPromptSet();
    }

    /**
     * Create an instance of {@link QAPromptSet }
     * 
     * @return
     *     the new instance of {@link QAPromptSet }
     */
    public QAPromptSet createQAPromptSet() {
        return new QAPromptSet();
    }

    /**
     * Create an instance of {@link PromptLine }
     * 
     * @return
     *     the new instance of {@link PromptLine }
     */
    public PromptLine createPromptLine() {
        return new PromptLine();
    }

    /**
     * Create an instance of {@link QACanSearch }
     * 
     * @return
     *     the new instance of {@link QACanSearch }
     */
    public QACanSearch createQACanSearch() {
        return new QACanSearch();
    }

    /**
     * Create an instance of {@link QASearchOk }
     * 
     * @return
     *     the new instance of {@link QASearchOk }
     */
    public QASearchOk createQASearchOk() {
        return new QASearchOk();
    }

    /**
     * Create an instance of {@link PicklistEntryType }
     * 
     * @return
     *     the new instance of {@link PicklistEntryType }
     */
    public PicklistEntryType createPicklistEntryType() {
        return new PicklistEntryType();
    }

    /**
     * Create an instance of {@link AddressLineType }
     * 
     * @return
     *     the new instance of {@link AddressLineType }
     */
    public AddressLineType createAddressLineType() {
        return new AddressLineType();
    }

    /**
     * Create an instance of {@link DataplusGroupType }
     * 
     * @return
     *     the new instance of {@link DataplusGroupType }
     */
    public DataplusGroupType createDataplusGroupType() {
        return new DataplusGroupType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UsernameTokenType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UsernameTokenType }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", name = "UsernameToken")
    public JAXBElement<UsernameTokenType> createUsernameToken(UsernameTokenType value) {
        return new JAXBElement<>(_UsernameToken_QNAME, UsernameTokenType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySecurityTokenType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BinarySecurityTokenType }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", name = "BinarySecurityToken")
    public JAXBElement<BinarySecurityTokenType> createBinarySecurityToken(BinarySecurityTokenType value) {
        return new JAXBElement<>(_BinarySecurityToken_QNAME, BinarySecurityTokenType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", name = "Reference")
    public JAXBElement<ReferenceType> createReference(ReferenceType value) {
        return new JAXBElement<>(_Reference_QNAME, ReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmbeddedType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EmbeddedType }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", name = "Embedded")
    public JAXBElement<EmbeddedType> createEmbedded(EmbeddedType value) {
        return new JAXBElement<>(_Embedded_QNAME, EmbeddedType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeyIdentifierType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link KeyIdentifierType }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", name = "KeyIdentifier")
    public JAXBElement<KeyIdentifierType> createKeyIdentifier(KeyIdentifierType value) {
        return new JAXBElement<>(_KeyIdentifier_QNAME, KeyIdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SecurityTokenReferenceType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SecurityTokenReferenceType }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", name = "SecurityTokenReference")
    public JAXBElement<SecurityTokenReferenceType> createSecurityTokenReference(SecurityTokenReferenceType value) {
        return new JAXBElement<>(_SecurityTokenReference_QNAME, SecurityTokenReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SecurityHeaderType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SecurityHeaderType }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", name = "Security")
    public JAXBElement<SecurityHeaderType> createSecurity(SecurityHeaderType value) {
        return new JAXBElement<>(_Security_QNAME, SecurityHeaderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransformationParametersType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TransformationParametersType }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", name = "TransformationParameters")
    public JAXBElement<TransformationParametersType> createTransformationParameters(TransformationParametersType value) {
        return new JAXBElement<>(_TransformationParameters_QNAME, TransformationParametersType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PasswordString }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PasswordString }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", name = "Password")
    public JAXBElement<PasswordString> createPassword(PasswordString value) {
        return new JAXBElement<>(_Password_QNAME, PasswordString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EncodedString }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EncodedString }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", name = "Nonce")
    public JAXBElement<EncodedString> createNonce(EncodedString value) {
        return new JAXBElement<>(_Nonce_QNAME, EncodedString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimestampType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TimestampType }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", name = "Timestamp")
    public JAXBElement<TimestampType> createTimestamp(TimestampType value) {
        return new JAXBElement<>(_Timestamp_QNAME, TimestampType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedDateTime }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AttributedDateTime }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", name = "Expires")
    public JAXBElement<AttributedDateTime> createExpires(AttributedDateTime value) {
        return new JAXBElement<>(_Expires_QNAME, AttributedDateTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedDateTime }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AttributedDateTime }{@code >}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", name = "Created")
    public JAXBElement<AttributedDateTime> createCreated(AttributedDateTime value) {
        return new JAXBElement<>(_Created_QNAME, AttributedDateTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QAAuthentication }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link QAAuthentication }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.qas.com/OnDemand-2011-03", name = "QAAuthentication")
    public JAXBElement<QAAuthentication> createQAAuthentication(QAAuthentication value) {
        return new JAXBElement<>(_QAAuthentication_QNAME, QAAuthentication.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QAQueryHeader }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link QAQueryHeader }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.qas.com/OnDemand-2011-03", name = "QAQueryHeader")
    public JAXBElement<QAQueryHeader> createQAQueryHeader(QAQueryHeader value) {
        return new JAXBElement<>(_QAQueryHeader_QNAME, QAQueryHeader.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QAInformation }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link QAInformation }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.qas.com/OnDemand-2011-03", name = "QAInformation")
    public JAXBElement<QAInformation> createQAInformation(QAInformation value) {
        return new JAXBElement<>(_QAInformation_QNAME, QAInformation.class, null, value);
    }

}

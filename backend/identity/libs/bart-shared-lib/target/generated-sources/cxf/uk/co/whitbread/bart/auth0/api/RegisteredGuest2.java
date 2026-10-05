
package uk.co.whitbread.bart.auth0.api;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for RegisteredGuest2 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RegisteredGuest2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="address" type="{http://bartws.micros.com/1.13}Address2"/&gt;
 *         &lt;element name="addressType"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="HOME"/&gt;
 *               &lt;enumeration value="BUSINESS"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="companyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="companyID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="existingPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="businessUse" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="emailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="electronicInvoiceRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="guestHistoryNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="guestHistoryCreated" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="telephoneNumbers" type="{http://bartws.micros.com/1.13}ArrayOfTelephoneRegisteredTelephone2" minOccurs="0"/&gt;
 *         &lt;element name="registeredGuestPassword"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="8"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="registeredPaymentCard" type="{http://bartws.micros.com/1.13}RegisteredPaymentCard2"/&gt;
 *         &lt;element name="registeredGuestPreferences" type="{http://bartws.micros.com/1.13}ArrayOfRegisteredGuestPreference" minOccurs="0"/&gt;
 *         &lt;element name="ivrRegistered" type="{http://bartws.micros.com/1.13}ivrRegistered2" minOccurs="0"/&gt;
 *         &lt;element name="receiveFutureMailings" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="additionalGuestNames" type="{http://bartws.micros.com/1.13}ArrayOfAdditionalGuestNameAdditionalGuestNames2" minOccurs="0"/&gt;
 *         &lt;element name="loyaltyStatuses" type="{http://bartws.micros.com/1.13}ArrayOfLoyaltyStatus" minOccurs="0"/&gt;
 *         &lt;element name="carRegistration" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="nationality" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="passport" type="{http://bartws.micros.com/1.13}Passport2" minOccurs="0"/&gt;
 *         &lt;element name="totalStays" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisteredGuest2", propOrder = {
    "title",
    "firstName",
    "lastName",
    "address",
    "addressType",
    "companyName",
    "companyID",
    "existingPassword",
    "businessUse",
    "emailAddress",
    "electronicInvoiceRequired",
    "guestHistoryNumber",
    "guestHistoryCreated",
    "telephoneNumbers",
    "registeredGuestPassword",
    "registeredPaymentCard",
    "registeredGuestPreferences",
    "ivrRegistered",
    "receiveFutureMailings",
    "additionalGuestNames",
    "loyaltyStatuses",
    "carRegistration",
    "nationality",
    "passport",
    "totalStays"
})
public class RegisteredGuest2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String title;
    @XmlElement(required = true)
    protected String firstName;
    @XmlElement(required = true)
    protected String lastName;
    @XmlElement(required = true)
    protected Address2 address;
    @XmlElement(required = true)
    protected String addressType;
    protected String companyName;
    protected String companyID;
    protected String existingPassword;
    protected Boolean businessUse;
    @XmlElement(required = true)
    protected String emailAddress;
    protected Boolean electronicInvoiceRequired;
    protected String guestHistoryNumber;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate guestHistoryCreated;
    protected ArrayOfTelephoneRegisteredTelephone2 telephoneNumbers;
    @XmlElement(required = true)
    protected String registeredGuestPassword;
    @XmlElement(required = true)
    protected RegisteredPaymentCard2 registeredPaymentCard;
    protected ArrayOfRegisteredGuestPreference registeredGuestPreferences;
    protected IvrRegistered2 ivrRegistered;
    protected boolean receiveFutureMailings;
    protected ArrayOfAdditionalGuestNameAdditionalGuestNames2 additionalGuestNames;
    protected ArrayOfLoyaltyStatus loyaltyStatuses;
    protected String carRegistration;
    protected String nationality;
    protected Passport2 passport;
    protected Long totalStays;

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link Address2 }
     *     
     */
    public Address2 getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address2 }
     *     
     */
    public void setAddress(Address2 value) {
        this.address = value;
    }

    /**
     * Gets the value of the addressType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressType() {
        return addressType;
    }

    /**
     * Sets the value of the addressType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressType(String value) {
        this.addressType = value;
    }

    /**
     * Gets the value of the companyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the value of the companyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyName(String value) {
        this.companyName = value;
    }

    /**
     * Gets the value of the companyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyID() {
        return companyID;
    }

    /**
     * Sets the value of the companyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyID(String value) {
        this.companyID = value;
    }

    /**
     * Gets the value of the existingPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExistingPassword() {
        return existingPassword;
    }

    /**
     * Sets the value of the existingPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExistingPassword(String value) {
        this.existingPassword = value;
    }

    /**
     * Gets the value of the businessUse property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBusinessUse() {
        return businessUse;
    }

    /**
     * Sets the value of the businessUse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBusinessUse(Boolean value) {
        this.businessUse = value;
    }

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the electronicInvoiceRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isElectronicInvoiceRequired() {
        return electronicInvoiceRequired;
    }

    /**
     * Sets the value of the electronicInvoiceRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setElectronicInvoiceRequired(Boolean value) {
        this.electronicInvoiceRequired = value;
    }

    /**
     * Gets the value of the guestHistoryNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuestHistoryNumber() {
        return guestHistoryNumber;
    }

    /**
     * Sets the value of the guestHistoryNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuestHistoryNumber(String value) {
        this.guestHistoryNumber = value;
    }

    /**
     * Gets the value of the guestHistoryCreated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getGuestHistoryCreated() {
        return guestHistoryCreated;
    }

    /**
     * Sets the value of the guestHistoryCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuestHistoryCreated(LocalDate value) {
        this.guestHistoryCreated = value;
    }

    /**
     * Gets the value of the telephoneNumbers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTelephoneRegisteredTelephone2 }
     *     
     */
    public ArrayOfTelephoneRegisteredTelephone2 getTelephoneNumbers() {
        return telephoneNumbers;
    }

    /**
     * Sets the value of the telephoneNumbers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTelephoneRegisteredTelephone2 }
     *     
     */
    public void setTelephoneNumbers(ArrayOfTelephoneRegisteredTelephone2 value) {
        this.telephoneNumbers = value;
    }

    /**
     * Gets the value of the registeredGuestPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegisteredGuestPassword() {
        return registeredGuestPassword;
    }

    /**
     * Sets the value of the registeredGuestPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegisteredGuestPassword(String value) {
        this.registeredGuestPassword = value;
    }

    /**
     * Gets the value of the registeredPaymentCard property.
     * 
     * @return
     *     possible object is
     *     {@link RegisteredPaymentCard2 }
     *     
     */
    public RegisteredPaymentCard2 getRegisteredPaymentCard() {
        return registeredPaymentCard;
    }

    /**
     * Sets the value of the registeredPaymentCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegisteredPaymentCard2 }
     *     
     */
    public void setRegisteredPaymentCard(RegisteredPaymentCard2 value) {
        this.registeredPaymentCard = value;
    }

    /**
     * Gets the value of the registeredGuestPreferences property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRegisteredGuestPreference }
     *     
     */
    public ArrayOfRegisteredGuestPreference getRegisteredGuestPreferences() {
        return registeredGuestPreferences;
    }

    /**
     * Sets the value of the registeredGuestPreferences property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRegisteredGuestPreference }
     *     
     */
    public void setRegisteredGuestPreferences(ArrayOfRegisteredGuestPreference value) {
        this.registeredGuestPreferences = value;
    }

    /**
     * Gets the value of the ivrRegistered property.
     * 
     * @return
     *     possible object is
     *     {@link IvrRegistered2 }
     *     
     */
    public IvrRegistered2 getIvrRegistered() {
        return ivrRegistered;
    }

    /**
     * Sets the value of the ivrRegistered property.
     * 
     * @param value
     *     allowed object is
     *     {@link IvrRegistered2 }
     *     
     */
    public void setIvrRegistered(IvrRegistered2 value) {
        this.ivrRegistered = value;
    }

    /**
     * Gets the value of the receiveFutureMailings property.
     * 
     */
    public boolean isReceiveFutureMailings() {
        return receiveFutureMailings;
    }

    /**
     * Sets the value of the receiveFutureMailings property.
     * 
     */
    public void setReceiveFutureMailings(boolean value) {
        this.receiveFutureMailings = value;
    }

    /**
     * Gets the value of the additionalGuestNames property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAdditionalGuestNameAdditionalGuestNames2 }
     *     
     */
    public ArrayOfAdditionalGuestNameAdditionalGuestNames2 getAdditionalGuestNames() {
        return additionalGuestNames;
    }

    /**
     * Sets the value of the additionalGuestNames property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAdditionalGuestNameAdditionalGuestNames2 }
     *     
     */
    public void setAdditionalGuestNames(ArrayOfAdditionalGuestNameAdditionalGuestNames2 value) {
        this.additionalGuestNames = value;
    }

    /**
     * Gets the value of the loyaltyStatuses property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLoyaltyStatus }
     *     
     */
    public ArrayOfLoyaltyStatus getLoyaltyStatuses() {
        return loyaltyStatuses;
    }

    /**
     * Sets the value of the loyaltyStatuses property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLoyaltyStatus }
     *     
     */
    public void setLoyaltyStatuses(ArrayOfLoyaltyStatus value) {
        this.loyaltyStatuses = value;
    }

    /**
     * Gets the value of the carRegistration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarRegistration() {
        return carRegistration;
    }

    /**
     * Sets the value of the carRegistration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarRegistration(String value) {
        this.carRegistration = value;
    }

    /**
     * Gets the value of the nationality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * Sets the value of the nationality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNationality(String value) {
        this.nationality = value;
    }

    /**
     * Gets the value of the passport property.
     * 
     * @return
     *     possible object is
     *     {@link Passport2 }
     *     
     */
    public Passport2 getPassport() {
        return passport;
    }

    /**
     * Sets the value of the passport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Passport2 }
     *     
     */
    public void setPassport(Passport2 value) {
        this.passport = value;
    }

    /**
     * Gets the value of the totalStays property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotalStays() {
        return totalStays;
    }

    /**
     * Sets the value of the totalStays property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotalStays(Long value) {
        this.totalStays = value;
    }

}


package uk.co.whitbread.bart.registeredguest.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CreateRegisteredGuestResponse1 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CreateRegisteredGuestResponse1"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="registrationSuccess" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="createRegisteredGuestError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
 *               &lt;enumeration value="INVALID_NUMBER_OF_DIGITS_IN_CREDIT_CARD_NUMBER"/&gt;
 *               &lt;enumeration value="INVALID_LUHN_CHECK_DIGIT"/&gt;
 *               &lt;enumeration value="EMAIL_ALREADY_REGISTERED"/&gt;
 *               &lt;enumeration value="TELEPHONE_NUMBER_ALREADY_REGISTERED"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.13}ErrorDetails" minOccurs="0"/&gt;
 *         &lt;element name="regularGuestErrors" type="{http://bartws.micros.com/1.13}ArrayOfRegularGuestUpdateErrorRegularGuestUpdateError" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateRegisteredGuestResponse1", propOrder = {
    "registrationSuccess",
    "sessionID",
    "createRegisteredGuestError",
    "errorDetail",
    "regularGuestErrors"
})
public class CreateRegisteredGuestResponse1
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected boolean registrationSuccess;
    @XmlElement(required = true)
    protected String sessionID;
    protected String createRegisteredGuestError;
    protected ErrorDetails errorDetail;
    protected ArrayOfRegularGuestUpdateErrorRegularGuestUpdateError regularGuestErrors;

    /**
     * Gets the value of the registrationSuccess property.
     * 
     */
    public boolean isRegistrationSuccess() {
        return registrationSuccess;
    }

    /**
     * Sets the value of the registrationSuccess property.
     * 
     */
    public void setRegistrationSuccess(boolean value) {
        this.registrationSuccess = value;
    }

    /**
     * Gets the value of the sessionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Sets the value of the sessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionID(String value) {
        this.sessionID = value;
    }

    /**
     * Gets the value of the createRegisteredGuestError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateRegisteredGuestError() {
        return createRegisteredGuestError;
    }

    /**
     * Sets the value of the createRegisteredGuestError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateRegisteredGuestError(String value) {
        this.createRegisteredGuestError = value;
    }

    /**
     * Gets the value of the errorDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorDetails }
     *     
     */
    public ErrorDetails getErrorDetail() {
        return errorDetail;
    }

    /**
     * Sets the value of the errorDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorDetails }
     *     
     */
    public void setErrorDetail(ErrorDetails value) {
        this.errorDetail = value;
    }

    /**
     * Gets the value of the regularGuestErrors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRegularGuestUpdateErrorRegularGuestUpdateError }
     *     
     */
    public ArrayOfRegularGuestUpdateErrorRegularGuestUpdateError getRegularGuestErrors() {
        return regularGuestErrors;
    }

    /**
     * Sets the value of the regularGuestErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRegularGuestUpdateErrorRegularGuestUpdateError }
     *     
     */
    public void setRegularGuestErrors(ArrayOfRegularGuestUpdateErrorRegularGuestUpdateError value) {
        this.regularGuestErrors = value;
    }

}

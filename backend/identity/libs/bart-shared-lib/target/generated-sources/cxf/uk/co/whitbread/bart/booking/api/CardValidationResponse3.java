
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CardValidationResponse3 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CardValidationResponse3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="redirectURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="redirectHTML" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="threeDSecureRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="PAReq" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="99999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cardValidationError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="VALIDATION_NOT_REQUIRED"/&gt;
 *               &lt;enumeration value="USE_THREEDSECURE_TRANSACTION_REQUEST"/&gt;
 *               &lt;enumeration value="INVALID_NUMBER_OF_DIGITS_IN_CREDIT_CARD_NUMBER"/&gt;
 *               &lt;enumeration value="INVALID_LUHN_CHECK_DIGIT"/&gt;
 *               &lt;enumeration value="CARD_TYPE_NOT_ACCEPTED_AT_PROPERTY"/&gt;
 *               &lt;enumeration value="CARD_NOT_VALID_ON_DAY_OF_ARRIVAL"/&gt;
 *               &lt;enumeration value="CARD_NOT_VALID_ON_DAY_OF_DEPARTURE"/&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *               &lt;enumeration value="PREPAYMENT_NOT_ALLOWED_IN_HOTEL"/&gt;
 *               &lt;enumeration value="PREPAYMENT_NOT_ALLOWED_FOR_THIS_CARD_TYPE"/&gt;
 *               &lt;enumeration value="THREE_D_SECURE_NOT_REQUIRED_FOR_THIS_CARD"/&gt;
 *               &lt;enumeration value="THREE_D_SECURE_NOT_CURRENTLY_AVAILABLE"/&gt;
 *               &lt;enumeration value="NO_REGISTERED_CARD_ON_FILE"/&gt;
 *               &lt;enumeration value="GUEST_HISTORY_NOT_FOUND"/&gt;
 *               &lt;enumeration value="PREPAYMENT_NOT_REQUIRED"/&gt;
 *               &lt;enumeration value="USE_EXISTING_CARD"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.31}ErrorDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardValidationResponse3", propOrder = {
    "sessionID",
    "redirectURL",
    "redirectHTML",
    "threeDSecureRequired",
    "paReq",
    "cardValidationError",
    "errorDetail"
})
public class CardValidationResponse3
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String sessionID;
    protected String redirectURL;
    protected String redirectHTML;
    protected Boolean threeDSecureRequired;
    @XmlElement(name = "PAReq")
    protected String paReq;
    protected String cardValidationError;
    protected ErrorDetails errorDetail;

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
     * Gets the value of the redirectURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRedirectURL() {
        return redirectURL;
    }

    /**
     * Sets the value of the redirectURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRedirectURL(String value) {
        this.redirectURL = value;
    }

    /**
     * Gets the value of the redirectHTML property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRedirectHTML() {
        return redirectHTML;
    }

    /**
     * Sets the value of the redirectHTML property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRedirectHTML(String value) {
        this.redirectHTML = value;
    }

    /**
     * Gets the value of the threeDSecureRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isThreeDSecureRequired() {
        return threeDSecureRequired;
    }

    /**
     * Sets the value of the threeDSecureRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setThreeDSecureRequired(Boolean value) {
        this.threeDSecureRequired = value;
    }

    /**
     * Gets the value of the paReq property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPAReq() {
        return paReq;
    }

    /**
     * Sets the value of the paReq property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPAReq(String value) {
        this.paReq = value;
    }

    /**
     * Gets the value of the cardValidationError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardValidationError() {
        return cardValidationError;
    }

    /**
     * Sets the value of the cardValidationError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardValidationError(String value) {
        this.cardValidationError = value;
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

}


package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ThreeDSecureTransactionResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ThreeDSecureTransactionResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="redirectURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="threeDSecureTransactionError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="INVALID_NUMBER_OF_DIGITS_IN_CREDIT_CARD_NUMBER"/&gt;
 *               &lt;enumeration value="USE_CARD_VALIDATION_REQUEST"/&gt;
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
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="transactionErrorDescription" type="{http://bartws.micros.com/1.31}ErrorDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ThreeDSecureTransactionResponse", propOrder = {
    "sessionID",
    "redirectURL",
    "threeDSecureTransactionError",
    "transactionErrorDescription"
})
public class ThreeDSecureTransactionResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String sessionID;
    protected String redirectURL;
    protected String threeDSecureTransactionError;
    protected ErrorDetails transactionErrorDescription;

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
     * Gets the value of the threeDSecureTransactionError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThreeDSecureTransactionError() {
        return threeDSecureTransactionError;
    }

    /**
     * Sets the value of the threeDSecureTransactionError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThreeDSecureTransactionError(String value) {
        this.threeDSecureTransactionError = value;
    }

    /**
     * Gets the value of the transactionErrorDescription property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorDetails }
     *     
     */
    public ErrorDetails getTransactionErrorDescription() {
        return transactionErrorDescription;
    }

    /**
     * Sets the value of the transactionErrorDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorDetails }
     *     
     */
    public void setTransactionErrorDescription(ErrorDetails value) {
        this.transactionErrorDescription = value;
    }

}


package uk.co.whitbread.bart.checkin.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ThreeDSecureResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ThreeDSecureResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="checkInComplete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="checkInReadback" type="{http://bartws.micros.com/1.0}Readback" minOccurs="0"/&gt;
 *         &lt;element name="threeDSecureError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="PREPAYMENT_FAILED_SYSTEM"/&gt;
 *               &lt;enumeration value="PREPAYMENT_FAILED_DECLINE"/&gt;
 *               &lt;enumeration value="FRAUD_CHECK_FAILED"/&gt;
 *               &lt;enumeration value="MISSING_PARES"/&gt;
 *               &lt;enumeration value="SITE_END_OF_DAY_IN_PROGRESS"/&gt;
 *               &lt;enumeration value="INVALID_MESSAGE_SEQUENCE"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="errorDetails" type="{http://bartws.micros.com/1.0}ErrorDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ThreeDSecureResponse", propOrder = {
    "sessionID",
    "checkInComplete",
    "checkInReadback",
    "threeDSecureError",
    "errorDetails"
})
public class ThreeDSecureResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String sessionID;
    protected Boolean checkInComplete;
    protected Readback checkInReadback;
    protected String threeDSecureError;
    protected ErrorDetails errorDetails;

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
     * Gets the value of the checkInComplete property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCheckInComplete() {
        return checkInComplete;
    }

    /**
     * Sets the value of the checkInComplete property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCheckInComplete(Boolean value) {
        this.checkInComplete = value;
    }

    /**
     * Gets the value of the checkInReadback property.
     * 
     * @return
     *     possible object is
     *     {@link Readback }
     *     
     */
    public Readback getCheckInReadback() {
        return checkInReadback;
    }

    /**
     * Sets the value of the checkInReadback property.
     * 
     * @param value
     *     allowed object is
     *     {@link Readback }
     *     
     */
    public void setCheckInReadback(Readback value) {
        this.checkInReadback = value;
    }

    /**
     * Gets the value of the threeDSecureError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThreeDSecureError() {
        return threeDSecureError;
    }

    /**
     * Sets the value of the threeDSecureError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThreeDSecureError(String value) {
        this.threeDSecureError = value;
    }

    /**
     * Gets the value of the errorDetails property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorDetails }
     *     
     */
    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }

    /**
     * Sets the value of the errorDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorDetails }
     *     
     */
    public void setErrorDetails(ErrorDetails value) {
        this.errorDetails = value;
    }

}

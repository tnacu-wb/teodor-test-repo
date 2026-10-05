
package uk.co.whitbread.bart.checkbookingconfirmation.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CheckBookingConfirmationResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CheckBookingConfirmationResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="confirmationSent" type="{http://bartws.micros.com/1.4}ArrayOfconfirmationConfirmationSent" minOccurs="0"/&gt;
 *         &lt;element name="checkBookingConfirmationError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *               &lt;enumeration value="INVALID_EMAIL_ADDRESS"/&gt;
 *               &lt;enumeration value="INVALID_MOBILE_NUMBER"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.4}ErrorDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CheckBookingConfirmationResponse", propOrder = {
    "sessionID",
    "confirmationSent",
    "checkBookingConfirmationError",
    "errorDetail"
})
public class CheckBookingConfirmationResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    protected ArrayOfconfirmationConfirmationSent confirmationSent;
    protected String checkBookingConfirmationError;
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
     * Gets the value of the confirmationSent property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfconfirmationConfirmationSent }
     *     
     */
    public ArrayOfconfirmationConfirmationSent getConfirmationSent() {
        return confirmationSent;
    }

    /**
     * Sets the value of the confirmationSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfconfirmationConfirmationSent }
     *     
     */
    public void setConfirmationSent(ArrayOfconfirmationConfirmationSent value) {
        this.confirmationSent = value;
    }

    /**
     * Gets the value of the checkBookingConfirmationError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckBookingConfirmationError() {
        return checkBookingConfirmationError;
    }

    /**
     * Sets the value of the checkBookingConfirmationError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckBookingConfirmationError(String value) {
        this.checkBookingConfirmationError = value;
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

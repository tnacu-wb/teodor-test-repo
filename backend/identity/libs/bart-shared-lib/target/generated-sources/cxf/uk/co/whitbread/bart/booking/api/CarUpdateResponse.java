
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CarUpdateResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CarUpdateResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="carUpdateSuccessful" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="carUpdateError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="INVALID_CONFIRMATION_NUMBER"/&gt;
 *               &lt;enumeration value="INVALID_ROOM_ID"/&gt;
 *               &lt;enumeration value="RESERVATION_CANCELLED"/&gt;
 *               &lt;enumeration value="RESERATION_RELEASED"/&gt;
 *               &lt;enumeration value="RESERVATION_LOCKED"/&gt;
 *               &lt;enumeration value="RESERVATION_NOT_SLEEP_PARK_FLY"/&gt;
 *               &lt;enumeration value="MANDATORY_FIELD_MISSING"/&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
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
@XmlType(name = "CarUpdateResponse", propOrder = {
    "sessionID",
    "carUpdateSuccessful",
    "carUpdateError",
    "errorDetail"
})
public class CarUpdateResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    protected Boolean carUpdateSuccessful;
    protected String carUpdateError;
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
     * Gets the value of the carUpdateSuccessful property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCarUpdateSuccessful() {
        return carUpdateSuccessful;
    }

    /**
     * Sets the value of the carUpdateSuccessful property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCarUpdateSuccessful(Boolean value) {
        this.carUpdateSuccessful = value;
    }

    /**
     * Gets the value of the carUpdateError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarUpdateError() {
        return carUpdateError;
    }

    /**
     * Sets the value of the carUpdateError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarUpdateError(String value) {
        this.carUpdateError = value;
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

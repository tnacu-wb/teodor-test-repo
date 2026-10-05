
package uk.co.whitbread.bart.registeredguest.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RegisteredGuestLoginResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RegisteredGuestLoginResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ezineSubscribed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="guestDetails" type="{http://bartws.micros.com/1.13}RegisteredGuest"/&gt;
 *         &lt;element name="inHouse" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="nextArrival" type="{http://bartws.micros.com/1.13}NextArrival" minOccurs="0"/&gt;
 *         &lt;element name="loginRegisteredGuestError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
 *               &lt;enumeration value="EMAIL_ADDRESS_NOT_REGISTERED"/&gt;
 *               &lt;enumeration value="TELEPHONE_NUMBER_NOT_REGISTERED"/&gt;
 *               &lt;enumeration value="INVALID_GUEST_LOGIN"/&gt;
 *               &lt;enumeration value="ACCOUNT_LOCKED"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.13}ErrorDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisteredGuestLoginResponse", propOrder = {
    "sessionID",
    "ezineSubscribed",
    "guestDetails",
    "inHouse",
    "nextArrival",
    "loginRegisteredGuestError",
    "errorDetail"
})
public class RegisteredGuestLoginResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    protected Boolean ezineSubscribed;
    @XmlElement(required = true)
    protected RegisteredGuest guestDetails;
    protected Boolean inHouse;
    protected NextArrival nextArrival;
    protected String loginRegisteredGuestError;
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
     * Gets the value of the ezineSubscribed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEzineSubscribed() {
        return ezineSubscribed;
    }

    /**
     * Sets the value of the ezineSubscribed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEzineSubscribed(Boolean value) {
        this.ezineSubscribed = value;
    }

    /**
     * Gets the value of the guestDetails property.
     * 
     * @return
     *     possible object is
     *     {@link RegisteredGuest }
     *     
     */
    public RegisteredGuest getGuestDetails() {
        return guestDetails;
    }

    /**
     * Sets the value of the guestDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegisteredGuest }
     *     
     */
    public void setGuestDetails(RegisteredGuest value) {
        this.guestDetails = value;
    }

    /**
     * Gets the value of the inHouse property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInHouse() {
        return inHouse;
    }

    /**
     * Sets the value of the inHouse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInHouse(Boolean value) {
        this.inHouse = value;
    }

    /**
     * Gets the value of the nextArrival property.
     * 
     * @return
     *     possible object is
     *     {@link NextArrival }
     *     
     */
    public NextArrival getNextArrival() {
        return nextArrival;
    }

    /**
     * Sets the value of the nextArrival property.
     * 
     * @param value
     *     allowed object is
     *     {@link NextArrival }
     *     
     */
    public void setNextArrival(NextArrival value) {
        this.nextArrival = value;
    }

    /**
     * Gets the value of the loginRegisteredGuestError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoginRegisteredGuestError() {
        return loginRegisteredGuestError;
    }

    /**
     * Sets the value of the loginRegisteredGuestError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoginRegisteredGuestError(String value) {
        this.loginRegisteredGuestError = value;
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

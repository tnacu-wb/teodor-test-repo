
package uk.co.whitbread.bart.auth0.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for InitSessionResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="InitSessionResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ezineSubscribed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="guestDetails" type="{http://bartws.micros.com/1.13}RegisteredGuest2"/&gt;
 *         &lt;element name="inHouse" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="nextArrival" type="{http://bartws.micros.com/1.13}NextArrival2" minOccurs="0"/&gt;
 *         &lt;element name="initError" minOccurs="0"&gt;
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
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.13}ErrorDetails2" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InitSessionResponse", propOrder = {
    "sessionID",
    "ezineSubscribed",
    "guestDetails",
    "inHouse",
    "nextArrival",
    "initError",
    "errorDetail"
})
public class InitSessionResponse2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    protected Boolean ezineSubscribed;
    @XmlElement(required = true)
    protected RegisteredGuest2 guestDetails;
    protected Boolean inHouse;
    protected NextArrival2 nextArrival;
    protected String initError;
    protected ErrorDetails2 errorDetail;

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
     *     {@link RegisteredGuest2 }
     *     
     */
    public RegisteredGuest2 getGuestDetails() {
        return guestDetails;
    }

    /**
     * Sets the value of the guestDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegisteredGuest2 }
     *     
     */
    public void setGuestDetails(RegisteredGuest2 value) {
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
     *     {@link NextArrival2 }
     *     
     */
    public NextArrival2 getNextArrival() {
        return nextArrival;
    }

    /**
     * Sets the value of the nextArrival property.
     * 
     * @param value
     *     allowed object is
     *     {@link NextArrival2 }
     *     
     */
    public void setNextArrival(NextArrival2 value) {
        this.nextArrival = value;
    }

    /**
     * Gets the value of the initError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitError() {
        return initError;
    }

    /**
     * Sets the value of the initError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitError(String value) {
        this.initError = value;
    }

    /**
     * Gets the value of the errorDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorDetails2 }
     *     
     */
    public ErrorDetails2 getErrorDetail() {
        return errorDetail;
    }

    /**
     * Sets the value of the errorDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorDetails2 }
     *     
     */
    public void setErrorDetail(ErrorDetails2 value) {
        this.errorDetail = value;
    }

}

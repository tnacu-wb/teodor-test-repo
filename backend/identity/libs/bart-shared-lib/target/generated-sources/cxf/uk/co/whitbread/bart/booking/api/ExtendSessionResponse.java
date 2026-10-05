
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ExtendSessionResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ExtendSessionResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sessionExtended" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="sessionExpirySeconds" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="extendSessionError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="SESSION_EXTENSION_NOT_ENABLED"/&gt;
 *               &lt;enumeration value=" MAXIMUM_EXTENSION_COUNT_REACHED"/&gt;
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
@XmlType(name = "ExtendSessionResponse", propOrder = {
    "sessionID",
    "sessionExtended",
    "sessionExpirySeconds",
    "extendSessionError",
    "errorDetail"
})
public class ExtendSessionResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    protected boolean sessionExtended;
    protected long sessionExpirySeconds;
    protected String extendSessionError;
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
     * Gets the value of the sessionExtended property.
     * 
     */
    public boolean isSessionExtended() {
        return sessionExtended;
    }

    /**
     * Sets the value of the sessionExtended property.
     * 
     */
    public void setSessionExtended(boolean value) {
        this.sessionExtended = value;
    }

    /**
     * Gets the value of the sessionExpirySeconds property.
     * 
     */
    public long getSessionExpirySeconds() {
        return sessionExpirySeconds;
    }

    /**
     * Sets the value of the sessionExpirySeconds property.
     * 
     */
    public void setSessionExpirySeconds(long value) {
        this.sessionExpirySeconds = value;
    }

    /**
     * Gets the value of the extendSessionError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtendSessionError() {
        return extendSessionError;
    }

    /**
     * Sets the value of the extendSessionError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtendSessionError(String value) {
        this.extendSessionError = value;
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


package uk.co.whitbread.bart.checkin.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for UpsellResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UpsellResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="upsellErrors" type="{http://bartws.micros.com/1.0}ArrayOfUpsellErrorUpsellError" minOccurs="0"/&gt;
 *         &lt;element name="upsellRequestSuccessful" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="checkInComplete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="cnpAuthRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="nextRequest" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="PaymentDetailsRequest"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="checkInReadback" type="{http://bartws.micros.com/1.0}Readback" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpsellResponse", propOrder = {
    "sessionID",
    "upsellErrors",
    "upsellRequestSuccessful",
    "checkInComplete",
    "cnpAuthRequired",
    "nextRequest",
    "checkInReadback"
})
public class UpsellResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String sessionID;
    protected ArrayOfUpsellErrorUpsellError upsellErrors;
    protected Boolean upsellRequestSuccessful;
    protected Boolean checkInComplete;
    protected Boolean cnpAuthRequired;
    protected String nextRequest;
    protected Readback checkInReadback;

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
     * Gets the value of the upsellErrors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUpsellErrorUpsellError }
     *     
     */
    public ArrayOfUpsellErrorUpsellError getUpsellErrors() {
        return upsellErrors;
    }

    /**
     * Sets the value of the upsellErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUpsellErrorUpsellError }
     *     
     */
    public void setUpsellErrors(ArrayOfUpsellErrorUpsellError value) {
        this.upsellErrors = value;
    }

    /**
     * Gets the value of the upsellRequestSuccessful property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUpsellRequestSuccessful() {
        return upsellRequestSuccessful;
    }

    /**
     * Sets the value of the upsellRequestSuccessful property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUpsellRequestSuccessful(Boolean value) {
        this.upsellRequestSuccessful = value;
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
     * Gets the value of the cnpAuthRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCnpAuthRequired() {
        return cnpAuthRequired;
    }

    /**
     * Sets the value of the cnpAuthRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCnpAuthRequired(Boolean value) {
        this.cnpAuthRequired = value;
    }

    /**
     * Gets the value of the nextRequest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNextRequest() {
        return nextRequest;
    }

    /**
     * Sets the value of the nextRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNextRequest(String value) {
        this.nextRequest = value;
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

}

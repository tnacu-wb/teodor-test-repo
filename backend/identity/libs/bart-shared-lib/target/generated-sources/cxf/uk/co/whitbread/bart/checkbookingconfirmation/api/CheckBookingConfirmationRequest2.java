
package uk.co.whitbread.bart.checkbookingconfirmation.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CheckBookingConfirmationRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CheckBookingConfirmationRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ConfirmationRequest" type="{http://bartws.micros.com/1.4}ArrayOfConfirmationConfirmation"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CheckBookingConfirmationRequest", propOrder = {
    "sessionID",
    "confirmationRequest"
})
public class CheckBookingConfirmationRequest2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(name = "ConfirmationRequest", required = true)
    protected ArrayOfConfirmationConfirmation confirmationRequest;

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
     * Gets the value of the confirmationRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfConfirmationConfirmation }
     *     
     */
    public ArrayOfConfirmationConfirmation getConfirmationRequest() {
        return confirmationRequest;
    }

    /**
     * Sets the value of the confirmationRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfConfirmationConfirmation }
     *     
     */
    public void setConfirmationRequest(ArrayOfConfirmationConfirmation value) {
        this.confirmationRequest = value;
    }

}

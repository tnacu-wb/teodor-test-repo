
package uk.co.whitbread.bart.registeredguest.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RegularGuestUpdateRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RegularGuestUpdateRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="additionalGuests" type="{http://bartws.micros.com/1.13}ArrayOfAdditionalGuestNameAdditionalGuestNames" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegularGuestUpdateRequest", propOrder = {
    "sessionID",
    "additionalGuests"
})
public class RegularGuestUpdateRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    protected ArrayOfAdditionalGuestNameAdditionalGuestNames additionalGuests;

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
     * Gets the value of the additionalGuests property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAdditionalGuestNameAdditionalGuestNames }
     *     
     */
    public ArrayOfAdditionalGuestNameAdditionalGuestNames getAdditionalGuests() {
        return additionalGuests;
    }

    /**
     * Sets the value of the additionalGuests property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAdditionalGuestNameAdditionalGuestNames }
     *     
     */
    public void setAdditionalGuests(ArrayOfAdditionalGuestNameAdditionalGuestNames value) {
        this.additionalGuests = value;
    }

}

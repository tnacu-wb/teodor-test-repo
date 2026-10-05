
package uk.co.whitbread.bart.checkin.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for UpsellRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UpsellRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="confirmationNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="upsellItems" type="{http://bartws.micros.com/1.0}ArrayOfUpsellItemsUpsellUnit" minOccurs="0"/&gt;
 *         &lt;element name="dinnerUpsell" type="{http://bartws.micros.com/1.0}ArrayOfdinnerDinnerUpsell" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpsellRequest", propOrder = {
    "sessionID",
    "confirmationNumber",
    "upsellItems",
    "dinnerUpsell"
})
public class UpsellRequest2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected String confirmationNumber;
    protected ArrayOfUpsellItemsUpsellUnit upsellItems;
    protected ArrayOfdinnerDinnerUpsell dinnerUpsell;

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
     * Gets the value of the confirmationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    /**
     * Sets the value of the confirmationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfirmationNumber(String value) {
        this.confirmationNumber = value;
    }

    /**
     * Gets the value of the upsellItems property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUpsellItemsUpsellUnit }
     *     
     */
    public ArrayOfUpsellItemsUpsellUnit getUpsellItems() {
        return upsellItems;
    }

    /**
     * Sets the value of the upsellItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUpsellItemsUpsellUnit }
     *     
     */
    public void setUpsellItems(ArrayOfUpsellItemsUpsellUnit value) {
        this.upsellItems = value;
    }

    /**
     * Gets the value of the dinnerUpsell property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfdinnerDinnerUpsell }
     *     
     */
    public ArrayOfdinnerDinnerUpsell getDinnerUpsell() {
        return dinnerUpsell;
    }

    /**
     * Sets the value of the dinnerUpsell property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfdinnerDinnerUpsell }
     *     
     */
    public void setDinnerUpsell(ArrayOfdinnerDinnerUpsell value) {
        this.dinnerUpsell = value;
    }

}


package uk.co.whitbread.bart.hub.inroomapp.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for UpsellCheckRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UpsellCheckRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="guestHistoryNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="roomNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="upsellItems" type="{http://hub.micros.com/1.0}ArrayOfupsellItemUpsellItem"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpsellCheckRequest", propOrder = {
    "sessionID",
    "guestHistoryNumber",
    "roomNumber",
    "upsellItems"
})
public class UpsellCheckRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected String guestHistoryNumber;
    @XmlElement(required = true)
    protected String roomNumber;
    @XmlElement(required = true)
    protected ArrayOfupsellItemUpsellItem upsellItems;

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
     * Gets the value of the guestHistoryNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuestHistoryNumber() {
        return guestHistoryNumber;
    }

    /**
     * Sets the value of the guestHistoryNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuestHistoryNumber(String value) {
        this.guestHistoryNumber = value;
    }

    /**
     * Gets the value of the roomNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomNumber() {
        return roomNumber;
    }

    /**
     * Sets the value of the roomNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomNumber(String value) {
        this.roomNumber = value;
    }

    /**
     * Gets the value of the upsellItems property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfupsellItemUpsellItem }
     *     
     */
    public ArrayOfupsellItemUpsellItem getUpsellItems() {
        return upsellItems;
    }

    /**
     * Sets the value of the upsellItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfupsellItemUpsellItem }
     *     
     */
    public void setUpsellItems(ArrayOfupsellItemUpsellItem value) {
        this.upsellItems = value;
    }

}

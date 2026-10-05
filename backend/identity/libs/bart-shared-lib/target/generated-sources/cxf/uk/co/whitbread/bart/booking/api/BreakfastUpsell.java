
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BreakfastUpsell complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BreakfastUpsell"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="roomNumber" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="breakfastUpsellCode" type="{http://bartws.micros.com/1.31}ArrayOfbreakfastBreakfastUpsellCode"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BreakfastUpsell", propOrder = {
    "roomNumber",
    "breakfastUpsellCode"
})
public class BreakfastUpsell
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected long roomNumber;
    @XmlElement(required = true)
    protected ArrayOfbreakfastBreakfastUpsellCode breakfastUpsellCode;

    /**
     * Gets the value of the roomNumber property.
     * 
     */
    public long getRoomNumber() {
        return roomNumber;
    }

    /**
     * Sets the value of the roomNumber property.
     * 
     */
    public void setRoomNumber(long value) {
        this.roomNumber = value;
    }

    /**
     * Gets the value of the breakfastUpsellCode property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfbreakfastBreakfastUpsellCode }
     *     
     */
    public ArrayOfbreakfastBreakfastUpsellCode getBreakfastUpsellCode() {
        return breakfastUpsellCode;
    }

    /**
     * Sets the value of the breakfastUpsellCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfbreakfastBreakfastUpsellCode }
     *     
     */
    public void setBreakfastUpsellCode(ArrayOfbreakfastBreakfastUpsellCode value) {
        this.breakfastUpsellCode = value;
    }

}

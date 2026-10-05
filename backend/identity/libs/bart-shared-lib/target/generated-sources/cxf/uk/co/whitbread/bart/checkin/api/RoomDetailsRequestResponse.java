
package uk.co.whitbread.bart.checkin.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RoomDetailsRequestResult" type="{http://bartws.micros.com/1.0}RoomDetailsResponse"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "roomDetailsRequestResult"
})
@XmlRootElement(name = "RoomDetailsRequestResponse")
public class RoomDetailsRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "RoomDetailsRequestResult", required = true)
    protected RoomDetailsResponse roomDetailsRequestResult;

    /**
     * Gets the value of the roomDetailsRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link RoomDetailsResponse }
     *     
     */
    public RoomDetailsResponse getRoomDetailsRequestResult() {
        return roomDetailsRequestResult;
    }

    /**
     * Sets the value of the roomDetailsRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoomDetailsResponse }
     *     
     */
    public void setRoomDetailsRequestResult(RoomDetailsResponse value) {
        this.roomDetailsRequestResult = value;
    }

}

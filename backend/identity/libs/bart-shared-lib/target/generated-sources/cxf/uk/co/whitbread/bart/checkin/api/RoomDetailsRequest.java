
package uk.co.whitbread.bart.checkin.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="roomDetailsRequest" type="{http://bartws.micros.com/1.0}RoomDetailsRequest" minOccurs="0"/&gt;
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
    "roomDetailsRequest"
})
@XmlRootElement(name = "RoomDetailsRequest")
public class RoomDetailsRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected RoomDetailsRequest2 roomDetailsRequest;

    /**
     * Gets the value of the roomDetailsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link RoomDetailsRequest2 }
     *     
     */
    public RoomDetailsRequest2 getRoomDetailsRequest() {
        return roomDetailsRequest;
    }

    /**
     * Sets the value of the roomDetailsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoomDetailsRequest2 }
     *     
     */
    public void setRoomDetailsRequest(RoomDetailsRequest2 value) {
        this.roomDetailsRequest = value;
    }

}

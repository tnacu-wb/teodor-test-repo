
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RoomsGrp complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="RoomsGrp">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="rateDialogue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="roomDetailsGroupArray" type="{https://dto.email.transact.comms.int.wtbapi.com}ArrayOfRoomDetailsGroup" minOccurs="0"/>
 *         <element name="totalRoomCost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="totalRooms" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoomsGrp", propOrder = {
    "rateDialogue",
    "roomDetailsGroupArray",
    "totalRoomCost",
    "totalRooms"
})
public class RoomsGrp
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected String rateDialogue;
    @XmlElement(nillable = true)
    protected ArrayOfRoomDetailsGroup roomDetailsGroupArray;
    @XmlElement(nillable = true)
    protected String totalRoomCost;
    protected Integer totalRooms;

    /**
     * Gets the value of the rateDialogue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateDialogue() {
        return rateDialogue;
    }

    /**
     * Sets the value of the rateDialogue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateDialogue(String value) {
        this.rateDialogue = value;
    }

    /**
     * Gets the value of the roomDetailsGroupArray property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRoomDetailsGroup }
     *     
     */
    public ArrayOfRoomDetailsGroup getRoomDetailsGroupArray() {
        return roomDetailsGroupArray;
    }

    /**
     * Sets the value of the roomDetailsGroupArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRoomDetailsGroup }
     *     
     */
    public void setRoomDetailsGroupArray(ArrayOfRoomDetailsGroup value) {
        this.roomDetailsGroupArray = value;
    }

    /**
     * Gets the value of the totalRoomCost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalRoomCost() {
        return totalRoomCost;
    }

    /**
     * Sets the value of the totalRoomCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalRoomCost(String value) {
        this.totalRoomCost = value;
    }

    /**
     * Gets the value of the totalRooms property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalRooms() {
        return totalRooms;
    }

    /**
     * Sets the value of the totalRooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalRooms(Integer value) {
        this.totalRooms = value;
    }

}

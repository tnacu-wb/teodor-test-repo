
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RoomDetailsGroup complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="RoomDetailsGroup">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="adults" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="babies" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="children" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="noGuaranteedRooms" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="noRooms" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="roomStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="roomType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="roomTypeCost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoomDetailsGroup", propOrder = {
    "adults",
    "babies",
    "children",
    "noGuaranteedRooms",
    "noRooms",
    "roomStatus",
    "roomType",
    "roomTypeCost"
})
public class RoomDetailsGroup
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected Integer adults;
    protected Integer babies;
    protected Integer children;
    protected Integer noGuaranteedRooms;
    protected Integer noRooms;
    @XmlElement(nillable = true)
    protected String roomStatus;
    @XmlElement(nillable = true)
    protected String roomType;
    @XmlElement(nillable = true)
    protected String roomTypeCost;

    /**
     * Gets the value of the adults property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAdults() {
        return adults;
    }

    /**
     * Sets the value of the adults property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAdults(Integer value) {
        this.adults = value;
    }

    /**
     * Gets the value of the babies property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBabies() {
        return babies;
    }

    /**
     * Sets the value of the babies property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBabies(Integer value) {
        this.babies = value;
    }

    /**
     * Gets the value of the children property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getChildren() {
        return children;
    }

    /**
     * Sets the value of the children property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setChildren(Integer value) {
        this.children = value;
    }

    /**
     * Gets the value of the noGuaranteedRooms property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNoGuaranteedRooms() {
        return noGuaranteedRooms;
    }

    /**
     * Sets the value of the noGuaranteedRooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNoGuaranteedRooms(Integer value) {
        this.noGuaranteedRooms = value;
    }

    /**
     * Gets the value of the noRooms property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNoRooms() {
        return noRooms;
    }

    /**
     * Sets the value of the noRooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNoRooms(Integer value) {
        this.noRooms = value;
    }

    /**
     * Gets the value of the roomStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomStatus() {
        return roomStatus;
    }

    /**
     * Sets the value of the roomStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomStatus(String value) {
        this.roomStatus = value;
    }

    /**
     * Gets the value of the roomType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomType() {
        return roomType;
    }

    /**
     * Sets the value of the roomType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomType(String value) {
        this.roomType = value;
    }

    /**
     * Gets the value of the roomTypeCost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomTypeCost() {
        return roomTypeCost;
    }

    /**
     * Sets the value of the roomTypeCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomTypeCost(String value) {
        this.roomTypeCost = value;
    }

}


package uk.co.whitbread.azure.email.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RoomsInd complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RoomsInd"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rateDialogue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="roomDetailsIndividualArray" type="{https://dto.email.transact.comms.int.wtbapi.com}ArrayOfRoomDetailsIndividual" minOccurs="0"/&gt;
 *         &lt;element name="totalRoomCost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="totalRooms" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoomsInd", propOrder = {
    "rateDialogue",
    "roomDetailsIndividualArray",
    "totalRoomCost",
    "totalRooms"
})
public class RoomsInd
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected String rateDialogue;
    @XmlElement(nillable = true)
    protected ArrayOfRoomDetailsIndividual roomDetailsIndividualArray;
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
     * Gets the value of the roomDetailsIndividualArray property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRoomDetailsIndividual }
     *     
     */
    public ArrayOfRoomDetailsIndividual getRoomDetailsIndividualArray() {
        return roomDetailsIndividualArray;
    }

    /**
     * Sets the value of the roomDetailsIndividualArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRoomDetailsIndividual }
     *     
     */
    public void setRoomDetailsIndividualArray(ArrayOfRoomDetailsIndividual value) {
        this.roomDetailsIndividualArray = value;
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

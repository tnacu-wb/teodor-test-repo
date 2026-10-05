
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookingPreferences complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingPreferences"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="numberOfAdults" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="numberOfChildren" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="numberOfInfants" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="roomType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="premierBreakfast" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="continentalBreakfast" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="mealDeal" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="preselectWifi" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="electronicInvoice" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookingPreferences", propOrder = {
    "numberOfAdults",
    "numberOfChildren",
    "numberOfInfants",
    "roomType",
    "premierBreakfast",
    "continentalBreakfast",
    "mealDeal",
    "preselectWifi",
    "electronicInvoice"
})
public class BookingPreferences
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected BigDecimal numberOfAdults;
    @XmlElement(required = true)
    protected BigDecimal numberOfChildren;
    @XmlElement(required = true)
    protected BigDecimal numberOfInfants;
    @XmlElement(required = true)
    protected String roomType;
    protected boolean premierBreakfast;
    protected boolean continentalBreakfast;
    protected boolean mealDeal;
    protected boolean preselectWifi;
    protected boolean electronicInvoice;

    /**
     * Gets the value of the numberOfAdults property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumberOfAdults() {
        return numberOfAdults;
    }

    /**
     * Sets the value of the numberOfAdults property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumberOfAdults(BigDecimal value) {
        this.numberOfAdults = value;
    }

    /**
     * Gets the value of the numberOfChildren property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumberOfChildren() {
        return numberOfChildren;
    }

    /**
     * Sets the value of the numberOfChildren property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumberOfChildren(BigDecimal value) {
        this.numberOfChildren = value;
    }

    /**
     * Gets the value of the numberOfInfants property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumberOfInfants() {
        return numberOfInfants;
    }

    /**
     * Sets the value of the numberOfInfants property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumberOfInfants(BigDecimal value) {
        this.numberOfInfants = value;
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
     * Gets the value of the premierBreakfast property.
     * 
     */
    public boolean isPremierBreakfast() {
        return premierBreakfast;
    }

    /**
     * Sets the value of the premierBreakfast property.
     * 
     */
    public void setPremierBreakfast(boolean value) {
        this.premierBreakfast = value;
    }

    /**
     * Gets the value of the continentalBreakfast property.
     * 
     */
    public boolean isContinentalBreakfast() {
        return continentalBreakfast;
    }

    /**
     * Sets the value of the continentalBreakfast property.
     * 
     */
    public void setContinentalBreakfast(boolean value) {
        this.continentalBreakfast = value;
    }

    /**
     * Gets the value of the mealDeal property.
     * 
     */
    public boolean isMealDeal() {
        return mealDeal;
    }

    /**
     * Sets the value of the mealDeal property.
     * 
     */
    public void setMealDeal(boolean value) {
        this.mealDeal = value;
    }

    /**
     * Gets the value of the preselectWifi property.
     * 
     */
    public boolean isPreselectWifi() {
        return preselectWifi;
    }

    /**
     * Sets the value of the preselectWifi property.
     * 
     */
    public void setPreselectWifi(boolean value) {
        this.preselectWifi = value;
    }

    /**
     * Gets the value of the electronicInvoice property.
     * 
     */
    public boolean isElectronicInvoice() {
        return electronicInvoice;
    }

    /**
     * Sets the value of the electronicInvoice property.
     * 
     */
    public void setElectronicInvoice(boolean value) {
        this.electronicInvoice = value;
    }

}

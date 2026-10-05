
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CNPAuthorisation complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CNPAuthorisation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="breakfastsAvailable" type="{http://bartws.micros.com/1.31}ArrayOfbreakfastBreakfast"/&gt;
 *         &lt;element name="dinnerAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="dinnerAvailableBA" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="dinnerAvailableNonBA" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="alcoholAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="carParkAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="wiFiAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="otherChargesAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CNPAuthorisation", propOrder = {
    "breakfastsAvailable",
    "dinnerAvailable",
    "dinnerAvailableBA",
    "dinnerAvailableNonBA",
    "alcoholAllowed",
    "carParkAvailable",
    "wiFiAvailable",
    "otherChargesAvailable"
})
public class CNPAuthorisation
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ArrayOfbreakfastBreakfast breakfastsAvailable;
    protected Boolean dinnerAvailable;
    protected Boolean dinnerAvailableBA;
    protected Boolean dinnerAvailableNonBA;
    protected Boolean alcoholAllowed;
    protected Boolean carParkAvailable;
    protected Boolean wiFiAvailable;
    protected Boolean otherChargesAvailable;

    /**
     * Gets the value of the breakfastsAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfbreakfastBreakfast }
     *     
     */
    public ArrayOfbreakfastBreakfast getBreakfastsAvailable() {
        return breakfastsAvailable;
    }

    /**
     * Sets the value of the breakfastsAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfbreakfastBreakfast }
     *     
     */
    public void setBreakfastsAvailable(ArrayOfbreakfastBreakfast value) {
        this.breakfastsAvailable = value;
    }

    /**
     * Gets the value of the dinnerAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDinnerAvailable() {
        return dinnerAvailable;
    }

    /**
     * Sets the value of the dinnerAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDinnerAvailable(Boolean value) {
        this.dinnerAvailable = value;
    }

    /**
     * Gets the value of the dinnerAvailableBA property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDinnerAvailableBA() {
        return dinnerAvailableBA;
    }

    /**
     * Sets the value of the dinnerAvailableBA property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDinnerAvailableBA(Boolean value) {
        this.dinnerAvailableBA = value;
    }

    /**
     * Gets the value of the dinnerAvailableNonBA property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDinnerAvailableNonBA() {
        return dinnerAvailableNonBA;
    }

    /**
     * Sets the value of the dinnerAvailableNonBA property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDinnerAvailableNonBA(Boolean value) {
        this.dinnerAvailableNonBA = value;
    }

    /**
     * Gets the value of the alcoholAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAlcoholAllowed() {
        return alcoholAllowed;
    }

    /**
     * Sets the value of the alcoholAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAlcoholAllowed(Boolean value) {
        this.alcoholAllowed = value;
    }

    /**
     * Gets the value of the carParkAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCarParkAvailable() {
        return carParkAvailable;
    }

    /**
     * Sets the value of the carParkAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCarParkAvailable(Boolean value) {
        this.carParkAvailable = value;
    }

    /**
     * Gets the value of the wiFiAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWiFiAvailable() {
        return wiFiAvailable;
    }

    /**
     * Sets the value of the wiFiAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWiFiAvailable(Boolean value) {
        this.wiFiAvailable = value;
    }

    /**
     * Gets the value of the otherChargesAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOtherChargesAvailable() {
        return otherChargesAvailable;
    }

    /**
     * Sets the value of the otherChargesAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOtherChargesAvailable(Boolean value) {
        this.otherChargesAvailable = value;
    }

}

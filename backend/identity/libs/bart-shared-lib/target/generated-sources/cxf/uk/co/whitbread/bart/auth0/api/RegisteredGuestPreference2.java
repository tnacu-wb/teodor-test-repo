
package uk.co.whitbread.bart.auth0.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RegisteredGuestPreference2 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RegisteredGuestPreference2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="roomType" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="FAM"/&gt;
 *               &lt;enumeration value="DB"/&gt;
 *               &lt;enumeration value="TWIN"/&gt;
 *               &lt;enumeration value="DIS"/&gt;
 *               &lt;enumeration value="SB"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="adults" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="children" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="cotRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="breakfastCode" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="prePay" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="preferredConfirmationMethod" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="EMAIL"/&gt;
 *               &lt;enumeration value="SMS"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="preferredLanguage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="hotelBrand" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisteredGuestPreference2", propOrder = {
    "roomType",
    "adults",
    "children",
    "cotRequired",
    "breakfastCode",
    "prePay",
    "preferredConfirmationMethod",
    "preferredLanguage"
})
public class RegisteredGuestPreference2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String roomType;
    protected Long adults;
    protected Long children;
    protected Boolean cotRequired;
    protected Long breakfastCode;
    protected Boolean prePay;
    protected String preferredConfirmationMethod;
    protected String preferredLanguage;
    @XmlAttribute(name = "hotelBrand")
    protected String hotelBrand;

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
     * Gets the value of the adults property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAdults() {
        return adults;
    }

    /**
     * Sets the value of the adults property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAdults(Long value) {
        this.adults = value;
    }

    /**
     * Gets the value of the children property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getChildren() {
        return children;
    }

    /**
     * Sets the value of the children property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setChildren(Long value) {
        this.children = value;
    }

    /**
     * Gets the value of the cotRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCotRequired() {
        return cotRequired;
    }

    /**
     * Sets the value of the cotRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCotRequired(Boolean value) {
        this.cotRequired = value;
    }

    /**
     * Gets the value of the breakfastCode property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBreakfastCode() {
        return breakfastCode;
    }

    /**
     * Sets the value of the breakfastCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBreakfastCode(Long value) {
        this.breakfastCode = value;
    }

    /**
     * Gets the value of the prePay property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPrePay() {
        return prePay;
    }

    /**
     * Sets the value of the prePay property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrePay(Boolean value) {
        this.prePay = value;
    }

    /**
     * Gets the value of the preferredConfirmationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreferredConfirmationMethod() {
        return preferredConfirmationMethod;
    }

    /**
     * Sets the value of the preferredConfirmationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreferredConfirmationMethod(String value) {
        this.preferredConfirmationMethod = value;
    }

    /**
     * Gets the value of the preferredLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     * Sets the value of the preferredLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreferredLanguage(String value) {
        this.preferredLanguage = value;
    }

    /**
     * Gets the value of the hotelBrand property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelBrand() {
        return hotelBrand;
    }

    /**
     * Sets the value of the hotelBrand property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelBrand(String value) {
        this.hotelBrand = value;
    }

}

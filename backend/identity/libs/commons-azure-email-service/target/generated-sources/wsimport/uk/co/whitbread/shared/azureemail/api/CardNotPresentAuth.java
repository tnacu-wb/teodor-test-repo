
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CardNotPresentAuth complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="CardNotPresentAuth">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="alcohol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="breakfast" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="carParking" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cnp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dinner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="other" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="wifi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardNotPresentAuth", propOrder = {
    "alcohol",
    "breakfast",
    "carParking",
    "cnp",
    "dinner",
    "other",
    "wifi"
})
public class CardNotPresentAuth
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected String alcohol;
    @XmlElement(nillable = true)
    protected String breakfast;
    @XmlElement(nillable = true)
    protected String carParking;
    @XmlElement(nillable = true)
    protected String cnp;
    @XmlElement(nillable = true)
    protected String dinner;
    @XmlElement(nillable = true)
    protected String other;
    @XmlElement(nillable = true)
    protected String wifi;

    /**
     * Gets the value of the alcohol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlcohol() {
        return alcohol;
    }

    /**
     * Sets the value of the alcohol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlcohol(String value) {
        this.alcohol = value;
    }

    /**
     * Gets the value of the breakfast property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBreakfast() {
        return breakfast;
    }

    /**
     * Sets the value of the breakfast property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBreakfast(String value) {
        this.breakfast = value;
    }

    /**
     * Gets the value of the carParking property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarParking() {
        return carParking;
    }

    /**
     * Sets the value of the carParking property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarParking(String value) {
        this.carParking = value;
    }

    /**
     * Gets the value of the cnp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCnp() {
        return cnp;
    }

    /**
     * Sets the value of the cnp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCnp(String value) {
        this.cnp = value;
    }

    /**
     * Gets the value of the dinner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDinner() {
        return dinner;
    }

    /**
     * Sets the value of the dinner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDinner(String value) {
        this.dinner = value;
    }

    /**
     * Gets the value of the other property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOther() {
        return other;
    }

    /**
     * Sets the value of the other property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOther(String value) {
        this.other = value;
    }

    /**
     * Gets the value of the wifi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWifi() {
        return wifi;
    }

    /**
     * Sets the value of the wifi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWifi(String value) {
        this.wifi = value;
    }

}


package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for HotelBreakfasts complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="HotelBreakfasts"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="breakfastCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="breakfastLegend" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="breakfastPrice" type="{http://bartws.micros.com/1.17}Price"/&gt;
 *         &lt;element name="breakfastCodeType"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="Adult"/&gt;
 *               &lt;enumeration value="Child"/&gt;
 *               &lt;enumeration value="Both"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dependentCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="freeRatio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HotelBreakfasts", propOrder = {
    "breakfastCode",
    "breakfastLegend",
    "breakfastPrice",
    "breakfastCodeType",
    "dependentCode",
    "freeRatio"
})
public class HotelBreakfasts
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String breakfastCode;
    @XmlElement(required = true)
    protected String breakfastLegend;
    @XmlElement(required = true)
    protected Price breakfastPrice;
    @XmlElement(required = true)
    protected String breakfastCodeType;
    protected String dependentCode;
    protected String freeRatio;

    /**
     * Gets the value of the breakfastCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBreakfastCode() {
        return breakfastCode;
    }

    /**
     * Sets the value of the breakfastCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBreakfastCode(String value) {
        this.breakfastCode = value;
    }

    /**
     * Gets the value of the breakfastLegend property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBreakfastLegend() {
        return breakfastLegend;
    }

    /**
     * Sets the value of the breakfastLegend property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBreakfastLegend(String value) {
        this.breakfastLegend = value;
    }

    /**
     * Gets the value of the breakfastPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getBreakfastPrice() {
        return breakfastPrice;
    }

    /**
     * Sets the value of the breakfastPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setBreakfastPrice(Price value) {
        this.breakfastPrice = value;
    }

    /**
     * Gets the value of the breakfastCodeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBreakfastCodeType() {
        return breakfastCodeType;
    }

    /**
     * Sets the value of the breakfastCodeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBreakfastCodeType(String value) {
        this.breakfastCodeType = value;
    }

    /**
     * Gets the value of the dependentCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDependentCode() {
        return dependentCode;
    }

    /**
     * Sets the value of the dependentCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDependentCode(String value) {
        this.dependentCode = value;
    }

    /**
     * Gets the value of the freeRatio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFreeRatio() {
        return freeRatio;
    }

    /**
     * Sets the value of the freeRatio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFreeRatio(String value) {
        this.freeRatio = value;
    }

}

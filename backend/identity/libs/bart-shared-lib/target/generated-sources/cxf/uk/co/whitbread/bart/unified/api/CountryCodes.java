
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CountryCodes complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CountryCodes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="countryCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="countryLegend" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="passportRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CountryCodes", propOrder = {
    "countryCode",
    "countryLegend",
    "passportRequired"
})
public class CountryCodes
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String countryCode;
    @XmlElement(required = true)
    protected String countryLegend;
    protected Boolean passportRequired;

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the countryLegend property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryLegend() {
        return countryLegend;
    }

    /**
     * Sets the value of the countryLegend property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryLegend(String value) {
        this.countryLegend = value;
    }

    /**
     * Gets the value of the passportRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPassportRequired() {
        return passportRequired;
    }

    /**
     * Sets the value of the passportRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPassportRequired(Boolean value) {
        this.passportRequired = value;
    }

}

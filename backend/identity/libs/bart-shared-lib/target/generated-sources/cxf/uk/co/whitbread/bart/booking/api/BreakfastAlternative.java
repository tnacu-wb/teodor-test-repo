
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BreakfastAlternative complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BreakfastAlternative"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="alternativeCode" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="alternativeText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="alternativePrice" type="{http://bartws.micros.com/1.31}Price"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BreakfastAlternative", propOrder = {
    "alternativeCode",
    "alternativeText",
    "alternativePrice"
})
public class BreakfastAlternative
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected Long alternativeCode;
    protected String alternativeText;
    @XmlElement(required = true)
    protected Price alternativePrice;

    /**
     * Gets the value of the alternativeCode property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAlternativeCode() {
        return alternativeCode;
    }

    /**
     * Sets the value of the alternativeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAlternativeCode(Long value) {
        this.alternativeCode = value;
    }

    /**
     * Gets the value of the alternativeText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlternativeText() {
        return alternativeText;
    }

    /**
     * Sets the value of the alternativeText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlternativeText(String value) {
        this.alternativeText = value;
    }

    /**
     * Gets the value of the alternativePrice property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getAlternativePrice() {
        return alternativePrice;
    }

    /**
     * Sets the value of the alternativePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setAlternativePrice(Price value) {
        this.alternativePrice = value;
    }

}

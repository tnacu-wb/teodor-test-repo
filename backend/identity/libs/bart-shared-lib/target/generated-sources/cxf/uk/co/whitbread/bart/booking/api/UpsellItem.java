
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for UpsellItem complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UpsellItem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="upsellItemCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="upsellItemLegend" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="upsellItemPrice" type="{http://bartws.micros.com/1.31}Price"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpsellItem", propOrder = {
    "upsellItemCode",
    "upsellItemLegend",
    "upsellItemPrice"
})
public class UpsellItem
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String upsellItemCode;
    @XmlElement(required = true)
    protected String upsellItemLegend;
    @XmlElement(required = true)
    protected Price upsellItemPrice;

    /**
     * Gets the value of the upsellItemCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpsellItemCode() {
        return upsellItemCode;
    }

    /**
     * Sets the value of the upsellItemCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpsellItemCode(String value) {
        this.upsellItemCode = value;
    }

    /**
     * Gets the value of the upsellItemLegend property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpsellItemLegend() {
        return upsellItemLegend;
    }

    /**
     * Sets the value of the upsellItemLegend property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpsellItemLegend(String value) {
        this.upsellItemLegend = value;
    }

    /**
     * Gets the value of the upsellItemPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getUpsellItemPrice() {
        return upsellItemPrice;
    }

    /**
     * Sets the value of the upsellItemPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setUpsellItemPrice(Price value) {
        this.upsellItemPrice = value;
    }

}

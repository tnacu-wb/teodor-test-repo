
package uk.co.whitbread.bart.checkin.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PackageItem complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PackageItem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="elementDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="elementNights" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageItem", propOrder = {
    "elementDescription",
    "elementNights"
})
public class PackageItem
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String elementDescription;
    protected String elementNights;

    /**
     * Gets the value of the elementDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElementDescription() {
        return elementDescription;
    }

    /**
     * Sets the value of the elementDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementDescription(String value) {
        this.elementDescription = value;
    }

    /**
     * Gets the value of the elementNights property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElementNights() {
        return elementNights;
    }

    /**
     * Sets the value of the elementNights property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementNights(String value) {
        this.elementNights = value;
    }

}


package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PackageElements complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PackageElements"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="elementCode" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="elementText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageElements", propOrder = {
    "elementCode",
    "elementText"
})
public class PackageElements
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected Long elementCode;
    protected String elementText;

    /**
     * Gets the value of the elementCode property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getElementCode() {
        return elementCode;
    }

    /**
     * Sets the value of the elementCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setElementCode(Long value) {
        this.elementCode = value;
    }

    /**
     * Gets the value of the elementText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElementText() {
        return elementText;
    }

    /**
     * Sets the value of the elementText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementText(String value) {
        this.elementText = value;
    }

}

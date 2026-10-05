
package uk.co.whitbread.azure.email.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SupplementBooking complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SupplementBooking"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="supplementsArray" type="{https://dto.email.transact.comms.int.wtbapi.com}ArrayOfSupplement" minOccurs="0"/&gt;
 *         &lt;element name="totalSupplementCost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupplementBooking", propOrder = {
    "supplementsArray",
    "totalSupplementCost"
})
public class SupplementBooking
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected ArrayOfSupplement supplementsArray;
    @XmlElement(nillable = true)
    protected String totalSupplementCost;

    /**
     * Gets the value of the supplementsArray property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupplement }
     *     
     */
    public ArrayOfSupplement getSupplementsArray() {
        return supplementsArray;
    }

    /**
     * Sets the value of the supplementsArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupplement }
     *     
     */
    public void setSupplementsArray(ArrayOfSupplement value) {
        this.supplementsArray = value;
    }

    /**
     * Gets the value of the totalSupplementCost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalSupplementCost() {
        return totalSupplementCost;
    }

    /**
     * Sets the value of the totalSupplementCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalSupplementCost(String value) {
        this.totalSupplementCost = value;
    }

}

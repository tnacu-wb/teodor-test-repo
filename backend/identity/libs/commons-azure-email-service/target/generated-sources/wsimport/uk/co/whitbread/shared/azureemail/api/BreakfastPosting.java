
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BreakfastPosting complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="BreakfastPosting">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="breakfastArray" type="{https://dto.email.transact.comms.int.wtbapi.com}ArrayOfBreakfast" minOccurs="0"/>
 *         <element name="totalBreakfastCost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BreakfastPosting", propOrder = {
    "breakfastArray",
    "totalBreakfastCost"
})
public class BreakfastPosting
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected ArrayOfBreakfast breakfastArray;
    @XmlElement(nillable = true)
    protected String totalBreakfastCost;

    /**
     * Gets the value of the breakfastArray property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfBreakfast }
     *     
     */
    public ArrayOfBreakfast getBreakfastArray() {
        return breakfastArray;
    }

    /**
     * Sets the value of the breakfastArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfBreakfast }
     *     
     */
    public void setBreakfastArray(ArrayOfBreakfast value) {
        this.breakfastArray = value;
    }

    /**
     * Gets the value of the totalBreakfastCost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalBreakfastCost() {
        return totalBreakfastCost;
    }

    /**
     * Sets the value of the totalBreakfastCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalBreakfastCost(String value) {
        this.totalBreakfastCost = value;
    }

}

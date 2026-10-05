
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BreakfastUpsellCode complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BreakfastUpsellCode"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="breakfastCode" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="adults" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="children" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BreakfastUpsellCode", propOrder = {
    "breakfastCode",
    "adults",
    "children"
})
public class BreakfastUpsellCode
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected long breakfastCode;
    protected long adults;
    protected long children;

    /**
     * Gets the value of the breakfastCode property.
     * 
     */
    public long getBreakfastCode() {
        return breakfastCode;
    }

    /**
     * Sets the value of the breakfastCode property.
     * 
     */
    public void setBreakfastCode(long value) {
        this.breakfastCode = value;
    }

    /**
     * Gets the value of the adults property.
     * 
     */
    public long getAdults() {
        return adults;
    }

    /**
     * Sets the value of the adults property.
     * 
     */
    public void setAdults(long value) {
        this.adults = value;
    }

    /**
     * Gets the value of the children property.
     * 
     */
    public long getChildren() {
        return children;
    }

    /**
     * Sets the value of the children property.
     * 
     */
    public void setChildren(long value) {
        this.children = value;
    }

}

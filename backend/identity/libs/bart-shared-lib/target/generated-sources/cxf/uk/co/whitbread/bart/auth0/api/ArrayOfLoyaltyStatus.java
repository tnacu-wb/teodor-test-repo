
package uk.co.whitbread.bart.auth0.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfLoyaltyStatus complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfLoyaltyStatus"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="LoyaltyStatus" type="{http://bartws.micros.com/1.13}LoyaltyStatus2" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfLoyaltyStatus", propOrder = {
    "loyaltyStatus"
})
public class ArrayOfLoyaltyStatus
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "LoyaltyStatus", nillable = true)
    protected List<LoyaltyStatus2> loyaltyStatus;

    /**
     * Gets the value of the loyaltyStatus property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the loyaltyStatus property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getLoyaltyStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LoyaltyStatus2 }
     * </p>
     * 
     * 
     * @return
     *     The value of the loyaltyStatus property.
     */
    public List<LoyaltyStatus2> getLoyaltyStatus() {
        if (loyaltyStatus == null) {
            loyaltyStatus = new ArrayList<>();
        }
        return this.loyaltyStatus;
    }

}

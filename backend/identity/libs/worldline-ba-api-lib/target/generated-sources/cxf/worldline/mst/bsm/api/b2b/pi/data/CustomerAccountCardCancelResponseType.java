
package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountCardCancelResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardCancelResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NewCardDetails" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountCardAllDetailsType" minOccurs="0"/&gt;
 *         &lt;element name="CancelledCardDetails" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountCardAllDetailsType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountCardCancelResponseType", propOrder = {
    "newCardDetails",
    "cancelledCardDetails"
})
public class CustomerAccountCardCancelResponseType
    extends ResponseType
{

    @XmlElement(name = "NewCardDetails")
    protected CustomerAccountCardAllDetailsType newCardDetails;
    @XmlElement(name = "CancelledCardDetails")
    protected CustomerAccountCardAllDetailsType cancelledCardDetails;

    /**
     * Gets the value of the newCardDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountCardAllDetailsType }
     *     
     */
    public CustomerAccountCardAllDetailsType getNewCardDetails() {
        return newCardDetails;
    }

    /**
     * Sets the value of the newCardDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountCardAllDetailsType }
     *     
     */
    public void setNewCardDetails(CustomerAccountCardAllDetailsType value) {
        this.newCardDetails = value;
    }

    /**
     * Gets the value of the cancelledCardDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountCardAllDetailsType }
     *     
     */
    public CustomerAccountCardAllDetailsType getCancelledCardDetails() {
        return cancelledCardDetails;
    }

    /**
     * Sets the value of the cancelledCardDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountCardAllDetailsType }
     *     
     */
    public void setCancelledCardDetails(CustomerAccountCardAllDetailsType value) {
        this.cancelledCardDetails = value;
    }

}

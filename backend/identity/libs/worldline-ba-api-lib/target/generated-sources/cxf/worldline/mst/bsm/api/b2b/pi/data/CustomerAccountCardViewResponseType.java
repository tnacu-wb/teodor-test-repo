
package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountCardViewResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardViewResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Card" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountCardAllDetailsType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountCardViewResponseType", propOrder = {
    "card"
})
public class CustomerAccountCardViewResponseType
    extends ResponseType
{

    @XmlElement(name = "Card")
    protected CustomerAccountCardAllDetailsType card;

    /**
     * Gets the value of the card property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountCardAllDetailsType }
     *     
     */
    public CustomerAccountCardAllDetailsType getCard() {
        return card;
    }

    /**
     * Sets the value of the card property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountCardAllDetailsType }
     *     
     */
    public void setCard(CustomerAccountCardAllDetailsType value) {
        this.card = value;
    }

}

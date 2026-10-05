
package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for TetherByCardNumberResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="TetherByCardNumberResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TetherDetails" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}TetherDetailsType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TetherByCardNumberResponseType", propOrder = {
    "tetherDetails"
})
public class TetherByCardNumberResponseType
    extends ResponseType
{

    /**
     * Only returned in the event of a successful tether
     * 
     */
    @XmlElement(name = "TetherDetails")
    protected TetherDetailsType tetherDetails;

    /**
     * Only returned in the event of a successful tether
     * 
     * @return
     *     possible object is
     *     {@link TetherDetailsType }
     *     
     */
    public TetherDetailsType getTetherDetails() {
        return tetherDetails;
    }

    /**
     * Sets the value of the tetherDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link TetherDetailsType }
     *     
     * @see #getTetherDetails()
     */
    public void setTetherDetails(TetherDetailsType value) {
        this.tetherDetails = value;
    }

}

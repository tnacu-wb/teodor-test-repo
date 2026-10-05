
package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for TetheredUserDetailsGetResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="TetheredUserDetailsGetResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TetheredUserDetails" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}TetheredUserDetailsType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TetheredUserDetailsGetResponseType", propOrder = {
    "tetheredUserDetails"
})
public class TetheredUserDetailsGetResponseType
    extends ResponseType
{

    @XmlElement(name = "TetheredUserDetails")
    protected TetheredUserDetailsType tetheredUserDetails;

    /**
     * Gets the value of the tetheredUserDetails property.
     * 
     * @return
     *     possible object is
     *     {@link TetheredUserDetailsType }
     *     
     */
    public TetheredUserDetailsType getTetheredUserDetails() {
        return tetheredUserDetails;
    }

    /**
     * Sets the value of the tetheredUserDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link TetheredUserDetailsType }
     *     
     */
    public void setTetheredUserDetails(TetheredUserDetailsType value) {
        this.tetheredUserDetails = value;
    }

}

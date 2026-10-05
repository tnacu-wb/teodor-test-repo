
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CreditProposeNewLimitResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CreditProposeNewLimitResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CLIRequestId" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditProposeNewLimitResponseType", propOrder = {
    "cliRequestId"
})
public class CreditProposeNewLimitResponseType
    extends ResponseType
{

    @XmlElement(name = "CLIRequestId", required = true)
    protected String cliRequestId;

    /**
     * Gets the value of the cliRequestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCLIRequestId() {
        return cliRequestId;
    }

    /**
     * Sets the value of the cliRequestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCLIRequestId(String value) {
        this.cliRequestId = value;
    }

}

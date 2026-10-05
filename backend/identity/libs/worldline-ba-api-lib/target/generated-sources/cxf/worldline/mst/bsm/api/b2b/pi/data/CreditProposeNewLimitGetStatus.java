
package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Request" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CreditProposeNewLimitGetStatusRequestType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "request"
})
@XmlRootElement(name = "creditProposeNewLimitGetStatus", namespace = "worldline.mst.bsm.api.b2b.pi.messages.v1.1")
public class CreditProposeNewLimitGetStatus {

    @XmlElement(name = "Request", namespace = "worldline.mst.bsm.api.b2b.pi.messages.v1.1", required = true)
    protected CreditProposeNewLimitGetStatusRequestType request;

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link CreditProposeNewLimitGetStatusRequestType }
     *     
     */
    public CreditProposeNewLimitGetStatusRequestType getRequest() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditProposeNewLimitGetStatusRequestType }
     *     
     */
    public void setRequest(CreditProposeNewLimitGetStatusRequestType value) {
        this.request = value;
    }

}

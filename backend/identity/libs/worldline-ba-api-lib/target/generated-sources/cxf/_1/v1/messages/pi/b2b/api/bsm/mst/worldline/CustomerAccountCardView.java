
package _1.v1.messages.pi.b2b.api.bsm.mst.worldline;

import _1.v1.data.pi.b2b.api.bsm.mst.worldline.CustomerAccountCardViewRequestType;
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
 *         &lt;element name="Request" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountCardViewRequestType"/&gt;
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
@XmlRootElement(name = "customerAccountCardView")
public class CustomerAccountCardView {

    @XmlElement(name = "Request", required = true)
    protected CustomerAccountCardViewRequestType request;

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountCardViewRequestType }
     *     
     */
    public CustomerAccountCardViewRequestType getRequest() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountCardViewRequestType }
     *     
     */
    public void setRequest(CustomerAccountCardViewRequestType value) {
        this.request = value;
    }

}

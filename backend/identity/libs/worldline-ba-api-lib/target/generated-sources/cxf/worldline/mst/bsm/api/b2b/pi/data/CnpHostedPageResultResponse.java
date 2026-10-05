
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
 *         &lt;element name="Response" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CNPHostedPageResultResponseType"/&gt;
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
    "response"
})
@XmlRootElement(name = "cnpHostedPageResultResponse", namespace = "worldline.mst.bsm.api.b2b.pi.messages.v1.1")
public class CnpHostedPageResultResponse {

    @XmlElement(name = "Response", namespace = "worldline.mst.bsm.api.b2b.pi.messages.v1.1", required = true)
    protected CNPHostedPageResultResponseType response;

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link CNPHostedPageResultResponseType }
     *     
     */
    public CNPHostedPageResultResponseType getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link CNPHostedPageResultResponseType }
     *     
     */
    public void setResponse(CNPHostedPageResultResponseType value) {
        this.response = value;
    }

}

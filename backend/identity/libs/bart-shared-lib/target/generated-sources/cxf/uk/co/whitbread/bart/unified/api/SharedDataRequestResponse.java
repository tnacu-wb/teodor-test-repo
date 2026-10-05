
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
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
 *         &lt;element name="SharedDataRequestResult" type="{http://bartws.micros.com/1.17}SharedDataResponse"/&gt;
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
    "sharedDataRequestResult"
})
@XmlRootElement(name = "SharedDataRequestResponse")
public class SharedDataRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SharedDataRequestResult", required = true)
    protected SharedDataResponse sharedDataRequestResult;

    /**
     * Gets the value of the sharedDataRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link SharedDataResponse }
     *     
     */
    public SharedDataResponse getSharedDataRequestResult() {
        return sharedDataRequestResult;
    }

    /**
     * Sets the value of the sharedDataRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SharedDataResponse }
     *     
     */
    public void setSharedDataRequestResult(SharedDataResponse value) {
        this.sharedDataRequestResult = value;
    }

}

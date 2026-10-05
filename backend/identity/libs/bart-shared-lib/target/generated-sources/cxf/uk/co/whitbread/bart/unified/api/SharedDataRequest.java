
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="sharedDataRequest" type="{http://bartws.micros.com/1.17}SharedDataRequest" minOccurs="0"/&gt;
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
    "sharedDataRequest"
})
@XmlRootElement(name = "SharedDataRequest")
public class SharedDataRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected SharedDataRequest2 sharedDataRequest;

    /**
     * Gets the value of the sharedDataRequest property.
     * 
     * @return
     *     possible object is
     *     {@link SharedDataRequest2 }
     *     
     */
    public SharedDataRequest2 getSharedDataRequest() {
        return sharedDataRequest;
    }

    /**
     * Sets the value of the sharedDataRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link SharedDataRequest2 }
     *     
     */
    public void setSharedDataRequest(SharedDataRequest2 value) {
        this.sharedDataRequest = value;
    }

}

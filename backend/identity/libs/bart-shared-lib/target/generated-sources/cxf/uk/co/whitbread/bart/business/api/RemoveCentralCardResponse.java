
package uk.co.whitbread.bart.business.api;

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
 *         &lt;element name="RemoveCentralCardResult" type="{http://corporate.micros.com/1.0}RemoveCentralCardResult"/&gt;
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
    "removeCentralCardResult"
})
@XmlRootElement(name = "RemoveCentralCardResponse")
public class RemoveCentralCardResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "RemoveCentralCardResult", required = true)
    protected RemoveCentralCardResult removeCentralCardResult;

    /**
     * Gets the value of the removeCentralCardResult property.
     * 
     * @return
     *     possible object is
     *     {@link RemoveCentralCardResult }
     *     
     */
    public RemoveCentralCardResult getRemoveCentralCardResult() {
        return removeCentralCardResult;
    }

    /**
     * Sets the value of the removeCentralCardResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link RemoveCentralCardResult }
     *     
     */
    public void setRemoveCentralCardResult(RemoveCentralCardResult value) {
        this.removeCentralCardResult = value;
    }

}

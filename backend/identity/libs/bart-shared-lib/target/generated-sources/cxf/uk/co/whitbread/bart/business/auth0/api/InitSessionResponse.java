
package uk.co.whitbread.bart.business.auth0.api;

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
 *         &lt;element name="InitSessionResult" type="{http://corporate.micros.com/1.0}InitSessionResponse"/&gt;
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
    "initSessionResult"
})
@XmlRootElement(name = "InitSessionResponse")
public class InitSessionResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "InitSessionResult", required = true)
    protected InitSessionResponse2 initSessionResult;

    /**
     * Gets the value of the initSessionResult property.
     * 
     * @return
     *     possible object is
     *     {@link InitSessionResponse2 }
     *     
     */
    public InitSessionResponse2 getInitSessionResult() {
        return initSessionResult;
    }

    /**
     * Sets the value of the initSessionResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link InitSessionResponse2 }
     *     
     */
    public void setInitSessionResult(InitSessionResponse2 value) {
        this.initSessionResult = value;
    }

}

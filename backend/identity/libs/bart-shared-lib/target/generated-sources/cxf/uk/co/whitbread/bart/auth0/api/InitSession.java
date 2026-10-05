
package uk.co.whitbread.bart.auth0.api;

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
 *         &lt;element name="initSessionRequest" type="{http://bartws.micros.com/1.13}InitSessionRequest" minOccurs="0"/&gt;
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
    "initSessionRequest"
})
@XmlRootElement(name = "InitSession")
public class InitSession
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected InitSessionRequest initSessionRequest;

    /**
     * Gets the value of the initSessionRequest property.
     * 
     * @return
     *     possible object is
     *     {@link InitSessionRequest }
     *     
     */
    public InitSessionRequest getInitSessionRequest() {
        return initSessionRequest;
    }

    /**
     * Sets the value of the initSessionRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link InitSessionRequest }
     *     
     */
    public void setInitSessionRequest(InitSessionRequest value) {
        this.initSessionRequest = value;
    }

}

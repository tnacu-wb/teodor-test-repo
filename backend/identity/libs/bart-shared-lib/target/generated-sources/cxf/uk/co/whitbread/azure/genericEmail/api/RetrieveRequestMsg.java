
package uk.co.whitbread.azure.genericEmail.api;

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
 *         &lt;element name="RetrieveRequest" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}RetrieveRequest"/&gt;
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
    "retrieveRequest"
})
@XmlRootElement(name = "RetrieveRequestMsg")
public class RetrieveRequestMsg
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "RetrieveRequest", required = true)
    protected RetrieveRequest retrieveRequest;

    /**
     * Gets the value of the retrieveRequest property.
     * 
     * @return
     *     possible object is
     *     {@link RetrieveRequest }
     *     
     */
    public RetrieveRequest getRetrieveRequest() {
        return retrieveRequest;
    }

    /**
     * Sets the value of the retrieveRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link RetrieveRequest }
     *     
     */
    public void setRetrieveRequest(RetrieveRequest value) {
        this.retrieveRequest = value;
    }

}

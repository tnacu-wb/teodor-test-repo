
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RetrieveSingleRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RetrieveSingleRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Request"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RequestedObject" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject"/&gt;
 *         &lt;element name="RetrieveOption" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Options" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RetrieveSingleRequest", propOrder = {
    "requestedObject",
    "retrieveOption"
})
public class RetrieveSingleRequest
    extends Request
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "RequestedObject", required = true)
    protected APIObject requestedObject;
    @XmlElement(name = "RetrieveOption")
    protected Options retrieveOption;

    /**
     * Gets the value of the requestedObject property.
     * 
     * @return
     *     possible object is
     *     {@link APIObject }
     *     
     */
    public APIObject getRequestedObject() {
        return requestedObject;
    }

    /**
     * Sets the value of the requestedObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link APIObject }
     *     
     */
    public void setRequestedObject(APIObject value) {
        this.requestedObject = value;
    }

    /**
     * Gets the value of the retrieveOption property.
     * 
     * @return
     *     possible object is
     *     {@link Options }
     *     
     */
    public Options getRetrieveOption() {
        return retrieveOption;
    }

    /**
     * Sets the value of the retrieveOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Options }
     *     
     */
    public void setRetrieveOption(Options value) {
        this.retrieveOption = value;
    }

}

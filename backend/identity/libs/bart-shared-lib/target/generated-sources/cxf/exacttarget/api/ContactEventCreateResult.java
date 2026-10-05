
package exacttarget.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ContactEventCreateResult complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ContactEventCreateResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}CreateResult"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="EventInstanceID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AsyncRequestID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactEventCreateResult", propOrder = {
    "eventInstanceID",
    "asyncRequestID"
})
public class ContactEventCreateResult
    extends CreateResult
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "EventInstanceID")
    protected String eventInstanceID;
    @XmlElement(name = "AsyncRequestID")
    protected Long asyncRequestID;

    /**
     * Gets the value of the eventInstanceID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventInstanceID() {
        return eventInstanceID;
    }

    /**
     * Sets the value of the eventInstanceID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventInstanceID(String value) {
        this.eventInstanceID = value;
    }

    /**
     * Gets the value of the asyncRequestID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAsyncRequestID() {
        return asyncRequestID;
    }

    /**
     * Sets the value of the asyncRequestID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAsyncRequestID(Long value) {
        this.asyncRequestID = value;
    }

}

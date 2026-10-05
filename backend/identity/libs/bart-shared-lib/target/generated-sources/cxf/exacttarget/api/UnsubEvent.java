
package exacttarget.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for UnsubEvent complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UnsubEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}TrackingEvent"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="List" type="{http://exacttarget.com/wsdl/partnerAPI}List" minOccurs="0"/&gt;
 *         &lt;element name="IsMasterUnsubscribed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnsubEvent", propOrder = {
    "list",
    "isMasterUnsubscribed"
})
public class UnsubEvent
    extends TrackingEvent
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "List")
    protected List list;
    @XmlElement(name = "IsMasterUnsubscribed")
    protected Boolean isMasterUnsubscribed;

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link List }
     *     
     */
    public List getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link List }
     *     
     */
    public void setList(List value) {
        this.list = value;
    }

    /**
     * Gets the value of the isMasterUnsubscribed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsMasterUnsubscribed() {
        return isMasterUnsubscribed;
    }

    /**
     * Sets the value of the isMasterUnsubscribed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsMasterUnsubscribed(Boolean value) {
        this.isMasterUnsubscribed = value;
    }

}

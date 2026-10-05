
package exacttarget.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for TriggeredSendCreateResult complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="TriggeredSendCreateResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}CreateResult"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SubscriberFailures" type="{http://exacttarget.com/wsdl/partnerAPI}SubscriberResult" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TriggeredSendCreateResult", propOrder = {
    "subscriberFailures"
})
public class TriggeredSendCreateResult
    extends CreateResult
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SubscriberFailures")
    protected List<SubscriberResult> subscriberFailures;

    /**
     * Gets the value of the subscriberFailures property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the subscriberFailures property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getSubscriberFailures().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubscriberResult }
     * </p>
     * 
     * 
     * @return
     *     The value of the subscriberFailures property.
     */
    public List<SubscriberResult> getSubscriberFailures() {
        if (subscriberFailures == null) {
            subscriberFailures = new ArrayList<>();
        }
        return this.subscriberFailures;
    }

}

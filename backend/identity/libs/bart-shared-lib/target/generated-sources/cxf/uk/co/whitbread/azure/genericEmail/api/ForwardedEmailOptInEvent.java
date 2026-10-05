
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ForwardedEmailOptInEvent complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ForwardedEmailOptInEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}TrackingEvent"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OptInSubscriberKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ForwardedEmailOptInEvent", propOrder = {
    "optInSubscriberKey"
})
public class ForwardedEmailOptInEvent
    extends TrackingEvent
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "OptInSubscriberKey")
    protected String optInSubscriberKey;

    /**
     * Gets the value of the optInSubscriberKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptInSubscriberKey() {
        return optInSubscriberKey;
    }

    /**
     * Sets the value of the optInSubscriberKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptInSubscriberKey(String value) {
        this.optInSubscriberKey = value;
    }

}

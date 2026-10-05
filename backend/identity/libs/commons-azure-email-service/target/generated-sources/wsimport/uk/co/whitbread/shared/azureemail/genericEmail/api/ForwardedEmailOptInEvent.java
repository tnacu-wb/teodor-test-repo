
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ForwardedEmailOptInEvent complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="ForwardedEmailOptInEvent">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}TrackingEvent">
 *       <sequence>
 *         <element name="OptInSubscriberKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
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

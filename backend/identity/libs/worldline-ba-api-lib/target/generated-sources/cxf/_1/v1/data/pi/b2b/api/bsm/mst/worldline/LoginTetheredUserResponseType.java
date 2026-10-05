
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for LoginTetheredUserResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="LoginTetheredUserResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NewSession" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}NewSessionType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LoginTetheredUserResponseType", propOrder = {
    "newSession"
})
public class LoginTetheredUserResponseType
    extends ResponseType
{

    /**
     * Newly created session details
     * 
     */
    @XmlElement(name = "NewSession")
    protected NewSessionType newSession;

    /**
     * Newly created session details
     * 
     * @return
     *     possible object is
     *     {@link NewSessionType }
     *     
     */
    public NewSessionType getNewSession() {
        return newSession;
    }

    /**
     * Sets the value of the newSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link NewSessionType }
     *     
     * @see #getNewSession()
     */
    public void setNewSession(NewSessionType value) {
        this.newSession = value;
    }

}


package uk.co.whitbread.bart.business.api;

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
 *         &lt;element name="InviteEmployeeResult" type="{http://corporate.micros.com/1.0}InviteEmployeeResponse"/&gt;
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
    "inviteEmployeeResult"
})
@XmlRootElement(name = "InviteEmployeeResponse")
public class InviteEmployeeResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "InviteEmployeeResult", required = true)
    protected InviteEmployeeResponse2 inviteEmployeeResult;

    /**
     * Gets the value of the inviteEmployeeResult property.
     * 
     * @return
     *     possible object is
     *     {@link InviteEmployeeResponse2 }
     *     
     */
    public InviteEmployeeResponse2 getInviteEmployeeResult() {
        return inviteEmployeeResult;
    }

    /**
     * Sets the value of the inviteEmployeeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link InviteEmployeeResponse2 }
     *     
     */
    public void setInviteEmployeeResult(InviteEmployeeResponse2 value) {
        this.inviteEmployeeResult = value;
    }

}

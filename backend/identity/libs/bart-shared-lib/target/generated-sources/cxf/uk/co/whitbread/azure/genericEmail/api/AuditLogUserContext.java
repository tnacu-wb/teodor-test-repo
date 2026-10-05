
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AuditLogUserContext complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AuditLogUserContext"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InitiatingUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="InitiatingUserIpAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuditLogUserContext", propOrder = {
    "initiatingUserName",
    "initiatingUserIpAddress"
})
public class AuditLogUserContext
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "InitiatingUserName")
    protected String initiatingUserName;
    @XmlElement(name = "InitiatingUserIpAddress")
    protected String initiatingUserIpAddress;

    /**
     * Gets the value of the initiatingUserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitiatingUserName() {
        return initiatingUserName;
    }

    /**
     * Sets the value of the initiatingUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitiatingUserName(String value) {
        this.initiatingUserName = value;
    }

    /**
     * Gets the value of the initiatingUserIpAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitiatingUserIpAddress() {
        return initiatingUserIpAddress;
    }

    /**
     * Sets the value of the initiatingUserIpAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitiatingUserIpAddress(String value) {
        this.initiatingUserIpAddress = value;
    }

}

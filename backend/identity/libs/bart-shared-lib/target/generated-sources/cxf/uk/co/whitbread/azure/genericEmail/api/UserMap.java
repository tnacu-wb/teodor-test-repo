
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for UserMap complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UserMap"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIProperty"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ETAccountUser" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AccountUser"/&gt;
 *         &lt;element name="AdditionalData" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIProperty" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserMap", propOrder = {
    "etAccountUser",
    "additionalData"
})
public class UserMap
    extends APIProperty
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ETAccountUser", required = true)
    protected AccountUser etAccountUser;
    @XmlElement(name = "AdditionalData")
    protected List<APIProperty> additionalData;

    /**
     * Gets the value of the etAccountUser property.
     * 
     * @return
     *     possible object is
     *     {@link AccountUser }
     *     
     */
    public AccountUser getETAccountUser() {
        return etAccountUser;
    }

    /**
     * Sets the value of the etAccountUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountUser }
     *     
     */
    public void setETAccountUser(AccountUser value) {
        this.etAccountUser = value;
    }

    /**
     * Gets the value of the additionalData property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the additionalData property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getAdditionalData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link APIProperty }
     * </p>
     * 
     * 
     * @return
     *     The value of the additionalData property.
     */
    public List<APIProperty> getAdditionalData() {
        if (additionalData == null) {
            additionalData = new ArrayList<>();
        }
        return this.additionalData;
    }

}

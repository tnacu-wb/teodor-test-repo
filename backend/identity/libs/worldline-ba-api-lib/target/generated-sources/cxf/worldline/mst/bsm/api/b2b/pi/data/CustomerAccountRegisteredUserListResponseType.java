
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountRegisteredUserListResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountRegisteredUserListResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegisteredUsers" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountRegisteredUserType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountRegisteredUserListResponseType", propOrder = {
    "registeredUsers"
})
public class CustomerAccountRegisteredUserListResponseType
    extends ResponseType
{

    @XmlElement(name = "RegisteredUsers")
    protected List<CustomerAccountRegisteredUserType> registeredUsers;

    /**
     * Gets the value of the registeredUsers property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the registeredUsers property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getRegisteredUsers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomerAccountRegisteredUserType }
     * </p>
     * 
     * 
     * @return
     *     The value of the registeredUsers property.
     */
    public List<CustomerAccountRegisteredUserType> getRegisteredUsers() {
        if (registeredUsers == null) {
            registeredUsers = new ArrayList<>();
        }
        return this.registeredUsers;
    }

}

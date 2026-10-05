
package uk.co.whitbread.bart.auth0.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfRegisteredGuestPreference complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfRegisteredGuestPreference"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegisteredGuestPreference" type="{http://bartws.micros.com/1.13}RegisteredGuestPreference2" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfRegisteredGuestPreference", propOrder = {
    "registeredGuestPreference"
})
public class ArrayOfRegisteredGuestPreference
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "RegisteredGuestPreference", nillable = true)
    protected List<RegisteredGuestPreference2> registeredGuestPreference;

    /**
     * Gets the value of the registeredGuestPreference property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the registeredGuestPreference property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getRegisteredGuestPreference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegisteredGuestPreference2 }
     * </p>
     * 
     * 
     * @return
     *     The value of the registeredGuestPreference property.
     */
    public List<RegisteredGuestPreference2> getRegisteredGuestPreference() {
        if (registeredGuestPreference == null) {
            registeredGuestPreference = new ArrayList<>();
        }
        return this.registeredGuestPreference;
    }

}

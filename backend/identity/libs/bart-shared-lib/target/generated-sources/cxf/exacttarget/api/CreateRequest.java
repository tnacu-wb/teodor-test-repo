
package exacttarget.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="Options" type="{http://exacttarget.com/wsdl/partnerAPI}CreateOptions"/&gt;
 *         &lt;element name="Objects" type="{http://exacttarget.com/wsdl/partnerAPI}APIObject" maxOccurs="unbounded"/&gt;
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
    "options",
    "objects"
})
@XmlRootElement(name = "CreateRequest")
public class CreateRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Options", required = true)
    protected CreateOptions options;
    @XmlElement(name = "Objects", required = true)
    protected List<APIObject> objects;

    /**
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link CreateOptions }
     *     
     */
    public CreateOptions getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateOptions }
     *     
     */
    public void setOptions(CreateOptions value) {
        this.options = value;
    }

    /**
     * Gets the value of the objects property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the objects property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getObjects().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link APIObject }
     * </p>
     * 
     * 
     * @return
     *     The value of the objects property.
     */
    public List<APIObject> getObjects() {
        if (objects == null) {
            objects = new ArrayList<>();
        }
        return this.objects;
    }

}

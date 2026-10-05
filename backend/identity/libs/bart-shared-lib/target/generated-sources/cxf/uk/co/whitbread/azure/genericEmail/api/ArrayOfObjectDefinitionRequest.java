
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfObjectDefinitionRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfObjectDefinitionRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ObjectDefinitionRequest" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ObjectDefinitionRequest" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfObjectDefinitionRequest", propOrder = {
    "objectDefinitionRequest"
})
public class ArrayOfObjectDefinitionRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ObjectDefinitionRequest", nillable = true)
    protected List<ObjectDefinitionRequest> objectDefinitionRequest;

    /**
     * Gets the value of the objectDefinitionRequest property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the objectDefinitionRequest property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getObjectDefinitionRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ObjectDefinitionRequest }
     * </p>
     * 
     * 
     * @return
     *     The value of the objectDefinitionRequest property.
     */
    public List<ObjectDefinitionRequest> getObjectDefinitionRequest() {
        if (objectDefinitionRequest == null) {
            objectDefinitionRequest = new ArrayList<>();
        }
        return this.objectDefinitionRequest;
    }

}


package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for UpdateResult complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UpdateResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Result"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Object" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject"/&gt;
 *         &lt;element name="UpdateResults" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}UpdateResult" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ParentPropertyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateResult", propOrder = {
    "object",
    "updateResults",
    "parentPropertyName"
})
@XmlSeeAlso({
    DataExtensionUpdateResult.class
})
public class UpdateResult
    extends Result
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Object", required = true)
    protected APIObject object;
    @XmlElement(name = "UpdateResults")
    protected List<UpdateResult> updateResults;
    @XmlElement(name = "ParentPropertyName")
    protected String parentPropertyName;

    /**
     * Gets the value of the object property.
     * 
     * @return
     *     possible object is
     *     {@link APIObject }
     *     
     */
    public APIObject getObject() {
        return object;
    }

    /**
     * Sets the value of the object property.
     * 
     * @param value
     *     allowed object is
     *     {@link APIObject }
     *     
     */
    public void setObject(APIObject value) {
        this.object = value;
    }

    /**
     * Gets the value of the updateResults property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the updateResults property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getUpdateResults().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UpdateResult }
     * </p>
     * 
     * 
     * @return
     *     The value of the updateResults property.
     */
    public List<UpdateResult> getUpdateResults() {
        if (updateResults == null) {
            updateResults = new ArrayList<>();
        }
        return this.updateResults;
    }

    /**
     * Gets the value of the parentPropertyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentPropertyName() {
        return parentPropertyName;
    }

    /**
     * Sets the value of the parentPropertyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentPropertyName(String value) {
        this.parentPropertyName = value;
    }

}

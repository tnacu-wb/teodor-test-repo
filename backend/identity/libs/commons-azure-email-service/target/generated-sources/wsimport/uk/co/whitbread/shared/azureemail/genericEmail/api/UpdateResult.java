
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateResult complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="UpdateResult">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Result">
 *       <sequence>
 *         <element name="Object" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject"/>
 *         <element name="UpdateResults" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}UpdateResult" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="ParentPropertyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
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
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the updateResults property.</p>
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

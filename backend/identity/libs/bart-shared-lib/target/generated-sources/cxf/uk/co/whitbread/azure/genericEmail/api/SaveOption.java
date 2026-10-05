
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SaveOption complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SaveOption"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PropertyName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="SaveAction" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SaveAction"/&gt;
 *         &lt;element name="TrackChanges" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveOption", propOrder = {
    "propertyName",
    "saveAction",
    "trackChanges"
})
public class SaveOption
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "PropertyName", required = true)
    protected String propertyName;
    @XmlElement(name = "SaveAction", required = true)
    @XmlSchemaType(name = "string")
    protected SaveAction saveAction;
    @XmlElement(name = "TrackChanges")
    protected Boolean trackChanges;

    /**
     * Gets the value of the propertyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the value of the propertyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyName(String value) {
        this.propertyName = value;
    }

    /**
     * Gets the value of the saveAction property.
     * 
     * @return
     *     possible object is
     *     {@link SaveAction }
     *     
     */
    public SaveAction getSaveAction() {
        return saveAction;
    }

    /**
     * Sets the value of the saveAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link SaveAction }
     *     
     */
    public void setSaveAction(SaveAction value) {
        this.saveAction = value;
    }

    /**
     * Gets the value of the trackChanges property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTrackChanges() {
        return trackChanges;
    }

    /**
     * Sets the value of the trackChanges property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTrackChanges(Boolean value) {
        this.trackChanges = value;
    }

}

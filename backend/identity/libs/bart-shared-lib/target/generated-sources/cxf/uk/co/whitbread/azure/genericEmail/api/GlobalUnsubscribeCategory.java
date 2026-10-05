
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for GlobalUnsubscribeCategory complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="GlobalUnsubscribeCategory"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="IgnorableByPartners" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="Ignore" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalUnsubscribeCategory", propOrder = {
    "name",
    "ignorableByPartners",
    "ignore"
})
public class GlobalUnsubscribeCategory
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "IgnorableByPartners")
    protected boolean ignorableByPartners;
    @XmlElement(name = "Ignore")
    protected boolean ignore;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the ignorableByPartners property.
     * 
     */
    public boolean isIgnorableByPartners() {
        return ignorableByPartners;
    }

    /**
     * Sets the value of the ignorableByPartners property.
     * 
     */
    public void setIgnorableByPartners(boolean value) {
        this.ignorableByPartners = value;
    }

    /**
     * Gets the value of the ignore property.
     * 
     */
    public boolean isIgnore() {
        return ignore;
    }

    /**
     * Sets the value of the ignore property.
     * 
     */
    public void setIgnore(boolean value) {
        this.ignore = value;
    }

}

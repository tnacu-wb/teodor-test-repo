
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RetrieveOptions complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RetrieveOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Options"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BatchSize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="IncludeObjects" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="OnlyIncludeBase" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RetrieveOptions", propOrder = {
    "batchSize",
    "includeObjects",
    "onlyIncludeBase"
})
public class RetrieveOptions
    extends Options
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "BatchSize")
    protected Integer batchSize;
    @XmlElement(name = "IncludeObjects")
    protected Boolean includeObjects;
    @XmlElement(name = "OnlyIncludeBase")
    protected Boolean onlyIncludeBase;

    /**
     * Gets the value of the batchSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBatchSize() {
        return batchSize;
    }

    /**
     * Sets the value of the batchSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBatchSize(Integer value) {
        this.batchSize = value;
    }

    /**
     * Gets the value of the includeObjects property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeObjects() {
        return includeObjects;
    }

    /**
     * Sets the value of the includeObjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeObjects(Boolean value) {
        this.includeObjects = value;
    }

    /**
     * Gets the value of the onlyIncludeBase property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOnlyIncludeBase() {
        return onlyIncludeBase;
    }

    /**
     * Sets the value of the onlyIncludeBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOnlyIncludeBase(Boolean value) {
        this.onlyIncludeBase = value;
    }

}

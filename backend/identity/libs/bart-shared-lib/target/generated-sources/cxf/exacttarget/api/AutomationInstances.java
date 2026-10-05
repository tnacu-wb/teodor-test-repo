
package exacttarget.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AutomationInstances complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AutomationInstances"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InstanceCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="AutomationInstanceCollection" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="AutomationInstance" type="{http://exacttarget.com/wsdl/partnerAPI}AutomationInstance" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutomationInstances", propOrder = {
    "instanceCount",
    "automationInstanceCollection"
})
public class AutomationInstances
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "InstanceCount")
    protected Integer instanceCount;
    @XmlElement(name = "AutomationInstanceCollection")
    protected AutomationInstances.AutomationInstanceCollection automationInstanceCollection;

    /**
     * Gets the value of the instanceCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInstanceCount() {
        return instanceCount;
    }

    /**
     * Sets the value of the instanceCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInstanceCount(Integer value) {
        this.instanceCount = value;
    }

    /**
     * Gets the value of the automationInstanceCollection property.
     * 
     * @return
     *     possible object is
     *     {@link AutomationInstances.AutomationInstanceCollection }
     *     
     */
    public AutomationInstances.AutomationInstanceCollection getAutomationInstanceCollection() {
        return automationInstanceCollection;
    }

    /**
     * Sets the value of the automationInstanceCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutomationInstances.AutomationInstanceCollection }
     *     
     */
    public void setAutomationInstanceCollection(AutomationInstances.AutomationInstanceCollection value) {
        this.automationInstanceCollection = value;
    }


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
     *         &lt;element name="AutomationInstance" type="{http://exacttarget.com/wsdl/partnerAPI}AutomationInstance" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "automationInstance"
    })
    public static class AutomationInstanceCollection
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "AutomationInstance")
        protected List<AutomationInstance> automationInstance;

        /**
         * Gets the value of the automationInstance property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the automationInstance property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getAutomationInstance().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AutomationInstance }
         * </p>
         * 
         * 
         * @return
         *     The value of the automationInstance property.
         */
        public List<AutomationInstance> getAutomationInstance() {
            if (automationInstance == null) {
                automationInstance = new ArrayList<>();
            }
            return this.automationInstance;
        }

    }

}

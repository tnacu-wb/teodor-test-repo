
package exacttarget.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AutomationTaskInstance complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AutomationTaskInstance"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}AutomationTask"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="StepDefinition" type="{http://exacttarget.com/wsdl/partnerAPI}AutomationTask" minOccurs="0"/&gt;
 *         &lt;element name="AutomationInstance" type="{http://exacttarget.com/wsdl/partnerAPI}AutomationInstance" minOccurs="0"/&gt;
 *         &lt;element name="ActivityInstances" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="ActivityInstance" type="{http://exacttarget.com/wsdl/partnerAPI}AutomationActivityInstance" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "AutomationTaskInstance", propOrder = {
    "stepDefinition",
    "automationInstance",
    "activityInstances"
})
public class AutomationTaskInstance
    extends AutomationTask
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "StepDefinition")
    protected AutomationTask stepDefinition;
    @XmlElement(name = "AutomationInstance")
    protected AutomationInstance automationInstance;
    @XmlElement(name = "ActivityInstances")
    protected AutomationTaskInstance.ActivityInstances activityInstances;

    /**
     * Gets the value of the stepDefinition property.
     * 
     * @return
     *     possible object is
     *     {@link AutomationTask }
     *     
     */
    public AutomationTask getStepDefinition() {
        return stepDefinition;
    }

    /**
     * Sets the value of the stepDefinition property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutomationTask }
     *     
     */
    public void setStepDefinition(AutomationTask value) {
        this.stepDefinition = value;
    }

    /**
     * Gets the value of the automationInstance property.
     * 
     * @return
     *     possible object is
     *     {@link AutomationInstance }
     *     
     */
    public AutomationInstance getAutomationInstance() {
        return automationInstance;
    }

    /**
     * Sets the value of the automationInstance property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutomationInstance }
     *     
     */
    public void setAutomationInstance(AutomationInstance value) {
        this.automationInstance = value;
    }

    /**
     * Gets the value of the activityInstances property.
     * 
     * @return
     *     possible object is
     *     {@link AutomationTaskInstance.ActivityInstances }
     *     
     */
    public AutomationTaskInstance.ActivityInstances getActivityInstances() {
        return activityInstances;
    }

    /**
     * Sets the value of the activityInstances property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutomationTaskInstance.ActivityInstances }
     *     
     */
    public void setActivityInstances(AutomationTaskInstance.ActivityInstances value) {
        this.activityInstances = value;
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
     *         &lt;element name="ActivityInstance" type="{http://exacttarget.com/wsdl/partnerAPI}AutomationActivityInstance" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "activityInstance"
    })
    public static class ActivityInstances
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "ActivityInstance")
        protected List<AutomationActivityInstance> activityInstance;

        /**
         * Gets the value of the activityInstance property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the activityInstance property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getActivityInstance().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AutomationActivityInstance }
         * </p>
         * 
         * 
         * @return
         *     The value of the activityInstance property.
         */
        public List<AutomationActivityInstance> getActivityInstance() {
            if (activityInstance == null) {
                activityInstance = new ArrayList<>();
            }
            return this.activityInstance;
        }

    }

}

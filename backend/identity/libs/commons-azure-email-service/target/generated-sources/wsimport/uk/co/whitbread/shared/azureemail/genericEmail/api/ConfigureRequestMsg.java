
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Options" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ConfigureOptions" minOccurs="0"/>
 *         <element name="Action" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="Configurations" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="Configuration" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject" maxOccurs="unbounded" minOccurs="0"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "options",
    "action",
    "configurations"
})
@XmlRootElement(name = "ConfigureRequestMsg")
public class ConfigureRequestMsg
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Options")
    protected ConfigureOptions options;
    @XmlElement(name = "Action")
    protected String action;
    @XmlElement(name = "Configurations")
    protected ConfigureRequestMsg.Configurations configurations;

    /**
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link ConfigureOptions }
     *     
     */
    public ConfigureOptions getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfigureOptions }
     *     
     */
    public void setOptions(ConfigureOptions value) {
        this.options = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    /**
     * Gets the value of the configurations property.
     * 
     * @return
     *     possible object is
     *     {@link ConfigureRequestMsg.Configurations }
     *     
     */
    public ConfigureRequestMsg.Configurations getConfigurations() {
        return configurations;
    }

    /**
     * Sets the value of the configurations property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfigureRequestMsg.Configurations }
     *     
     */
    public void setConfigurations(ConfigureRequestMsg.Configurations value) {
        this.configurations = value;
    }


    /**
     * <p>Java class for anonymous complex type</p>.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.</p>
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="Configuration" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject" maxOccurs="unbounded" minOccurs="0"/>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "configuration"
    })
    public static class Configurations
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "Configuration")
        protected List<APIObject> configuration;

        /**
         * Gets the value of the configuration property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the configuration property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getConfiguration().add(newItem);
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
         *     The value of the configuration property.
         */
        public List<APIObject> getConfiguration() {
            if (configuration == null) {
                configuration = new ArrayList<>();
            }
            return this.configuration;
        }

    }

}

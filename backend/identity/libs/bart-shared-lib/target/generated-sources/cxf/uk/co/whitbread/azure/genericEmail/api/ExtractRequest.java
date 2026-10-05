
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ExtractRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ExtractRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Request"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Client" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ClientID" minOccurs="0"/&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Options" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ExtractOptions"/&gt;
 *         &lt;element name="Parameters" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Parameter" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ExtractParameter" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="Description" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ExtractDescription"/&gt;
 *           &lt;element name="Definition" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ExtractDefinition"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtractRequest", propOrder = {
    "client",
    "id",
    "options",
    "parameters",
    "description",
    "definition"
})
public class ExtractRequest
    extends Request
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Client")
    protected ClientID client;
    @XmlElement(name = "ID", required = true)
    protected String id;
    @XmlElement(name = "Options", required = true)
    protected ExtractOptions options;
    @XmlElement(name = "Parameters")
    protected ExtractRequest.Parameters parameters;
    @XmlElement(name = "Description")
    protected ExtractDescription description;
    @XmlElement(name = "Definition")
    protected ExtractDefinition definition;

    /**
     * Gets the value of the client property.
     * 
     * @return
     *     possible object is
     *     {@link ClientID }
     *     
     */
    public ClientID getClient() {
        return client;
    }

    /**
     * Sets the value of the client property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientID }
     *     
     */
    public void setClient(ClientID value) {
        this.client = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link ExtractOptions }
     *     
     */
    public ExtractOptions getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtractOptions }
     *     
     */
    public void setOptions(ExtractOptions value) {
        this.options = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link ExtractRequest.Parameters }
     *     
     */
    public ExtractRequest.Parameters getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtractRequest.Parameters }
     *     
     */
    public void setParameters(ExtractRequest.Parameters value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link ExtractDescription }
     *     
     */
    public ExtractDescription getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtractDescription }
     *     
     */
    public void setDescription(ExtractDescription value) {
        this.description = value;
    }

    /**
     * Gets the value of the definition property.
     * 
     * @return
     *     possible object is
     *     {@link ExtractDefinition }
     *     
     */
    public ExtractDefinition getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtractDefinition }
     *     
     */
    public void setDefinition(ExtractDefinition value) {
        this.definition = value;
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
     *         &lt;element name="Parameter" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ExtractParameter" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "parameter"
    })
    public static class Parameters
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "Parameter")
        protected List<ExtractParameter> parameter;

        /**
         * Gets the value of the parameter property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the parameter property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getParameter().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ExtractParameter }
         * </p>
         * 
         * 
         * @return
         *     The value of the parameter property.
         */
        public List<ExtractParameter> getParameter() {
            if (parameter == null) {
                parameter = new ArrayList<>();
            }
            return this.parameter;
        }

    }

}

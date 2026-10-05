
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for Options complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="Options"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Client" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ClientID" minOccurs="0"/&gt;
 *         &lt;element name="SendResponseTo" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}AsyncResponse" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="SaveOptions" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="SaveOption" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SaveOption" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Priority" type="{http://www.w3.org/2001/XMLSchema}byte" minOccurs="0"/&gt;
 *         &lt;element name="ConversationID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SequenceCode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="CallsInConversation" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ScheduledTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="RequestType" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}RequestType" minOccurs="0"/&gt;
 *         &lt;element name="QueuePriority" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Priority" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Options", propOrder = {
    "client",
    "sendResponseTo",
    "saveOptions",
    "priority",
    "conversationID",
    "sequenceCode",
    "callsInConversation",
    "scheduledTime",
    "requestType",
    "queuePriority"
})
@XmlSeeAlso({
    CreateOptions.class,
    UpdateOptions.class,
    DeleteOptions.class,
    ConfigureOptions.class,
    ScheduleOptions.class,
    SystemStatusOptions.class,
    RetrieveSingleOptions.class,
    RetrieveOptions.class,
    PerformOptions.class,
    ExtractOptions.class
})
public abstract class Options
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Client")
    protected ClientID client;
    @XmlElement(name = "SendResponseTo")
    protected List<AsyncResponse> sendResponseTo;
    @XmlElement(name = "SaveOptions")
    protected Options.SaveOptions saveOptions;
    @XmlElement(name = "Priority")
    protected Byte priority;
    @XmlElement(name = "ConversationID")
    protected String conversationID;
    @XmlElement(name = "SequenceCode")
    protected Integer sequenceCode;
    @XmlElement(name = "CallsInConversation")
    protected Integer callsInConversation;
    @XmlElement(name = "ScheduledTime", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime scheduledTime;
    @XmlElement(name = "RequestType")
    @XmlSchemaType(name = "string")
    protected RequestType requestType;
    @XmlElement(name = "QueuePriority")
    @XmlSchemaType(name = "string")
    protected Priority queuePriority;

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
     * Gets the value of the sendResponseTo property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the sendResponseTo property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getSendResponseTo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AsyncResponse }
     * </p>
     * 
     * 
     * @return
     *     The value of the sendResponseTo property.
     */
    public List<AsyncResponse> getSendResponseTo() {
        if (sendResponseTo == null) {
            sendResponseTo = new ArrayList<>();
        }
        return this.sendResponseTo;
    }

    /**
     * Gets the value of the saveOptions property.
     * 
     * @return
     *     possible object is
     *     {@link Options.SaveOptions }
     *     
     */
    public Options.SaveOptions getSaveOptions() {
        return saveOptions;
    }

    /**
     * Sets the value of the saveOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Options.SaveOptions }
     *     
     */
    public void setSaveOptions(Options.SaveOptions value) {
        this.saveOptions = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setPriority(Byte value) {
        this.priority = value;
    }

    /**
     * Gets the value of the conversationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConversationID() {
        return conversationID;
    }

    /**
     * Sets the value of the conversationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConversationID(String value) {
        this.conversationID = value;
    }

    /**
     * Gets the value of the sequenceCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSequenceCode() {
        return sequenceCode;
    }

    /**
     * Sets the value of the sequenceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSequenceCode(Integer value) {
        this.sequenceCode = value;
    }

    /**
     * Gets the value of the callsInConversation property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCallsInConversation() {
        return callsInConversation;
    }

    /**
     * Sets the value of the callsInConversation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCallsInConversation(Integer value) {
        this.callsInConversation = value;
    }

    /**
     * Gets the value of the scheduledTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    /**
     * Sets the value of the scheduledTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScheduledTime(LocalDateTime value) {
        this.scheduledTime = value;
    }

    /**
     * Gets the value of the requestType property.
     * 
     * @return
     *     possible object is
     *     {@link RequestType }
     *     
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * Sets the value of the requestType property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestType }
     *     
     */
    public void setRequestType(RequestType value) {
        this.requestType = value;
    }

    /**
     * Gets the value of the queuePriority property.
     * 
     * @return
     *     possible object is
     *     {@link Priority }
     *     
     */
    public Priority getQueuePriority() {
        return queuePriority;
    }

    /**
     * Sets the value of the queuePriority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Priority }
     *     
     */
    public void setQueuePriority(Priority value) {
        this.queuePriority = value;
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
     *         &lt;element name="SaveOption" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SaveOption" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "saveOption"
    })
    public static class SaveOptions
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "SaveOption")
        protected List<SaveOption> saveOption;

        /**
         * Gets the value of the saveOption property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the saveOption property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getSaveOption().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link SaveOption }
         * </p>
         * 
         * 
         * @return
         *     The value of the saveOption property.
         */
        public List<SaveOption> getSaveOption() {
            if (saveOption == null) {
                saveOption = new ArrayList<>();
            }
            return this.saveOption;
        }

    }

}

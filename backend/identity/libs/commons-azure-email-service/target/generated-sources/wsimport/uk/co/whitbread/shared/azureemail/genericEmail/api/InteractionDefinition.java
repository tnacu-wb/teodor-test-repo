
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InteractionDefinition complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="InteractionDefinition">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}InteractionBaseObject">
 *       <sequence>
 *         <element name="InteractionObjectID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InteractionDefinition", propOrder = {
    "interactionObjectID"
})
@XmlSeeAlso({
    Campaign.class,
    SendDefinition.class,
    SalesforceSendActivity.class,
    DataExtractActivity.class,
    MessageSendActivity.class,
    SmsSendActivity.class,
    MobileConnectRefreshListActivity.class,
    MobileConnectSendSmsActivity.class,
    MobilePushSendMessageActivity.class,
    ReportActivity.class,
    ImportDefinition.class,
    FilterActivity.class,
    GroupDefinition.class,
    GroupConnectActivity.class,
    FileTransferActivity.class,
    QueryDefinition.class,
    HiveQueryDefinition.class,
    Automation.class,
    AutomationChain.class
})
public class InteractionDefinition
    extends InteractionBaseObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "InteractionObjectID")
    protected String interactionObjectID;

    /**
     * Gets the value of the interactionObjectID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInteractionObjectID() {
        return interactionObjectID;
    }

    /**
     * Sets the value of the interactionObjectID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInteractionObjectID(String value) {
        this.interactionObjectID = value;
    }

}

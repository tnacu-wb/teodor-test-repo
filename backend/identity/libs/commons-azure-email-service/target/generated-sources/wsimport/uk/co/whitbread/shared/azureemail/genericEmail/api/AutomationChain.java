
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AutomationChain complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="AutomationChain">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}InteractionDefinition">
 *       <sequence>
 *         <element name="AutomationToChainID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutomationChain", propOrder = {
    "automationToChainID"
})
public class AutomationChain
    extends InteractionDefinition
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "AutomationToChainID")
    protected String automationToChainID;

    /**
     * Gets the value of the automationToChainID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAutomationToChainID() {
        return automationToChainID;
    }

    /**
     * Sets the value of the automationToChainID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAutomationToChainID(String value) {
        this.automationToChainID = value;
    }

}

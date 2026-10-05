
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
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
 *         <element name="in0" type="{https://dto.email.transact.comms.int.wtbapi.com}PromptEmail"/>
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
    "in0"
})
@XmlRootElement(name = "sendPromptEmailForBkr", namespace = "https://email.transact.comms.int.wtbapi.com")
public class SendPromptEmailForBkr
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(namespace = "https://email.transact.comms.int.wtbapi.com", required = true, nillable = true)
    protected PromptEmail in0;

    /**
     * Gets the value of the in0 property.
     * 
     * @return
     *     possible object is
     *     {@link PromptEmail }
     *     
     */
    public PromptEmail getIn0() {
        return in0;
    }

    /**
     * Sets the value of the in0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link PromptEmail }
     *     
     */
    public void setIn0(PromptEmail value) {
        this.in0 = value;
    }

}

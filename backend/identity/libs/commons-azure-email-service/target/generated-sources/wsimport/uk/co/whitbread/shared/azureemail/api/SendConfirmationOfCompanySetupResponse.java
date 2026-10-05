
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
 *         <element name="out" type="{https://dto.email.transact.comms.int.wtbapi.com}GenericServiceResponse"/>
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
    "out"
})
@XmlRootElement(name = "sendConfirmationOfCompanySetupResponse", namespace = "https://email.transact.comms.int.wtbapi.com")
public class SendConfirmationOfCompanySetupResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(namespace = "https://email.transact.comms.int.wtbapi.com", required = true, nillable = true)
    protected GenericServiceResponse out;

    /**
     * Gets the value of the out property.
     * 
     * @return
     *     possible object is
     *     {@link GenericServiceResponse }
     *     
     */
    public GenericServiceResponse getOut() {
        return out;
    }

    /**
     * Sets the value of the out property.
     * 
     * @param value
     *     allowed object is
     *     {@link GenericServiceResponse }
     *     
     */
    public void setOut(GenericServiceResponse value) {
        this.out = value;
    }

}

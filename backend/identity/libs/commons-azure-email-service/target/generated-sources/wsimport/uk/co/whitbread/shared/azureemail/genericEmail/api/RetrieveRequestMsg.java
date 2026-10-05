
package uk.co.whitbread.shared.azureemail.genericEmail.api;

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
 *         <element name="RetrieveRequest" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}RetrieveRequest"/>
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
    "retrieveRequest"
})
@XmlRootElement(name = "RetrieveRequestMsg")
public class RetrieveRequestMsg
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "RetrieveRequest", required = true)
    protected RetrieveRequest retrieveRequest;

    /**
     * Gets the value of the retrieveRequest property.
     * 
     * @return
     *     possible object is
     *     {@link RetrieveRequest }
     *     
     */
    public RetrieveRequest getRetrieveRequest() {
        return retrieveRequest;
    }

    /**
     * Sets the value of the retrieveRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link RetrieveRequest }
     *     
     */
    public void setRetrieveRequest(RetrieveRequest value) {
        this.retrieveRequest = value;
    }

}

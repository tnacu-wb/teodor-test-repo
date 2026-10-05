
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
 *         <element name="DescribeRequests" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ArrayOfObjectDefinitionRequest" minOccurs="0"/>
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
    "describeRequests"
})
@XmlRootElement(name = "DefinitionRequestMsg")
public class DefinitionRequestMsg
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "DescribeRequests")
    protected ArrayOfObjectDefinitionRequest describeRequests;

    /**
     * Gets the value of the describeRequests property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfObjectDefinitionRequest }
     *     
     */
    public ArrayOfObjectDefinitionRequest getDescribeRequests() {
        return describeRequests;
    }

    /**
     * Sets the value of the describeRequests property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfObjectDefinitionRequest }
     *     
     */
    public void setDescribeRequests(ArrayOfObjectDefinitionRequest value) {
        this.describeRequests = value;
    }

}

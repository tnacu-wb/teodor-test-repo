
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


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
 *         &lt;element name="DescribeRequests" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ArrayOfObjectDefinitionRequest" minOccurs="0"/&gt;
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


package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContainerID complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="ContainerID">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="APIObject" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContainerID", propOrder = {
    "apiObject"
})
public class ContainerID
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "APIObject")
    protected APIObject apiObject;

    /**
     * Gets the value of the apiObject property.
     * 
     * @return
     *     possible object is
     *     {@link APIObject }
     *     
     */
    public APIObject getAPIObject() {
        return apiObject;
    }

    /**
     * Sets the value of the apiObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link APIObject }
     *     
     */
    public void setAPIObject(APIObject value) {
        this.apiObject = value;
    }

}

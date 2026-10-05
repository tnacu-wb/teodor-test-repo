
package uk.co.whitbread.bart.registeredguest.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="getUserRequest" type="{http://bartws.micros.com/1.13}GetUserRequest" minOccurs="0"/&gt;
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
    "getUserRequest"
})
@XmlRootElement(name = "GetUser")
public class GetUser
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected GetUserRequest getUserRequest;

    /**
     * Gets the value of the getUserRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetUserRequest }
     *     
     */
    public GetUserRequest getGetUserRequest() {
        return getUserRequest;
    }

    /**
     * Sets the value of the getUserRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetUserRequest }
     *     
     */
    public void setGetUserRequest(GetUserRequest value) {
        this.getUserRequest = value;
    }

}

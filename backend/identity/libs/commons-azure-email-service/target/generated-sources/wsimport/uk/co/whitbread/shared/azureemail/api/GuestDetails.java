
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GuestDetails complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="GuestDetails">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="guestForename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="guestSurname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="guestTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuestDetails", propOrder = {
    "guestForename",
    "guestSurname",
    "guestTitle"
})
public class GuestDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected String guestForename;
    @XmlElement(nillable = true)
    protected String guestSurname;
    @XmlElement(nillable = true)
    protected String guestTitle;

    /**
     * Gets the value of the guestForename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuestForename() {
        return guestForename;
    }

    /**
     * Sets the value of the guestForename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuestForename(String value) {
        this.guestForename = value;
    }

    /**
     * Gets the value of the guestSurname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuestSurname() {
        return guestSurname;
    }

    /**
     * Sets the value of the guestSurname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuestSurname(String value) {
        this.guestSurname = value;
    }

    /**
     * Gets the value of the guestTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuestTitle() {
        return guestTitle;
    }

    /**
     * Sets the value of the guestTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuestTitle(String value) {
        this.guestTitle = value;
    }

}

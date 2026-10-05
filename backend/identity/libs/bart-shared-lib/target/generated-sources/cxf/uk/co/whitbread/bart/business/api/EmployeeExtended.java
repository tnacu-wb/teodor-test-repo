
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for EmployeeExtended complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="EmployeeExtended"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://corporate.micros.com/1.0}Employee"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ghCreation" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="totalStays" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EmployeeExtended", propOrder = {
    "ghCreation",
    "totalStays"
})
public class EmployeeExtended
    extends Employee
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate ghCreation;
    protected Long totalStays;

    /**
     * Gets the value of the ghCreation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getGhCreation() {
        return ghCreation;
    }

    /**
     * Sets the value of the ghCreation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGhCreation(LocalDate value) {
        this.ghCreation = value;
    }

    /**
     * Gets the value of the totalStays property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotalStays() {
        return totalStays;
    }

    /**
     * Sets the value of the totalStays property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotalStays(Long value) {
        this.totalStays = value;
    }

}

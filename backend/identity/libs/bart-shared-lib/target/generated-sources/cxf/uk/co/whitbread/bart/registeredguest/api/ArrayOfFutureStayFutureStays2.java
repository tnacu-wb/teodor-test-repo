
package uk.co.whitbread.bart.registeredguest.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfFutureStayFutureStays2 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfFutureStayFutureStays2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FutureStay" type="{http://bartws.micros.com/1.13}FutureStays2" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFutureStayFutureStays2", propOrder = {
    "futureStay"
})
public class ArrayOfFutureStayFutureStays2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "FutureStay", nillable = true)
    protected List<FutureStays2> futureStay;

    /**
     * Gets the value of the futureStay property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the futureStay property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getFutureStay().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FutureStays2 }
     * </p>
     * 
     * 
     * @return
     *     The value of the futureStay property.
     */
    public List<FutureStays2> getFutureStay() {
        if (futureStay == null) {
            futureStay = new ArrayList<>();
        }
        return this.futureStay;
    }

}

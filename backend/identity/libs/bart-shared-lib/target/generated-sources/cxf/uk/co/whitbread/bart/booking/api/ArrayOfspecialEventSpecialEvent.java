
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfspecialEventSpecialEvent complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfspecialEventSpecialEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="specialEvent" type="{http://bartws.micros.com/1.31}SpecialEvent" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfspecialEventSpecialEvent", propOrder = {
    "specialEvent"
})
public class ArrayOfspecialEventSpecialEvent
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected List<SpecialEvent> specialEvent;

    /**
     * Gets the value of the specialEvent property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the specialEvent property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getSpecialEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpecialEvent }
     * </p>
     * 
     * 
     * @return
     *     The value of the specialEvent property.
     */
    public List<SpecialEvent> getSpecialEvent() {
        if (specialEvent == null) {
            specialEvent = new ArrayList<>();
        }
        return this.specialEvent;
    }

}

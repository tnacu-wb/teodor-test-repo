
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfDinner complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="ArrayOfDinner">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Dinner" type="{https://dto.email.transact.comms.int.wtbapi.com}Dinner" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfDinner", propOrder = {
    "dinner"
})
public class ArrayOfDinner
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Dinner", nillable = true)
    protected List<Dinner> dinner;

    /**
     * Gets the value of the dinner property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dinner property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getDinner().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dinner }
     * </p>
     * 
     * 
     * @return
     *     The value of the dinner property.
     */
    public List<Dinner> getDinner() {
        if (dinner == null) {
            dinner = new ArrayList<>();
        }
        return this.dinner;
    }

}

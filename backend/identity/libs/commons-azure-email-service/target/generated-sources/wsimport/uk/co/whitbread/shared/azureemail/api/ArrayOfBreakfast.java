
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfBreakfast complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="ArrayOfBreakfast">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Breakfast" type="{https://dto.email.transact.comms.int.wtbapi.com}Breakfast" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfBreakfast", propOrder = {
    "breakfast"
})
public class ArrayOfBreakfast
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Breakfast", nillable = true)
    protected List<Breakfast> breakfast;

    /**
     * Gets the value of the breakfast property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the breakfast property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getBreakfast().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Breakfast }
     * </p>
     * 
     * 
     * @return
     *     The value of the breakfast property.
     */
    public List<Breakfast> getBreakfast() {
        if (breakfast == null) {
            breakfast = new ArrayList<>();
        }
        return this.breakfast;
    }

}

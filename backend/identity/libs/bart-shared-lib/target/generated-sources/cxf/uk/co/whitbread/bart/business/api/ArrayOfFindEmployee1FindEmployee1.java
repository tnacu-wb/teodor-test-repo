
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfFindEmployee1FindEmployee1 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfFindEmployee1FindEmployee1"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FindEmployee1" type="{http://corporate.micros.com/1.0}FindEmployee1" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFindEmployee1FindEmployee1", propOrder = {
    "findEmployee1"
})
public class ArrayOfFindEmployee1FindEmployee1
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "FindEmployee1", nillable = true)
    protected List<FindEmployee1> findEmployee1;

    /**
     * Gets the value of the findEmployee1 property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the findEmployee1 property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getFindEmployee1().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FindEmployee1 }
     * </p>
     * 
     * 
     * @return
     *     The value of the findEmployee1 property.
     */
    public List<FindEmployee1> getFindEmployee1() {
        if (findEmployee1 == null) {
            findEmployee1 = new ArrayList<>();
        }
        return this.findEmployee1;
    }

}

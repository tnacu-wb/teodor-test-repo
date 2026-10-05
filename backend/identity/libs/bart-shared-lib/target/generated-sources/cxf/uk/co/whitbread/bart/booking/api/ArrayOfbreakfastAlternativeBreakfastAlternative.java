
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfbreakfastAlternativeBreakfastAlternative complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfbreakfastAlternativeBreakfastAlternative"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="breakfastAlternative" type="{http://bartws.micros.com/1.31}BreakfastAlternative" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfbreakfastAlternativeBreakfastAlternative", propOrder = {
    "breakfastAlternative"
})
public class ArrayOfbreakfastAlternativeBreakfastAlternative
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected List<BreakfastAlternative> breakfastAlternative;

    /**
     * Gets the value of the breakfastAlternative property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the breakfastAlternative property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getBreakfastAlternative().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BreakfastAlternative }
     * </p>
     * 
     * 
     * @return
     *     The value of the breakfastAlternative property.
     */
    public List<BreakfastAlternative> getBreakfastAlternative() {
        if (breakfastAlternative == null) {
            breakfastAlternative = new ArrayList<>();
        }
        return this.breakfastAlternative;
    }

}

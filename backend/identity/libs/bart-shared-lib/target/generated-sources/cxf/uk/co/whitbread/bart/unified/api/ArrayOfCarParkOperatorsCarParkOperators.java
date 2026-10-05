
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfCarParkOperatorsCarParkOperators complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfCarParkOperatorsCarParkOperators"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CarParkOperators" type="{http://bartws.micros.com/1.17}CarParkOperators" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCarParkOperatorsCarParkOperators", propOrder = {
    "carParkOperators"
})
public class ArrayOfCarParkOperatorsCarParkOperators
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "CarParkOperators", nillable = true)
    protected List<CarParkOperators> carParkOperators;

    /**
     * Gets the value of the carParkOperators property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the carParkOperators property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCarParkOperators().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CarParkOperators }
     * </p>
     * 
     * 
     * @return
     *     The value of the carParkOperators property.
     */
    public List<CarParkOperators> getCarParkOperators() {
        if (carParkOperators == null) {
            carParkOperators = new ArrayList<>();
        }
        return this.carParkOperators;
    }

}


package uk.co.whitbread.bart.business.auth0.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfUDFAnswers complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfUDFAnswers"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="UDFAnswers" type="{http://corporate.micros.com/1.0}UDFAnswers2" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfUDFAnswers", propOrder = {
    "udfAnswers"
})
public class ArrayOfUDFAnswers
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "UDFAnswers", nillable = true)
    protected List<UDFAnswers2> udfAnswers;

    /**
     * Gets the value of the udfAnswers property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the udfAnswers property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getUDFAnswers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UDFAnswers2 }
     * </p>
     * 
     * 
     * @return
     *     The value of the udfAnswers property.
     */
    public List<UDFAnswers2> getUDFAnswers() {
        if (udfAnswers == null) {
            udfAnswers = new ArrayList<>();
        }
        return this.udfAnswers;
    }

}

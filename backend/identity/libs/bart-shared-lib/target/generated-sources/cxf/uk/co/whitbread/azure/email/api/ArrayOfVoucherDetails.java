
package uk.co.whitbread.azure.email.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ArrayOfVoucherDetails complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ArrayOfVoucherDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="VoucherDetails" type="{https://dto.email.transact.comms.int.wtbapi.com}VoucherDetails" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfVoucherDetails", propOrder = {
    "voucherDetails"
})
public class ArrayOfVoucherDetails
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "VoucherDetails", nillable = true)
    protected List<VoucherDetails> voucherDetails;

    /**
     * Gets the value of the voucherDetails property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the voucherDetails property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getVoucherDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VoucherDetails }
     * </p>
     * 
     * 
     * @return
     *     The value of the voucherDetails property.
     */
    public List<VoucherDetails> getVoucherDetails() {
        if (voucherDetails == null) {
            voucherDetails = new ArrayList<>();
        }
        return this.voucherDetails;
    }

}


package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CampaignPerformOptions complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CampaignPerformOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}PerformOptions"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OccurrenceIDs" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="OccurrenceIDsIndex" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CampaignPerformOptions", propOrder = {
    "occurrenceIDs",
    "occurrenceIDsIndex"
})
public class CampaignPerformOptions
    extends PerformOptions
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "OccurrenceIDs")
    protected List<String> occurrenceIDs;
    @XmlElement(name = "OccurrenceIDsIndex")
    protected Integer occurrenceIDsIndex;

    /**
     * Gets the value of the occurrenceIDs property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the occurrenceIDs property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getOccurrenceIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * </p>
     * 
     * 
     * @return
     *     The value of the occurrenceIDs property.
     */
    public List<String> getOccurrenceIDs() {
        if (occurrenceIDs == null) {
            occurrenceIDs = new ArrayList<>();
        }
        return this.occurrenceIDs;
    }

    /**
     * Gets the value of the occurrenceIDsIndex property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOccurrenceIDsIndex() {
        return occurrenceIDsIndex;
    }

    /**
     * Sets the value of the occurrenceIDsIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOccurrenceIDsIndex(Integer value) {
        this.occurrenceIDsIndex = value;
    }

}

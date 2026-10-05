
package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PagingResultType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PagingResultType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FromRecord" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="ToRecord" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="TotalRecordCount" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="LastPage" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PagingResultType", propOrder = {
    "fromRecord",
    "toRecord",
    "totalRecordCount",
    "lastPage"
})
public class PagingResultType {

    /**
     * The first record number returned from this search
     * 
     */
    @XmlElement(name = "FromRecord")
    protected int fromRecord;
    /**
     * The last record number returned from this seach
     * 
     */
    @XmlElement(name = "ToRecord")
    protected int toRecord;
    /**
     * The total records returned from this sesrch
     * 
     */
    @XmlElement(name = "TotalRecordCount")
    protected int totalRecordCount;
    /**
     * The last possible page to display
     * 
     */
    @XmlElement(name = "LastPage")
    protected int lastPage;

    /**
     * The first record number returned from this search
     * 
     */
    public int getFromRecord() {
        return fromRecord;
    }

    /**
     * Sets the value of the fromRecord property.
     * 
     */
    public void setFromRecord(int value) {
        this.fromRecord = value;
    }

    /**
     * The last record number returned from this seach
     * 
     */
    public int getToRecord() {
        return toRecord;
    }

    /**
     * Sets the value of the toRecord property.
     * 
     */
    public void setToRecord(int value) {
        this.toRecord = value;
    }

    /**
     * The total records returned from this sesrch
     * 
     */
    public int getTotalRecordCount() {
        return totalRecordCount;
    }

    /**
     * Sets the value of the totalRecordCount property.
     * 
     */
    public void setTotalRecordCount(int value) {
        this.totalRecordCount = value;
    }

    /**
     * The last possible page to display
     * 
     */
    public int getLastPage() {
        return lastPage;
    }

    /**
     * Sets the value of the lastPage property.
     * 
     */
    public void setLastPage(int value) {
        this.lastPage = value;
    }

}


package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PagingRequestWithoutSortType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PagingRequestWithoutSortType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Page" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="MaximumDisplayRows" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PagingRequestWithoutSortType", propOrder = {
    "page",
    "maximumDisplayRows"
})
public class PagingRequestWithoutSortType {

    /**
     * The page to be returned
     * 
     */
    @XmlElement(name = "Page")
    protected int page;
    /**
     * The maximum amount of rows to be returned
     * 
     */
    @XmlElement(name = "MaximumDisplayRows")
    protected int maximumDisplayRows;

    /**
     * The page to be returned
     * 
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     * 
     */
    public void setPage(int value) {
        this.page = value;
    }

    /**
     * The maximum amount of rows to be returned
     * 
     */
    public int getMaximumDisplayRows() {
        return maximumDisplayRows;
    }

    /**
     * Sets the value of the maximumDisplayRows property.
     * 
     */
    public void setMaximumDisplayRows(int value) {
        this.maximumDisplayRows = value;
    }

}

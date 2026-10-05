
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PagingRequestType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PagingRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Page" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="MaximumDisplayRows" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="SortOrderAsc" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="SortColumn"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PagingRequestType", propOrder = {
    "page",
    "maximumDisplayRows",
    "sortOrderAsc",
    "sortColumn"
})
public class PagingRequestType {

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
    @XmlElement(name = "SortOrderAsc")
    protected boolean sortOrderAsc;
    @XmlElement(name = "SortColumn", required = true)
    protected String sortColumn;

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

    /**
     * Gets the value of the sortOrderAsc property.
     * 
     */
    public boolean isSortOrderAsc() {
        return sortOrderAsc;
    }

    /**
     * Sets the value of the sortOrderAsc property.
     * 
     */
    public void setSortOrderAsc(boolean value) {
        this.sortOrderAsc = value;
    }

    /**
     * Gets the value of the sortColumn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortColumn() {
        return sortColumn;
    }

    /**
     * Sets the value of the sortColumn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortColumn(String value) {
        this.sortColumn = value;
    }

}

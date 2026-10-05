
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="IncludeVersionHistory" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "includeVersionHistory"
})
@XmlRootElement(name = "VersionInfoRequestMsg")
public class VersionInfoRequestMsg
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "IncludeVersionHistory")
    protected boolean includeVersionHistory;

    /**
     * Gets the value of the includeVersionHistory property.
     * 
     */
    public boolean isIncludeVersionHistory() {
        return includeVersionHistory;
    }

    /**
     * Sets the value of the includeVersionHistory property.
     * 
     */
    public void setIncludeVersionHistory(boolean value) {
        this.includeVersionHistory = value;
    }

}


package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountInvoiceDownloadResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountInvoiceDownloadResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FileDownloadResult" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}FileDownloadResultType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountInvoiceDownloadResponseType", propOrder = {
    "fileDownloadResult"
})
public class CustomerAccountInvoiceDownloadResponseType
    extends ResponseType
{

    @XmlElement(name = "FileDownloadResult", required = true)
    protected FileDownloadResultType fileDownloadResult;

    /**
     * Gets the value of the fileDownloadResult property.
     * 
     * @return
     *     possible object is
     *     {@link FileDownloadResultType }
     *     
     */
    public FileDownloadResultType getFileDownloadResult() {
        return fileDownloadResult;
    }

    /**
     * Sets the value of the fileDownloadResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileDownloadResultType }
     *     
     */
    public void setFileDownloadResult(FileDownloadResultType value) {
        this.fileDownloadResult = value;
    }

}


package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CNPHostedPageInitiateResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CNPHostedPageInitiateResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CNPSessionId" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType" minOccurs="0"/&gt;
 *         &lt;element name="HostedPageUrl" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType" minOccurs="0"/&gt;
 *         &lt;element name="SchemeID" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CNPHostedPageInitiateResponseType", propOrder = {
    "cnpSessionId",
    "hostedPageUrl",
    "schemeID"
})
public class CNPHostedPageInitiateResponseType
    extends ResponseType
{

    /**
     * (e.g. c0cc0ff5-b05d-43ce-86be-c5d6c85e8ea2)
     * 
     */
    @XmlElement(name = "CNPSessionId")
    protected String cnpSessionId;
    /**
     * Full Hosted Page URL including cnpSessionID (e.g. https://cnp.atosb2b.com/hostedpage.aspx?cnpsessionId=c0cc0ff5-b05d-43ce-86be-c5d6c85e8ea2)
     * 
     */
    @XmlElement(name = "HostedPageUrl")
    protected String hostedPageUrl;
    @XmlElement(name = "SchemeID")
    protected Object schemeID;

    /**
     * (e.g. c0cc0ff5-b05d-43ce-86be-c5d6c85e8ea2)
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCNPSessionId() {
        return cnpSessionId;
    }

    /**
     * Sets the value of the cnpSessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getCNPSessionId()
     */
    public void setCNPSessionId(String value) {
        this.cnpSessionId = value;
    }

    /**
     * Full Hosted Page URL including cnpSessionID (e.g. https://cnp.atosb2b.com/hostedpage.aspx?cnpsessionId=c0cc0ff5-b05d-43ce-86be-c5d6c85e8ea2)
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostedPageUrl() {
        return hostedPageUrl;
    }

    /**
     * Sets the value of the hostedPageUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getHostedPageUrl()
     */
    public void setHostedPageUrl(String value) {
        this.hostedPageUrl = value;
    }

    /**
     * Gets the value of the schemeID property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getSchemeID() {
        return schemeID;
    }

    /**
     * Sets the value of the schemeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setSchemeID(Object value) {
        this.schemeID = value;
    }

}

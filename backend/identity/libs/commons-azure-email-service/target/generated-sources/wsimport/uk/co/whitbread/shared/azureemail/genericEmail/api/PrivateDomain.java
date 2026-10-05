
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrivateDomain complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="PrivateDomain">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject">
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrivateDomain")
public class PrivateDomain
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;

}

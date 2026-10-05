
package exacttarget.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for SubscriberAddressStatus&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="SubscriberAddressStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="OptedIn"/&gt;
 *     &lt;enumeration value="OptedOut"/&gt;
 *     &lt;enumeration value="InActive"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "SubscriberAddressStatus")
@XmlEnum
public enum SubscriberAddressStatus {

    @XmlEnumValue("OptedIn")
    OPTED_IN("OptedIn"),
    @XmlEnumValue("OptedOut")
    OPTED_OUT("OptedOut"),
    @XmlEnumValue("InActive")
    IN_ACTIVE("InActive");
    private final String value;

    SubscriberAddressStatus(String v) {
        value = v;
    }

    /**
     * Gets the value associated to the enum constant.
     * 
     * @return
     *     The value linked to the enum.
     */
    public String value() {
        return value;
    }

    /**
     * Gets the enum associated to the value passed as parameter.
     * 
     * @param v
     *     The value to get the enum from.
     * @return
     *     The enum which corresponds to the value, if it exists.
     * @throws IllegalArgumentException
     *     If no value matches in the enum declaration.
     */
    public static SubscriberAddressStatus fromValue(String v) {
        for (SubscriberAddressStatus c: SubscriberAddressStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}


package uk.co.whitbread.azure.genericEmail.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for UnsubscribeBehaviorEnum&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="UnsubscribeBehaviorEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ENTIRE_ENTERPRISE"/&gt;
 *     &lt;enumeration value="BUSINESS_UNIT_ONLY"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "UnsubscribeBehaviorEnum")
@XmlEnum
public enum UnsubscribeBehaviorEnum {

    ENTIRE_ENTERPRISE,
    BUSINESS_UNIT_ONLY;

    public String value() {
        return name();
    }

    public static UnsubscribeBehaviorEnum fromValue(String v) {
        return valueOf(v);
    }

}

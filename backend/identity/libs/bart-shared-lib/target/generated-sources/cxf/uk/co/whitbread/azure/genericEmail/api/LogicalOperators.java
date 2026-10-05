
package uk.co.whitbread.azure.genericEmail.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for LogicalOperators&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="LogicalOperators"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="OR"/&gt;
 *     &lt;enumeration value="AND"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "LogicalOperators")
@XmlEnum
public enum LogicalOperators {

    OR,
    AND;

    public String value() {
        return name();
    }

    public static LogicalOperators fromValue(String v) {
        return valueOf(v);
    }

}

package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * OhipBadRequestExceptionCauseStackTrace
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OhipBadRequestExceptionCauseStackTrace {

  private @Nullable String classLoaderName;

  private @Nullable String className;

  private @Nullable String fileName;

  private @Nullable Integer lineNumber;

  private @Nullable String methodName;

  private @Nullable String moduleName;

  private @Nullable String moduleVersion;

  private @Nullable Boolean nativeMethod;

  public OhipBadRequestExceptionCauseStackTrace classLoaderName(String classLoaderName) {
    this.classLoaderName = classLoaderName;
    return this;
  }

  /**
   * Get classLoaderName
   * @return classLoaderName
   */
  
  @Schema(name = "classLoaderName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("classLoaderName")
  public String getClassLoaderName() {
    return classLoaderName;
  }

  public void setClassLoaderName(String classLoaderName) {
    this.classLoaderName = classLoaderName;
  }

  public OhipBadRequestExceptionCauseStackTrace className(String className) {
    this.className = className;
    return this;
  }

  /**
   * Get className
   * @return className
   */
  
  @Schema(name = "className", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("className")
  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public OhipBadRequestExceptionCauseStackTrace fileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  /**
   * Get fileName
   * @return fileName
   */
  
  @Schema(name = "fileName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fileName")
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public OhipBadRequestExceptionCauseStackTrace lineNumber(Integer lineNumber) {
    this.lineNumber = lineNumber;
    return this;
  }

  /**
   * Get lineNumber
   * @return lineNumber
   */
  
  @Schema(name = "lineNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lineNumber")
  public Integer getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(Integer lineNumber) {
    this.lineNumber = lineNumber;
  }

  public OhipBadRequestExceptionCauseStackTrace methodName(String methodName) {
    this.methodName = methodName;
    return this;
  }

  /**
   * Get methodName
   * @return methodName
   */
  
  @Schema(name = "methodName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("methodName")
  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public OhipBadRequestExceptionCauseStackTrace moduleName(String moduleName) {
    this.moduleName = moduleName;
    return this;
  }

  /**
   * Get moduleName
   * @return moduleName
   */
  
  @Schema(name = "moduleName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("moduleName")
  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public OhipBadRequestExceptionCauseStackTrace moduleVersion(String moduleVersion) {
    this.moduleVersion = moduleVersion;
    return this;
  }

  /**
   * Get moduleVersion
   * @return moduleVersion
   */
  
  @Schema(name = "moduleVersion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("moduleVersion")
  public String getModuleVersion() {
    return moduleVersion;
  }

  public void setModuleVersion(String moduleVersion) {
    this.moduleVersion = moduleVersion;
  }

  public OhipBadRequestExceptionCauseStackTrace nativeMethod(Boolean nativeMethod) {
    this.nativeMethod = nativeMethod;
    return this;
  }

  /**
   * Get nativeMethod
   * @return nativeMethod
   */
  
  @Schema(name = "nativeMethod", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nativeMethod")
  public Boolean getNativeMethod() {
    return nativeMethod;
  }

  public void setNativeMethod(Boolean nativeMethod) {
    this.nativeMethod = nativeMethod;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OhipBadRequestExceptionCauseStackTrace ohipBadRequestExceptionCauseStackTrace = (OhipBadRequestExceptionCauseStackTrace) o;
    return Objects.equals(this.classLoaderName, ohipBadRequestExceptionCauseStackTrace.classLoaderName) &&
        Objects.equals(this.className, ohipBadRequestExceptionCauseStackTrace.className) &&
        Objects.equals(this.fileName, ohipBadRequestExceptionCauseStackTrace.fileName) &&
        Objects.equals(this.lineNumber, ohipBadRequestExceptionCauseStackTrace.lineNumber) &&
        Objects.equals(this.methodName, ohipBadRequestExceptionCauseStackTrace.methodName) &&
        Objects.equals(this.moduleName, ohipBadRequestExceptionCauseStackTrace.moduleName) &&
        Objects.equals(this.moduleVersion, ohipBadRequestExceptionCauseStackTrace.moduleVersion) &&
        Objects.equals(this.nativeMethod, ohipBadRequestExceptionCauseStackTrace.nativeMethod);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classLoaderName, className, fileName, lineNumber, methodName, moduleName, moduleVersion, nativeMethod);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OhipBadRequestExceptionCauseStackTrace {\n");
    sb.append("    classLoaderName: ").append(toIndentedString(classLoaderName)).append("\n");
    sb.append("    className: ").append(toIndentedString(className)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
    sb.append("    lineNumber: ").append(toIndentedString(lineNumber)).append("\n");
    sb.append("    methodName: ").append(toIndentedString(methodName)).append("\n");
    sb.append("    moduleName: ").append(toIndentedString(moduleName)).append("\n");
    sb.append("    moduleVersion: ").append(toIndentedString(moduleVersion)).append("\n");
    sb.append("    nativeMethod: ").append(toIndentedString(nativeMethod)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * OhipBadRequestExceptionCauseStackTraceInner
 */

@JsonTypeName("OhipBadRequestException_cause_stackTrace_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OhipBadRequestExceptionCauseStackTraceInner {

  private @Nullable String classLoaderName;

  private @Nullable String className;

  private @Nullable String fileName;

  private @Nullable Integer lineNumber;

  private @Nullable String methodName;

  private @Nullable String moduleName;

  private @Nullable String moduleVersion;

  private @Nullable Boolean nativeMethod;

  public OhipBadRequestExceptionCauseStackTraceInner classLoaderName(String classLoaderName) {
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

  public OhipBadRequestExceptionCauseStackTraceInner className(String className) {
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

  public OhipBadRequestExceptionCauseStackTraceInner fileName(String fileName) {
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

  public OhipBadRequestExceptionCauseStackTraceInner lineNumber(Integer lineNumber) {
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

  public OhipBadRequestExceptionCauseStackTraceInner methodName(String methodName) {
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

  public OhipBadRequestExceptionCauseStackTraceInner moduleName(String moduleName) {
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

  public OhipBadRequestExceptionCauseStackTraceInner moduleVersion(String moduleVersion) {
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

  public OhipBadRequestExceptionCauseStackTraceInner nativeMethod(Boolean nativeMethod) {
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
    OhipBadRequestExceptionCauseStackTraceInner ohipBadRequestExceptionCauseStackTraceInner = (OhipBadRequestExceptionCauseStackTraceInner) o;
    return Objects.equals(this.classLoaderName, ohipBadRequestExceptionCauseStackTraceInner.classLoaderName) &&
        Objects.equals(this.className, ohipBadRequestExceptionCauseStackTraceInner.className) &&
        Objects.equals(this.fileName, ohipBadRequestExceptionCauseStackTraceInner.fileName) &&
        Objects.equals(this.lineNumber, ohipBadRequestExceptionCauseStackTraceInner.lineNumber) &&
        Objects.equals(this.methodName, ohipBadRequestExceptionCauseStackTraceInner.methodName) &&
        Objects.equals(this.moduleName, ohipBadRequestExceptionCauseStackTraceInner.moduleName) &&
        Objects.equals(this.moduleVersion, ohipBadRequestExceptionCauseStackTraceInner.moduleVersion) &&
        Objects.equals(this.nativeMethod, ohipBadRequestExceptionCauseStackTraceInner.nativeMethod);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classLoaderName, className, fileName, lineNumber, methodName, moduleName, moduleVersion, nativeMethod);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OhipBadRequestExceptionCauseStackTraceInner {\n");
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


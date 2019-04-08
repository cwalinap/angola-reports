package org.openlmis.ao.reports.service;

import static java.io.File.createTempFile;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_CREATION;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_FILE_EMPTY;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_FILE_INCORRECT_TYPE;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_FILE_INVALID;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_FILE_MISSING;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_IO;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_PARAMETER_INCORRECT_TYPE;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_PARAMETER_MISSING;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_TEMPLATE_EXIST;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

import org.openlmis.ao.reports.domain.JasperTemplate;
import org.openlmis.ao.reports.exception.ReportingException;
import org.openlmis.ao.reports.repository.JasperTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.openlmis.ao.reports.domain.JasperTemplateParameter;
import org.openlmis.ao.reports.domain.JasperTemplateParameterDependency;

@Service
@SuppressWarnings("PMD.TooManyMethods")
public class JasperTemplateService {
  protected static final String REPORT_TYPE_PROPERTY = "reportType";
  protected static final String IS_DISPLAYED_PROPERTY = "isDisplayed";
  protected static final String SUPPORTED_FORMATS_PROPERTY = "supportedFormats";

  @Autowired
  private JasperTemplateRepository jasperTemplateRepository;

  /**
   * Validate ".jrmxl" file and insert this template to database.
   */
  public void validateFileAndInsertTemplate(JasperTemplate jasperTemplate, MultipartFile file)
      throws ReportingException {
    throwIfTemplateWithSameNameAlreadyExists(jasperTemplate.getName());
    validateFileAndSetData(jasperTemplate, file);
    saveWithParameters(jasperTemplate);
  }

  /**
   * Validate ".jrmxl" file and insert if template not exist. If this name of template already
   * exist, remove older template and insert new.
   */
  public void validateFileAndSaveTemplate(JasperTemplate jasperTemplate, MultipartFile file)
      throws ReportingException {
    JasperTemplate templateTmp = jasperTemplateRepository.findByName(jasperTemplate.getName());
    if (templateTmp != null) {
      jasperTemplateRepository.delete(templateTmp.getId());
    }
    validateFileAndSetData(jasperTemplate, file);
    saveWithParameters(jasperTemplate);
  }

  /**
   * Insert template and template parameters to database.
   */
  public void saveWithParameters(JasperTemplate jasperTemplate) {
    jasperTemplateRepository.save(jasperTemplate);
  }

  /**
   * Convert template from ".jasper" format in database to ".jrxml"(extension) format.
   */
  public File convertJasperToXml(JasperTemplate jasperTemplate) throws ReportingException {
    try (InputStream inputStream = new ByteArrayInputStream(jasperTemplate.getData());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      JasperCompileManager.writeReportToXmlStream(inputStream, outputStream);
      File xmlReport = createTempFile(jasperTemplate.getName(), ".jrxml");
      writeByteArrayToFile(xmlReport, outputStream.toByteArray());
      return xmlReport;
    } catch (JRException | IOException ex) {
      throw new ReportingException(ex, ERROR_REPORTING_CREATION);
    }
  }

  /**
   * Validate ".jrxml" report file with JasperCompileManager. If report is valid create additional
   * report parameters. Save additional report parameters as JasperTemplateParameter list. Save
   * report file as ".jasper" in byte array in Template class. If report is not valid throw
   * exception.
   */
  private void validateFileAndSetData(JasperTemplate jasperTemplate, MultipartFile file)
      throws ReportingException {
    throwIfFileIsNull(file);
    throwIfIncorrectFileType(file);
    throwIfFileIsEmpty(file);

    try {
      JasperReport report = JasperCompileManager.compileReport(file.getInputStream());

      String reportType = report.getProperty(REPORT_TYPE_PROPERTY);
      if (reportType != null) {
        jasperTemplate.setType(reportType);
      }

      String isDisplayed = report.getProperty(IS_DISPLAYED_PROPERTY);
      if (isDisplayed != null) {
        jasperTemplate.setIsDisplayed(Boolean.valueOf(isDisplayed));
      }

      String formats = report.getProperty(SUPPORTED_FORMATS_PROPERTY);
      if (formats != null) {
        jasperTemplate.setSupportedFormats(extractListProperties(formats));
      }

      JRParameter[] jrParameters = report.getParameters();

      if (jrParameters != null && jrParameters.length > 0) {
        setTemplateParameters(jasperTemplate, jrParameters);
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bos);
      out.writeObject(report);
      jasperTemplate.setData(bos.toByteArray());
    } catch (JRException ex) {
      throw new ReportingException(ex, ERROR_REPORTING_FILE_INVALID);
    } catch (IOException ex) {
      throw new ReportingException(ex, ERROR_REPORTING_IO, ex.getMessage());
    }
  }

  private void setTemplateParameters(JasperTemplate jasperTemplate, JRParameter[] jrParameters)
      throws ReportingException {
    ArrayList<JasperTemplateParameter> parameters = new ArrayList<>();

    for (JRParameter jrParameter : jrParameters) {
      if (!jrParameter.isSystemDefined() && jrParameter.isForPrompting()) {
        JasperTemplateParameter jasperTemplateParameter = createParameter(jrParameter);
        jasperTemplateParameter.setTemplate(jasperTemplate);
        parameters.add(jasperTemplateParameter);
      }
    }

    jasperTemplate.setTemplateParameters(parameters);
  }

  /**
   * Create new report parameter of report which is not defined in Jasper system.
   */
  private JasperTemplateParameter createParameter(JRParameter jrParameter)
      throws ReportingException {
    String displayName = jrParameter.getPropertiesMap().getProperty("displayName");

    if (isBlank(displayName)) {
      throw new ReportingException(
          ERROR_REPORTING_PARAMETER_MISSING, "displayName");
    }

    String dataType = jrParameter.getValueClassName();
    if (isNotBlank(dataType)) {
      try {
        Class.forName(dataType);
      } catch (ClassNotFoundException err) {
        throw new ReportingException(err, ERROR_REPORTING_PARAMETER_INCORRECT_TYPE,
            jrParameter.getName(), dataType);
      }
    }

    // Set parameters.
    JasperTemplateParameter jasperTemplateParameter = new JasperTemplateParameter();
    jasperTemplateParameter.setName(jrParameter.getName());
    jasperTemplateParameter.setDisplayName(displayName);
    jasperTemplateParameter.setDescription(jrParameter.getDescription());
    jasperTemplateParameter.setDataType(dataType);
    jasperTemplateParameter.setSelectExpression(
        jrParameter.getPropertiesMap().getProperty("selectExpression"));
    jasperTemplateParameter.setSelectMethod(
        jrParameter.getPropertiesMap().getProperty("selectMethod"));
    jasperTemplateParameter.setSelectBody(
        jrParameter.getPropertiesMap().getProperty("selectBody"));
    jasperTemplateParameter.setSelectProperty(
        jrParameter.getPropertiesMap().getProperty("selectProperty"));
    jasperTemplateParameter.setDisplayProperty(
        jrParameter.getPropertiesMap().getProperty("displayProperty"));
    String required = jrParameter.getPropertiesMap().getProperty("required");
    if (required != null) {
      jasperTemplateParameter.setRequired(Boolean.parseBoolean(
          jrParameter.getPropertiesMap().getProperty("required")));
    }

    if (jrParameter.getDefaultValueExpression() != null) {
      jasperTemplateParameter.setDefaultValue(jrParameter.getDefaultValueExpression()
          .getText().replace("\"", "").replace("\'", ""));
    }

    jasperTemplateParameter.setOptions(extractOptions(jrParameter));
    jasperTemplateParameter.setDependencies(extractDependencies(jrParameter));

    return jasperTemplateParameter;
  }

  /**
   * Map request parameters to the template parameters in the template. If there are no template
   * parameters, returns an empty Map.
   *
   * @param request  request with parameters
   * @param template template with parameters
   * @return Map of matching parameters, empty Map if none match
   */
  public Map<String, Object> mapRequestParametersToTemplate(HttpServletRequest request,
      JasperTemplate template) {

    List<JasperTemplateParameter> templateParameters = template.getTemplateParameters();
    if (templateParameters == null) {
      return new HashMap<>();
    }

    Map<String, String[]> requestParameterMap = request.getParameterMap();
    Map<String, Object> map = new HashMap<>();

    for (JasperTemplateParameter templateParameter : templateParameters) {
      String templateParameterName = templateParameter.getName();

      for (String requestParamName : requestParameterMap.keySet()) {

        if (templateParameterName.equalsIgnoreCase(requestParamName)) {
          String requestParamValue = "";
          if (requestParameterMap.get(templateParameterName).length > 0) {
            requestParamValue = requestParameterMap.get(templateParameterName)[0];
          }

          if (!(isBlank(requestParamValue)
              || "null".equals(requestParamValue)
              || "undefined".equals(requestParamValue))) {
            map.put(templateParameterName, requestParamValue);
          }
        }
      }
    }

    return map;
  }

  private void throwIfTemplateWithSameNameAlreadyExists(String name) throws ReportingException {
    if (jasperTemplateRepository.findByName(name) != null) {
      throw new ReportingException(ERROR_REPORTING_TEMPLATE_EXIST);
    }
  }

  private void throwIfFileIsEmpty(MultipartFile file) throws ReportingException {
    if (file.isEmpty()) {
      throw new ReportingException(ERROR_REPORTING_FILE_EMPTY);
    }
  }

  private void throwIfIncorrectFileType(MultipartFile file) throws ReportingException {
    if (!file.getOriginalFilename().endsWith(".jrxml")) {
      throw new ReportingException(ERROR_REPORTING_FILE_INCORRECT_TYPE);
    }
  }

  private void throwIfFileIsNull(MultipartFile file) throws ReportingException {
    if (file == null) {
      throw new ReportingException(ERROR_REPORTING_FILE_MISSING);
    }
  }

  private List<String> extractOptions(JRParameter parameter) {
    return extractListProperties(parameter, "options");
  }

  private List<JasperTemplateParameterDependency> extractDependencies(JRParameter parameter) {
    return extractListProperties(parameter, "dependencies")
        .stream()
        .map(option -> {
          // split by colons
          String[] properties = option.split(":");
          return new JasperTemplateParameterDependency(properties[0], properties[1], properties[2]);
        })
        .collect(Collectors.toList());
  }

  private List<String> extractListProperties(JRParameter parameter, String property) {
    return extractListProperties(
        parameter.getPropertiesMap().getProperty(property));
  }

  private List<String> extractListProperties(String property) {
    if (property != null) {
      // split by unescaped commas
      return Arrays
          .stream(property.split("(?<!\\\\),"))
          .map(option -> option.replace("\\,", ","))
          .collect(Collectors.toList());
    }

    return new ArrayList<>();
  }
}

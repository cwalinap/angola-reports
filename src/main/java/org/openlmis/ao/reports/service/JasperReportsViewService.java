package org.openlmis.ao.reports.service;

import static java.io.File.createTempFile;
import static java.util.Collections.singletonList;
import static org.openlmis.ao.reports.i18n.JasperMessageKeys.ERROR_JASPER_FILE_CREATION;
import static org.openlmis.ao.reports.i18n.MessageKeys.ERROR_GENERATE_REPORT_FAILED;
import static org.openlmis.ao.reports.i18n.MessageKeys.ERROR_IO;
import static org.openlmis.ao.reports.i18n.MessageKeys.ERROR_JASPER_FILE_FORMAT;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_CLASS_NOT_FOUND;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_IO;
import static org.openlmis.ao.reports.web.ReportTypes.ORDER_REPORT;
import static net.sf.jasperreports.engine.export.JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collections;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.openlmis.ao.reports.dto.RequisitionReportDto;
import org.openlmis.ao.reports.dto.external.OrderDto;
import org.openlmis.ao.reports.dto.external.OrderLineItemDto;
import org.openlmis.ao.reports.dto.external.ProcessingPeriodDto;
import org.openlmis.ao.reports.dto.external.RequisitionDto;
import org.openlmis.ao.reports.dto.external.RequisitionStatusDto;
import org.openlmis.ao.reports.dto.external.RequisitionTemplateColumnDto;
import org.openlmis.ao.reports.dto.external.RequisitionTemplateDto;
import org.openlmis.ao.reports.dto.external.StockCardDto;
import org.openlmis.ao.reports.dto.external.WrappedStockCardDto;
import org.openlmis.ao.reports.service.fulfillment.OrderService;
import org.openlmis.ao.reports.service.referencedata.BaseReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.PeriodReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.UserReferenceDataService;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperReport;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.openlmis.ao.reports.service.stockmanagement.StockCardStockmanagementService;
import org.openlmis.ao.reports.service.stockmanagement.StockCardStockSummariesService;
import org.openlmis.ao.reports.web.RequisitionReportDtoBuilder;
import org.openlmis.ao.utils.ReportUtils;
import org.openlmis.ao.utils.RequestParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.openlmis.ao.reports.domain.JasperTemplate;
import org.openlmis.ao.reports.exception.JasperReportViewException;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

@Service
public class JasperReportsViewService {
  private static final String REQUISITION_REPORT_DIR = "/jasperTemplates/requisition.jrxml";
  private static final String CARD_SUMMARY_REPORT_URL = "/jasperTemplates/stockCardSummary.jrxml";
  private static final String REQUISITION_LINE_REPORT_DIR =
      "/jasperTemplates/requisitionLines.jrxml";
  private static final String DATASOURCE = "datasource";
  private static final String DATE_FORMAT = "dateFormat";
  private static final String DECIMAL_FORMAT = "decimalFormat";

  @Autowired
  private DataSource replicationDataSource;

  @Autowired
  private PeriodReferenceDataService periodReferenceDataService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  @Autowired
  private RequisitionReportDtoBuilder requisitionReportDtoBuilder;

  @Autowired
  private StockCardStockmanagementService stockCardService;

  @Autowired
  private StockCardStockSummariesService stockCardStockSummariesService;

  @Autowired
  private ApplicationContext appContext;

  @Value("${dateFormat}")
  private String dateFormat;

  @Value("${dateTimeFormat}")
  private String dateTimeFormat;

  @Value("${groupingSeparator}")
  private String groupingSeparator;

  @Value("${groupingSize}")
  private String groupingSize;

  @Value("${defaultLocale}")
  private String defaultLocale;

  @Value("${currencyLocale}")
  private String currencyLocale;

  /**
   * Create Jasper Report View.
   * Create Jasper Report (".jasper" file) from bytes from Template entity.
   * Set 'Jasper' exporter parameters, JDBC data source, web application context, url to file.
   *
   * @param jasperTemplate template that will be used to create a view
   * @param request        it is used to take web application context
   * @return created jasper view.
   * @throws JasperReportViewException if there will be any problem with creating the view.
   */
  public JasperReportsMultiFormatView getJasperReportsView(
      JasperTemplate jasperTemplate, HttpServletRequest request) throws JasperReportViewException {
    JasperReportsMultiFormatView jasperView = new JasperReportsMultiFormatView();
    setExportParams(jasperView);
    jasperView.setUrl(getReportUrlForReportData(jasperTemplate));
    jasperView.setJdbcDataSource(replicationDataSource);

    if (getApplicationContext(request) != null) {
      jasperView.setApplicationContext(getApplicationContext(request));
    }
    return jasperView;
  }

  /**
   * Set export parameters in jasper view.
   */
  private void setExportParams(JasperReportsMultiFormatView jasperView) {
    Map<JRExporterParameter, Object> reportFormatMap = new HashMap<>();
    reportFormatMap.put(IS_USING_IMAGES_TO_ALIGN, false);
    jasperView.setExporterParameters(reportFormatMap);
  }

  /**
   * Get application context from servlet.
   */
  public WebApplicationContext getApplicationContext(HttpServletRequest servletRequest) {
    ServletContext servletContext = servletRequest.getSession().getServletContext();
    return WebApplicationContextUtils.getWebApplicationContext(servletContext);
  }

  /**
   * Create ".jasper" file with byte array from Template.
   *
   * @return Url to ".jasper" file.
   */
  private String getReportUrlForReportData(JasperTemplate jasperTemplate)
      throws JasperReportViewException {
    File tmpFile;

    try {
      tmpFile = createTempFile(jasperTemplate.getName() + "_temp", ".jasper");
    } catch (IOException exp) {
      throw new JasperReportViewException(
          exp, ERROR_JASPER_FILE_CREATION
      );
    }

    try (ObjectInputStream inputStream =
             new ObjectInputStream(new ByteArrayInputStream(jasperTemplate.getData()))) {
      JasperReport jasperReport = (JasperReport) inputStream.readObject();

      try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
           ObjectOutputStream out = new ObjectOutputStream(bos)) {

        out.writeObject(jasperReport);
        writeByteArrayToFile(tmpFile, bos.toByteArray());

        return tmpFile.toURI().toURL().toString();
      }
    } catch (IOException exp) {
      throw new JasperReportViewException(exp, ERROR_REPORTING_IO, exp.getMessage());
    } catch (ClassNotFoundException exp) {
      throw new JasperReportViewException(
          exp, ERROR_REPORTING_CLASS_NOT_FOUND, JasperReport.class.getName());
    }
  }

  /**
   * Generate stock card report in PDF format.
   *
   * @param jasperView generic jasper report view
   * @param parameters template parameters populated with values from the request
   * @return generated stock card report.
   */
  public ModelAndView getStockCardReportView(JasperReportsMultiFormatView jasperView,
      Map<String, Object> parameters) {
    StockCardDto stockCardDto = stockCardService.findOne(
        UUID.fromString(parameters.get("stockCard").toString())
    );

    Collections.reverse(stockCardDto.getLineItems());
    parameters.put(DATASOURCE, new JRBeanCollectionDataSource(singletonList(stockCardDto)));
    parameters.put("hasLot", stockCardDto.hasLot());
    parameters.put(DATE_FORMAT, dateFormat);
    parameters.put(DECIMAL_FORMAT, createDecimalFormat());

    return new ModelAndView(jasperView, parameters);
  }

  /**
   * Get customized Jasper Report View for Order Report.
   *
   * @param jasperView generic jasper report view
   * @param parameters template parameters populated with values from the request
   * @return customized jasper view.
   */
  public ModelAndView getOrderJasperReportView(JasperReportsMultiFormatView jasperView,
                                               Map<String, Object> parameters) {
    OrderDto order = orderService.findOne(
        UUID.fromString(parameters.get("order").toString())
    );
    order.getStatusChanges().forEach(
        statusChange -> statusChange.setAuthor(
            getIfPresent(userReferenceDataService, statusChange.getAuthorId()))
    );
    List<OrderLineItemDto> items = order.getOrderLineItems();
    items.sort(Comparator.comparing(c -> c.getOrderable().getProductCode()));

    parameters.put(DATASOURCE, new JRBeanCollectionDataSource(items));
    parameters.put("order", order);
    parameters.put("orderingPeriod", order.getEmergency()
        ? order.getProcessingPeriod() : findNextPeriod(order.getProcessingPeriod(), null));

    return new ModelAndView(jasperView, parameters);
  }

  /**
   * Create custom Jasper Report View for printing a POD.
   *
   * @param jasperView generic jasper report view
   * @param parameters template parameters populated with values from the request
   * @return customized jasper view.
   */
  public ModelAndView getPodJasperReportView(JasperReportsMultiFormatView jasperView,
                                             Map<String, Object> parameters) {
    parameters.put("id", parameters.get("proofOfDelivery").toString());
    parameters.put(DATE_FORMAT, dateFormat);
    DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
    decimalFormatSymbols.setGroupingSeparator(groupingSeparator.charAt(0));
    DecimalFormat decimalFormat = new DecimalFormat("", decimalFormatSymbols);
    decimalFormat.setGroupingSize(Integer.parseInt(groupingSize));
    parameters.put(DECIMAL_FORMAT, decimalFormat);
    parameters.put("dateTimeFormat", dateTimeFormat);

    return new ModelAndView(jasperView, parameters);
  }

  /**
   * Create custom Jasper Report View for printing a requisition.
   *
   * @param requisition requisition to render report for.
   * @param request  it is used to take web application context.
   * @return created jasper view.
   * @throws JasperReportViewException if there will be any problem with creating the view.
   */
  public ModelAndView getRequisitionJasperReportView(
      RequisitionDto requisition, HttpServletRequest request) throws JasperReportViewException {
    RequisitionReportDto reportDto = requisitionReportDtoBuilder.build(requisition);
    RequisitionTemplateDto template = requisition.getTemplate();

    Map<String, Object> params = ReportUtils.createParametersMap();
    params.put("subreport", createCustomizedRequisitionLineSubreport(
        template, requisition.getStatus()));
    params.put(DATASOURCE, Collections.singletonList(reportDto));
    params.put("template", template);
    params.put(DATE_FORMAT, dateFormat);
    params.put(DECIMAL_FORMAT, createDecimalFormat());
    params.put("currencyDecimalFormat",
        NumberFormat.getCurrencyInstance(getLocaleFromService()));

    JasperReportsMultiFormatView jasperView = new JasperReportsMultiFormatView();
    setExportParams(jasperView);
    setCustomizedJasperTemplateForRequisitionReport(jasperView);

    if (getApplicationContext(request) != null) {
      jasperView.setApplicationContext(getApplicationContext(request));
    }
    return new ModelAndView(jasperView, params);
  }

  /**
   * Generate stock card summary report in PDF format.
   *
   * @param program  program id
   * @param facility facility id
   * @return generated stock card summary report.
   */
  public ModelAndView getStockCardSummariesReportView(UUID program, UUID facility)
          throws JasperReportViewException {
    RequestParameters requestParameters = RequestParameters
            .init()
            .set("program", program)
            .set("facility", facility);
    WrappedStockCardDto wrappedStockCardDto = stockCardStockSummariesService
            .findOne("", requestParameters);
    List<StockCardDto> cards = wrappedStockCardDto.getContent();
    StockCardDto firstCard = cards.get(0);
    Map<String, Object> params = new HashMap<>();
    params.put("stockCardSummaries", cards);

    params.put("program", firstCard.getProgram());
    params.put("facility", firstCard.getFacility());
    //right now, each report can only be about one program, one facility
    //in the future we may want to support one reprot for multiple programs
    params.put("showProgram", getCount(cards, card -> card.getProgram().getId().toString()) > 1);
    params.put("showFacility", getCount(cards, card -> card.getFacility().getId().toString()) > 1);
    params.put("showLot", cards.stream().anyMatch(card -> card.getLotId() != null));
    params.put(DATE_FORMAT, dateFormat);
    params.put("dateTimeFormat", dateTimeFormat);
    params.put(DECIMAL_FORMAT, createDecimalFormat());

    return generateReport(CARD_SUMMARY_REPORT_URL, params);
  }

  /**
   * Get report's filename.
   *
   * @param template jasper template
   * @param params   template parameters populated with values from the request
   * @return filename
   */
  public String getFilename(JasperTemplate template, Map<String, Object> params) {
    String templateType = template.getType();
    // start the filename with report's name
    StringBuilder fileName = new StringBuilder(template.getName());
    // add all the params that report takes to the value list
    List<Object> values = new ArrayList<>();
    for (Map.Entry<String, Object> param : params.entrySet()) {
      values.add(param.getValue());
    }
    // if it's Order report, add filename parts manually
    if (ORDER_REPORT.equals(templateType)) {
      OrderDto order = orderService.findOne(
          UUID.fromString(params.get("order").toString())
      );
      ProcessingPeriodDto period = order.getEmergency() ? order.getProcessingPeriod() :
          findNextPeriod(order.getProcessingPeriod(), null);
      values = Arrays.asList(
          order.getProgram().getName(),
          (period != null) ? period.getName() : "",
          order.getFacility().getName()
      );
    }
    // add all the parts to the filename and separate them by "_"
    for (Object value : values) {
      fileName
          .append('_')
          .append(value.toString());
    }
    // replace whitespaces with "_" and make the filename lowercase
    return fileName.toString()
        .replaceAll("\\s+", "_")
        .toLowerCase(Locale.ENGLISH);
  }

  private long getCount(List<StockCardDto> stockCards, Function<StockCardDto, String> mapper) {
    return stockCards.stream().map(mapper).distinct().count();
  }

  private ModelAndView generateReport(String templateUrl, Map<String, Object> params)
          throws JasperReportViewException {
    JasperReportsPdfView view = createJasperReportsPdfView();
    view.setUrl(compileReportAndGetUrl(templateUrl));
    view.setApplicationContext(appContext);
    return new ModelAndView(view, params);
  }

  protected JasperReportsPdfView createJasperReportsPdfView() {
    return new JasperReportsPdfView();
  }

  private String saveAndGetUrl(JasperReport report, String templateName)
          throws JasperReportViewException, IOException {
    File reportTempFile;
    try {
      reportTempFile = createTempFile(templateName, ".jasper");
    } catch (IOException ex) {
      throw new JasperReportViewException(ex, ERROR_JASPER_FILE_CREATION, ex.getMessage());
    }

    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream out = new ObjectOutputStream(bos)) {

      out.writeObject(report);
      writeByteArrayToFile(reportTempFile, bos.toByteArray());

      return reportTempFile.toURI().toURL().toString();
    }
  }

  private String compileReportAndGetUrl(String templateUrl) throws JasperReportViewException {
    try (InputStream inputStream = getClass().getResourceAsStream(templateUrl)) {
      JasperReport report = JasperCompileManager.compileReport(inputStream);

      return saveAndGetUrl(report, "report_temp");
    } catch (IOException ex) {
      throw new JasperReportViewException(ex, ERROR_IO, ex.getMessage());
    } catch (JRException ex) {
      throw new JasperReportViewException(ex, ERROR_GENERATE_REPORT_FAILED, ex.getMessage());
    }
  }

  private JasperDesign createCustomizedRequisitionLineSubreport(RequisitionTemplateDto template,
      RequisitionStatusDto requisitionStatus)
      throws JasperReportViewException {
    try (InputStream inputStream = getClass().getResourceAsStream(REQUISITION_LINE_REPORT_DIR)) {
      JasperDesign design = JRXmlLoader.load(inputStream);
      JRBand detail = design.getDetailSection().getBands()[0];
      JRBand header = design.getColumnHeader();

      Map<String, RequisitionTemplateColumnDto> columns =
          ReportUtils.getSortedTemplateColumnsForPrint(template.getColumnsMap(), requisitionStatus);

      ReportUtils.customizeBandWithTemplateFields(detail, columns, design.getPageWidth(), 9);
      ReportUtils.customizeBandWithTemplateFields(header, columns, design.getPageWidth(), 9);

      return design;
    } catch (IOException err) {
      throw new JasperReportViewException(err, ERROR_IO, err.getMessage());
    } catch (JRException err) {
      throw new JasperReportViewException(err, ERROR_JASPER_FILE_FORMAT, err.getMessage());
    }
  }

  private void setCustomizedJasperTemplateForRequisitionReport(
      JasperReportsMultiFormatView jasperView) throws JasperReportViewException {
    try (InputStream inputStream = getClass().getResourceAsStream(REQUISITION_REPORT_DIR)) {
      File reportTempFile = createTempFile("requisitionReport_temp", ".jasper");
      JasperReport report = JasperCompileManager.compileReport(inputStream);

      try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
          ObjectOutputStream out = new ObjectOutputStream(bos)) {

        out.writeObject(report);
        writeByteArrayToFile(reportTempFile, bos.toByteArray());

        jasperView.setUrl(reportTempFile.toURI().toURL().toString());
      }
    } catch (IOException err) {
      throw new JasperReportViewException(err, ERROR_IO, err.getMessage());
    } catch (JRException err) {
      throw new JasperReportViewException(err, ERROR_JASPER_FILE_FORMAT, err.getMessage());
    }
  }

  private <T> T getIfPresent(BaseReferenceDataService<T> service, UUID id) {
    return Optional.ofNullable(id).isPresent() ? service.findOne(id) : null;
  }

  private ProcessingPeriodDto findNextPeriod(ProcessingPeriodDto period,
                                             Collection<ProcessingPeriodDto> periods) {
    periods = (periods != null) ? periods : periodReferenceDataService.search(
        period.getProcessingSchedule().getId(), null);
    return periods.stream()
        .filter(p -> p.getStartDate().isAfter(period.getEndDate()))
        .min(Comparator.comparing(ProcessingPeriodDto::getStartDate)).orElse(null);
  }

  private DecimalFormat createDecimalFormat() {
    DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
    decimalFormatSymbols.setGroupingSeparator(groupingSeparator.charAt(0));
    DecimalFormat decimalFormat = new DecimalFormat("", decimalFormatSymbols);
    decimalFormat.setGroupingSize(Integer.valueOf(groupingSize));
    return decimalFormat;
  }

  protected Locale getLocaleFromService() {
    return new Locale(defaultLocale, currencyLocale);
  }
}

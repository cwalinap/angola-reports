package org.openlmis.ao.reports.service;

import static java.io.File.createTempFile;
import static java.util.Collections.singletonList;
import static net.sf.jasperreports.engine.export.JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;
import static org.openlmis.ao.reports.i18n.JasperMessageKeys.ERROR_JASPER_FILE_CREATION;
import static org.openlmis.ao.reports.i18n.MessageKeys.ERROR_IO;
import static org.openlmis.ao.reports.i18n.MessageKeys.ERROR_JASPER_FILE_FORMAT;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_CLASS_NOT_FOUND;
import static org.openlmis.ao.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_IO;
import static org.openlmis.ao.reports.web.ReportTypes.ORDER_REPORT;
import static org.openlmis.ao.reports.web.ReportTypes.USERS_REPORT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.ao.reports.domain.JasperTemplate;
import org.openlmis.ao.reports.dto.RequisitionReportDto;
import org.openlmis.ao.reports.dto.external.CanFulfillForMeEntryDto;
import org.openlmis.ao.reports.dto.external.DispensableDto;
import org.openlmis.ao.reports.dto.external.FacilityDto;
import org.openlmis.ao.reports.dto.external.FullStockCardSummaryV2Dto;
import org.openlmis.ao.reports.dto.external.GeographicZoneDto;
import org.openlmis.ao.reports.dto.external.LotDto;
import org.openlmis.ao.reports.dto.external.OrderDto;
import org.openlmis.ao.reports.dto.external.OrderLineItemDto;
import org.openlmis.ao.reports.dto.external.OrderableDto;
import org.openlmis.ao.reports.dto.external.ProcessingPeriodDto;
import org.openlmis.ao.reports.dto.external.RequisitionDto;
import org.openlmis.ao.reports.dto.external.RequisitionStatusDto;
import org.openlmis.ao.reports.dto.external.RequisitionTemplateColumnDto;
import org.openlmis.ao.reports.dto.external.RequisitionTemplateDto;
import org.openlmis.ao.reports.dto.external.StockCardDto;
import org.openlmis.ao.reports.dto.external.StockCardSummaryV2Dto;
import org.openlmis.ao.reports.dto.external.WrappedStockCardV2Dto;
import org.openlmis.ao.reports.exception.JasperReportViewException;
import org.openlmis.ao.reports.service.fulfillment.OrderService;
import org.openlmis.ao.reports.service.referencedata.BaseReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.FacilityReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.LotReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.OrderableReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.PeriodReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.ProgramReferenceDataService;
import org.openlmis.ao.reports.service.referencedata.UserReferenceDataService;
import org.openlmis.ao.reports.service.stockmanagement.StockCardStockmanagementService;
import org.openlmis.ao.reports.service.stockmanagement.StockCardV2StockSummariesService;
import org.openlmis.ao.reports.web.RequisitionReportDtoBuilder;
import org.openlmis.ao.utils.ReportUtils;
import org.openlmis.ao.utils.RequestParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

@Service
public class JasperReportsViewService {
  private static final String REQUISITION_REPORT_DIR = "/jasperTemplates/requisition.jrxml";
  private static final String REQUISITION_LINE_REPORT_DIR =
      "/jasperTemplates/requisitionLines.jrxml";
  private static final String DATASOURCE = "datasource";
  private static final String DATE_FORMAT = "dateFormat";
  private static final String DECIMAL_FORMAT = "decimalFormat";
  private static final String FACILITY = "facility";
  private static final String FACILITY_ID = "facilityId";
  private static final String PROGRAM_ID = "programId";
  private static final String AS_OF_DATE = "asOfDate";

  private static final int PROVINCE_LEVEL = 2;
  private static final int REGION_LEVEL = 3;

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
  private StockCardV2StockSummariesService stockCardV2StockSummariesService;

  @Autowired
  private FacilityReferenceDataService facilityReferenceDataService;

  @Autowired
  private OrderableReferenceDataService orderableReferenceDataService;

  @Autowired
  private LotReferenceDataService lotReferenceDataService;

  @Autowired
  private ProgramReferenceDataService programReferenceDataService;

  @Autowired
  private UsersReportViewService usersReportViewService;

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
   * @param request it is used to take web application context
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
   * Generate users report.
   *
   * @param jasperView generic jasper report view
   * @param parameters template parameters populated with values from the request
   * @return generated users report.
   */
  public ModelAndView getUsersReportView(JasperReportsMultiFormatView jasperView,
                                             Map<String, Object> parameters) {
    parameters.put(DATE_FORMAT, dateFormat);
    parameters.put(DECIMAL_FORMAT, createDecimalFormat());

    return usersReportViewService.getReportView(jasperView, parameters);
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
   * Generate inventory report.
   *
   * @param jasperView jasper template
   * @param parameters template parameters populated with values from the request
   * @return generated inventory report.
   */
  public ModelAndView getInventoryReportView(JasperReportsMultiFormatView jasperView,
                                                      Map<String, Object> parameters) {
    LocalDate asOfDate = getAsOfDate(parameters);
    parameters.put(AS_OF_DATE, asOfDate);
    List<String> programs = Arrays.asList(parameters.get(PROGRAM_ID).toString()
        .replaceAll(";",",").split("\\s*,\\s*"));

    FacilityDto facilityDto = getReferencedFacility(parameters);

    parameters.put("program", getProgramsName(programs));
    parameters.put(FACILITY, facilityDto);
    parameters.put("province", extractProvinceName(facilityDto.getGeographicZone()));
    parameters.put("region", extractRegionName(facilityDto.getGeographicZone()));

    UUID facilityId = UUID.fromString(parameters.get(FACILITY_ID).toString());
    List<FullStockCardSummaryV2Dto> fullNonEmptySummaries = getFullSummaries(
        asOfDate, facilityId, programs);
    parameters.put(DATASOURCE, new JRBeanCollectionDataSource(fullNonEmptySummaries));
    parameters.put("stockCardSummaries", fullNonEmptySummaries);
    parameters.put(DATE_FORMAT, dateFormat);
    parameters.put(DECIMAL_FORMAT, createDecimalFormat());
    if (parameters.get("format").toString().equals("html")) {
      parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
    }

    return new ModelAndView(jasperView, parameters);
  }

  /**
   * Generate stock card summary report.
   *
   * @param jasperView jasper template
   * @param parameters template parameters populated with values from the request
   * @return generated stock card summary report.
   */
  public ModelAndView getStockCardSummaryReportView(JasperReportsMultiFormatView jasperView,
      Map<String, Object> parameters) {
    LocalDate asOfDate = getAsOfDate(parameters);
    parameters.put(AS_OF_DATE, asOfDate);
    RequestParameters requestParameters = RequestParameters
            .init()
            .set(PROGRAM_ID, UUID.fromString(parameters.get(PROGRAM_ID).toString()))
            .set(FACILITY_ID, UUID.fromString(parameters.get(FACILITY_ID).toString()))
            .set(AS_OF_DATE, asOfDate);

    WrappedStockCardV2Dto wrappedStockCardDto = stockCardV2StockSummariesService
            .findOne("", requestParameters);
    List<FullStockCardSummaryV2Dto> fullNonEmptySummaries = extractFullSummaries(
            wrappedStockCardDto.getContent()).stream().filter(sc -> sc.getStockOnHand() != null)
            .collect(Collectors.toList());
    FacilityDto facilityDto = getReferencedFacility(parameters);

    parameters.put("program", getProgramName(parameters));
    parameters.put(FACILITY, facilityDto);
    parameters.put("province", extractProvinceName(facilityDto.getGeographicZone()));
    parameters.put("region", extractRegionName(facilityDto.getGeographicZone()));
    parameters.put(DATASOURCE, new JRBeanCollectionDataSource(fullNonEmptySummaries));
    parameters.put("stockCardSummaries", fullNonEmptySummaries);
    parameters.put(DATE_FORMAT, dateFormat);
    parameters.put(DECIMAL_FORMAT, createDecimalFormat());
    if (parameters.get("format").toString().equals("html")) {
      parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
    }

    return new ModelAndView(jasperView, parameters);
  }

  /**
   * Get report's filename.
   *
   * @param template jasper template
   * @param params template parameters populated with values from the request
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
    } else if (USERS_REPORT.equals(templateType)) {
      values = usersReportViewService.getFilenameValues(params);
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

  private FacilityDto getReferencedFacility(Map<String, Object> parameters) {
    return facilityReferenceDataService
            .findOne(UUID.fromString(parameters.get(FACILITY_ID).toString()));
  }

  private String getProgramName(Map<String, Object> parameters) {
    return programReferenceDataService
            .findOne(UUID.fromString(parameters.get(PROGRAM_ID).toString())).getName();
  }

  private String getProgramsName(List<String> programs) {
    List<String> programNames = new ArrayList<>();
    programs.forEach(program -> {
      UUID programId = UUID.fromString(program.trim());
      programNames.add(programReferenceDataService.findOne(programId).getName());
    });

    return StringUtils.join(programNames, ", ");
  }

  private List<FullStockCardSummaryV2Dto> extractFullSummaries(
          List<StockCardSummaryV2Dto> summaries) {
    List<FullStockCardSummaryV2Dto> result = new ArrayList<>();
    summaries.forEach(card -> {
      if (card.getCanFulfillForMe().isEmpty()) {
        OrderableDto orderable = orderableReferenceDataService.findOne(card.getOrderable().getId());
        result.add(buildFullSummaryFromOrderable(orderable));
      } else {
        card.getCanFulfillForMe().forEach(entry -> result.add(
                buildFullSummaryFromFulfillEntry(entry)));
      }
    });
    return result;
  }

  private List<FullStockCardSummaryV2Dto> getFullSummaries(
      LocalDate asOfDate, UUID facilityId, List<String> programs) {
    Map<UUID, OrderableDto> orderableDtoMap = new HashMap<>();
    Map<UUID, LotDto> lotDtoMap = new HashMap<>();
    Map<UUID, Map<UUID, Integer>> stockOnHandMap = new HashMap<>();

    programs.forEach(program -> {
      List<FullStockCardSummaryV2Dto> fullNonEmptySummaries = getFullSummaries(
          asOfDate, facilityId, program);
      fullNonEmptySummaries.forEach(summary -> {
        UUID lotId = null;
        UUID orderableId = summary.getOrderable().getId();
        orderableDtoMap.put(orderableId, summary.getOrderable());
        if (summary.getLot() != null) {
          lotId = summary.getLot().getId();
          lotDtoMap.put(lotId, summary.getLot());
        }

        if (stockOnHandMap.containsKey(orderableId)) {
          Map<UUID, Integer> sohByLot = stockOnHandMap.get(orderableId);
          if (sohByLot.containsKey(lotId)) {
            Integer soh = sohByLot.get(lotId) + summary.getStockOnHand();
            sohByLot.put(lotId, soh);
          } else {
            sohByLot.put(lotId, summary.getStockOnHand());
          }
        } else {
          Map<UUID, Integer> sohByLot = new HashMap<>();
          sohByLot.put(lotId, summary.getStockOnHand());
          stockOnHandMap.put(orderableId, sohByLot);
        }
      });
    });

    List<FullStockCardSummaryV2Dto> fullStockCardSummaryV2Dtos = new ArrayList<>();
    stockOnHandMap.forEach((orderableId, sohByLot) -> {
      sohByLot.forEach((lotId, soh) -> {
        OrderableDto orderableDto = orderableDtoMap.get(orderableId);
        LotDto lotDto = lotDtoMap.get(lotId);
        FullStockCardSummaryV2Dto fullStockCardSummaryV2Dto = new FullStockCardSummaryV2Dto(
            orderableDto, orderableDto.getDispensable(), lotDto, soh);
        fullStockCardSummaryV2Dtos.add(fullStockCardSummaryV2Dto);
      });
    });

    return fullStockCardSummaryV2Dtos;
  }

  private List<FullStockCardSummaryV2Dto> getFullSummaries(
      LocalDate asOfDate, UUID facilityId, String program) {
    UUID programId = UUID.fromString(program.trim());
    RequestParameters requestParameters = RequestParameters
        .init()
        .set(PROGRAM_ID, programId)
        .set(FACILITY_ID, facilityId)
        .set(AS_OF_DATE, asOfDate);

    WrappedStockCardV2Dto wrappedStockCardDto = stockCardV2StockSummariesService
        .findOne("", requestParameters);
    return extractFullSummaries(
        wrappedStockCardDto.getContent()).stream().filter(sc -> sc.getStockOnHand() != null)
        .collect(Collectors.toList());
  }

  private FullStockCardSummaryV2Dto buildFullSummaryFromFulfillEntry(
          CanFulfillForMeEntryDto entry) {
    OrderableDto orderable = entry.getOrderable() == null ? null :
            orderableReferenceDataService.findOne(entry.getOrderable().getId());
    LotDto lot = entry.getLot() == null ? null :
            lotReferenceDataService.findOne(entry.getLot().getId());
    DispensableDto dispensable = orderable == null ? null :
            orderable.getDispensable();
    return new FullStockCardSummaryV2Dto(orderable, dispensable, lot, entry.getStockOnHand());
  }

  private FullStockCardSummaryV2Dto buildFullSummaryFromOrderable(OrderableDto orderable) {
    return new FullStockCardSummaryV2Dto(orderable, orderable.getDispensable(), null, null);
  }

  private LocalDate getAsOfDate(Map<String, Object> parameters) {
    LocalDate date = LocalDate.now();
    if (parameters.get(AS_OF_DATE) != null) {
      date = LocalDate.parse(parameters.get(AS_OF_DATE).toString());
    }
    return date;
  }

  private String extractProvinceName(GeographicZoneDto zone) {
    String result = "-";
    if (zone != null) {
      if (zone.getLevel().getLevelNumber() > PROVINCE_LEVEL) {
        result = extractProvinceName(zone.getParent());
      } else if (zone.getLevel().getLevelNumber().equals(PROVINCE_LEVEL)) {
        result = zone.getName();
      }
    }
    return result;
  }

  private String extractRegionName(GeographicZoneDto zone) {
    String result = "-";
    if (zone != null && zone.getLevel().getLevelNumber().equals(REGION_LEVEL)) {
      result = zone.getName();
    }
    return result;
  }

  private long getCount(List<StockCardDto> stockCards, Function<StockCardDto, String> mapper) {
    return stockCards.stream().map(mapper).distinct().count();
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

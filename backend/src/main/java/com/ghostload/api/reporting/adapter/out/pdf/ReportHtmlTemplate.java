package com.ghostload.api.reporting.adapter.out.pdf;

import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.MaturityLevel;
import com.ghostload.api.assessment.domain.model.ModuleScore;
import com.ghostload.api.reporting.configuration.PdfProperties;
import com.ghostload.api.reporting.domain.model.ReportData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

// Plantilla institucional del reporte en HTML/CSS orientada a renderizado A4 (OpenHtmlToPdf).
// Diseñada para presentar el resumen ejecutivo sin desbordamientos de página.
final class ReportHtmlTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportHtmlTemplate.class);

    private static final Map<BenchmarkModule, String> MODULE_LABELS = Map.of(
            BenchmarkModule.ENERGY, "Energía",
            BenchmarkModule.GPU_UTILIZATION, "Utilización de GPU",
            BenchmarkModule.COOLING, "Refrigeración",
            BenchmarkModule.OPERATIONS, "Operaciones",
            BenchmarkModule.CAPACITY, "Capacidad");

    private static final Map<MaturityLevel, String> MATURITY_LABELS = Map.of(
            MaturityLevel.CRITICAL, "Crítico",
            MaturityLevel.OPERATIONAL_RISK, "Riesgo Operativo",
            MaturityLevel.GROWING, "En Crecimiento",
            MaturityLevel.MATURE, "Maduro",
            MaturityLevel.LEADER, "Líder");

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ROOT);

    private final PdfProperties properties;

    ReportHtmlTemplate(PdfProperties properties) {
        this.properties = properties;
    }

    String render(ReportData data) {
        String html = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
                  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
                <html xmlns="http://www.w3.org/1999/xhtml" lang="es">
                <head>
                  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                  <style>
                    @page {
                      size: A4 portrait;
                      margin: 10mm 12mm 10mm 12mm;
                    }
                    * { box-sizing: border-box; }
                    body {
                      margin: 0;
                      padding: 0;
                      font-family: Arial, 'DejaVu Sans', sans-serif;
                      font-size: 11px;
                      line-height: 1.35;
                      color: #1a2420;
                      background: #ffffff;
                    }

                    .header-table {
                      width: 100%;
                      background: #153c32;
                      border-radius: 6px;
                      padding: 14px 20px;
                      color: #ffffff;
                      margin-bottom: 12px;
                    }
                    .tagline {
                      color: #d8b75b;
                      font-size: 9px;
                      letter-spacing: 2px;
                      text-transform: uppercase;
                      margin-top: 3px;
                      font-weight: bold;
                    }

                    .card {
                      background: #ffffff;
                      border: 1px solid #e0d8c8;
                      border-radius: 6px;
                      padding: 12px 16px;
                      margin-bottom: 10px;
                      page-break-inside: avoid;
                    }
                    .section-title {
                      font-size: 11px;
                      font-weight: bold;
                      color: #153c32;
                      text-transform: uppercase;
                      letter-spacing: 1px;
                      border-left: 3.5px solid #d8b75b;
                      padding-left: 8px;
                      margin: 0 0 10px 0;
                    }

                    .two-col {
                      width: 100%;
                      border-collapse: collapse;
                    }
                    .two-col > tbody > tr > td {
                      vertical-align: top;
                    }

                    .info-table {
                      width: 100%;
                      border-collapse: collapse;
                    }
                    .info-table td {
                      padding: 4px 6px;
                      vertical-align: top;
                    }

                    .label {
                      color: #5c6b65;
                      font-size: 9px;
                      text-transform: uppercase;
                      letter-spacing: 0.5px;
                      font-weight: bold;
                    }
                    .value {
                      font-size: 12px;
                      font-weight: bold;
                      color: #153c32;
                    }

                    .score-box {
                      background: #153c32;
                      border-radius: 6px;
                      padding: 12px;
                      color: #ffffff;
                      text-align: center;
                    }
                    .score-number {
                      font-size: 32px;
                      font-weight: bold;
                      color: #d8b75b;
                      line-height: 1;
                    }
                    .score-unit {
                      font-size: 10px;
                      color: #cfded8;
                      margin-top: 2px;
                    }
                    .maturity-badge {
                      display: inline-block;
                      background: #d8b75b;
                      color: #153c32;
                      font-weight: bold;
                      padding: 3px 10px;
                      border-radius: 4px;
                      font-size: 11px;
                      margin-top: 6px;
                    }

                    .kpi-table {
                      width: 100%;
                      border-collapse: collapse;
                    }
                    .kpi-table td {
                      padding: 6px 10px;
                      border: 1px solid #eae4d6;
                      background: #faf8f4;
                      width: 25%;
                      vertical-align: top;
                    }

                    .module-table {
                      width: 100%;
                      border-collapse: collapse;
                    }
                    .module-table td {
                      padding: 4px 2px;
                      vertical-align: middle;
                    }
                    .module-name {
                      font-size: 10.5px;
                      font-weight: bold;
                      color: #153c32;
                      width: 28%;
                    }
                    .module-bar-td {
                      width: 58%;
                      padding-right: 8px;
                    }
                    .module-val {
                      font-size: 10.5px;
                      font-weight: bold;
                      color: #153c32;
                      text-align: right;
                      width: 14%;
                    }
                    .bar-track {
                      background: #eae5d9;
                      border-radius: 3px;
                      height: 8px;
                      width: 100%;
                    }
                    .bar-fill {
                      background: #153c32;
                      height: 8px;
                      border-radius: 3px;
                    }

                    .founder-box {
                      background: #f8f6f0;
                      border: 1px solid #e0d8c8;
                      border-radius: 6px;
                      padding: 10px 14px;
                    }
                    .cta {
                      display: inline-block;
                      background: #153c32;
                      color: #d8b75b;
                      font-weight: bold;
                      font-size: 10.5px;
                      text-decoration: none;
                      padding: 6px 14px;
                      border-radius: 4px;
                      margin-top: 6px;
                    }

                    .footer {
                      font-size: 8.5px;
                      color: #7b8882;
                      text-align: center;
                      margin-top: 8px;
                      padding-top: 6px;
                      border-top: 1px solid #e8e3d5;
                      page-break-inside: avoid;
                    }
                  </style>
                </head>
                <body>
                  <table class="header-table" cellspacing="0" cellpadding="0">
                    <tr>
                      <td style="vertical-align:middle">__LOGO__</td>
                      <td align="right" style="vertical-align:middle">
                        <div style="font-size:18px;font-weight:bold;color:#ffffff">Ghost Load</div>
                        <div class="tagline">Intelligence for AI Data Centers</div>
                      </td>
                    </tr>
                  </table>

                  <table class="two-col" cellspacing="0" cellpadding="0" style="margin-bottom:10px">
                    <tr>
                      <td style="width:58%;padding-right:8px">
                        <div class="card" style="margin-bottom:0">
                          <p class="section-title">Empresa y Operador</p>
                          <table class="info-table" cellspacing="0" cellpadding="0">
                            <tr>
                              <td style="width:50%"><span class="label">Empresa</span><br /><span class="value">__COMPANY__</span></td>
                              <td style="width:50%"><span class="label">Operador</span><br /><span class="value">__FULL_NAME__</span></td>
                            </tr>
                            <tr>
                              <td><span class="label">Cargo</span><br /><span class="value">__POSITION__</span></td>
                              <td><span class="label">País</span><br /><span class="value">__COUNTRY__</span></td>
                            </tr>
                            <tr>
                              <td colspan="2"><span class="label">Email</span><br /><span class="value">__EMAIL__</span></td>
                            </tr>
                          </table>
                        </div>
                      </td>
                      <td style="width:42%">
                        <div class="score-box">
                          <div class="label" style="color:#a8c2b7">Resultado Benchmark</div>
                          <div class="score-number">__TOTAL_SCORE__</div>
                          <div class="score-unit">de 100 puntos</div>
                          <div><span class="maturity-badge">__MATURITY__</span></div>
                          <div style="font-size:10px;margin-top:6px;color:#ffffff">
                            Percentil Industrial: <strong>__PERCENTILE__</strong>
                          </div>
                        </div>
                      </td>
                    </tr>
                  </table>

                  <div class="card">
                    <p class="section-title">KPIs de la Calculadora</p>
                    <table class="kpi-table" cellspacing="0" cellpadding="0">
                      <tr>
                        <td><span class="label">Capacidad Total</span><br /><span class="value">__TOTAL_CAPACITY__</span></td>
                        <td><span class="label">Capacidad Productiva</span><br /><span class="value">__PRODUCTIVE_CAPACITY__</span></td>
                        <td><span class="label">Capacidad No Productiva</span><br /><span class="value">__NON_PRODUCTIVE_CAPACITY__</span></td>
                        <td><span class="label">Utilización</span><br /><span class="value">__UTILIZATION__</span></td>
                      </tr>
                      <tr>
                        <td><span class="label">No Productiva %</span><br /><span class="value">__NON_PRODUCTIVE_PERCENT__</span></td>
                        <td><span class="label">Costo Mensual / kW</span><br /><span class="value">__MONTHLY_COST__</span></td>
                        <td colspan="2" style="background:#f4efe1"><span class="label">Costo Anual Desperdiciado</span><br /><span class="value" style="color:#8b6914">__ANNUAL_COST__</span></td>
                      </tr>
                    </table>
                  </div>

                  <div class="card">
                    <p class="section-title">Desempeño del Benchmark por Módulo</p>
                    <table class="module-table" cellspacing="0" cellpadding="0">
                      __MODULE_ROWS__
                    </table>
                  </div>

                  <div class="card">
                    <p class="section-title">Contacto &amp; Próximos Pasos</p>
                    <div class="founder-box">
                      <table width="100%" cellspacing="0" cellpadding="0">
                        __FOUNDER_ROWS__
                      </table>
                      __CTA__
                    </div>
                  </div>

                  <div class="footer">
                    Reporte generado el __DATE__ para __COMPANY_NAME__. Contiene información confidencial de infraestructura.
                  </div>
                </body>
                </html>
                """;
        return html.replace("__LOGO__", logoMarkup())
                .replace("__COMPANY__", esc(data.operator().companyName()))
                .replace("__FULL_NAME__", esc(data.operator().fullName()))
                .replace("__POSITION__", esc(data.operator().position()))
                .replace("__COUNTRY__", esc(data.operator().country()))
                .replace("__EMAIL__", esc(data.operator().email()))
                .replace("__TOTAL_CAPACITY__", mw(data.calculator().totalCapacityMw()))
                .replace("__PRODUCTIVE_CAPACITY__", mw(data.calculator().productiveCapacityMw()))
                .replace("__NON_PRODUCTIVE_CAPACITY__", mw(data.calculator().nonProductiveCapacityMw()))
                .replace("__UTILIZATION__", percent(data.calculator().utilizationPercentage()))
                .replace("__NON_PRODUCTIVE_PERCENT__", percent(data.calculator().nonProductivePercentage()))
                .replace("__MONTHLY_COST__", money(data.calculator().monthlyCostPerKw()))
                .replace("__ANNUAL_COST__", money(data.calculator().estimatedAnnualCost()) + " " + esc(data.calculator().currency()))
                .replace("__TOTAL_SCORE__", number(data.benchmark().totalScore()))
                .replace("__MATURITY__", maturityLabel(data.benchmark().maturityLevel()))
                .replace("__PERCENTILE__", number(data.benchmark().percentile()))
                .replace("__MODULE_ROWS__", moduleRows(data.benchmark().moduleScores()))
                .replace("__FOUNDER_ROWS__", founderRows())
                .replace("__CTA__", ctaMarkup())
                .replace("__DATE__", formatDate(data.benchmark().completedAt()))
                .replace("__COMPANY_NAME__", esc(data.operator().companyName()));
    }

    private String moduleRows(java.util.List<ModuleScore> scores) {
        StringBuilder rows = new StringBuilder();
        for (ModuleScore score : scores) {
            String label = MODULE_LABELS.getOrDefault(score.module(), score.module().name());
            double value = score.score();
            rows.append("<tr>")
                    .append("<td class=\"module-name\">").append(esc(label)).append("</td>")
                    .append("<td class=\"module-bar-td\">")
                    .append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" class=\"bar-track\">")
                    .append("<tr>")
                    .append("<td width=\"")
                    .append(String.format(Locale.ROOT, "%.1f", Math.max(0, Math.min(100, value))))
                    .append("%\" class=\"bar-fill\"></td>")
                    .append("<td></td>")
                    .append("</tr>")
                    .append("</table>")
                    .append("</td>")
                    .append("<td class=\"module-val\">").append(number(value)).append(" / 100</td>")
                    .append("</tr>");
        }
        return rows.toString();
    }

    private String founderRows() {
        StringBuilder rows = new StringBuilder();
        appendRow(rows, "Nombre", properties.founderName());
        appendRow(rows, "Teléfono", properties.founderPhone());
        appendRow(rows, "Email", properties.founderEmail());
        appendRow(rows, "LinkedIn", properties.founderLinkedin());
        return rows.toString();
    }

    private void appendRow(StringBuilder rows, String label, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        rows.append("<tr>")
                .append("<td style=\"width:25%\"><span class=\"label\">")
                .append(esc(label))
                .append("</span></td>")
                .append("<td><span class=\"value\" style=\"font-size:11px\">")
                .append(esc(value))
                .append("</span></td>")
                .append("</tr>");
    }

    private String ctaMarkup() {
        String url = properties.founderBookingUrl();
        if (url == null || url.isBlank()) {
            return "";
        }
        return "<a class=\"cta\" href=\"" + esc(url) + "\">Agendar una llamada de asesoría</a>";
    }

    private String logoMarkup() {
        String logoPath = properties.logoPath();
        if (logoPath != null && !logoPath.isBlank()) {
            File file = new File(logoPath);
            if (file.isFile()) {
                try {
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    String mime = mimeFor(file.getName());
                    String dataUri = "data:" + mime + ";base64,"
                            + Base64.getEncoder().encodeToString(bytes);
                    return "<img src=\"" + dataUri + "\" alt=\"Ghost Load\" style=\"height:44px;width:auto\" />";
                } catch (IOException exception) {
                    LOGGER.warn("No se pudo leer el logo configurado: {}", logoPath);
                }
            } else {
                LOGGER.warn("El logo configurado no existe: {}", logoPath);
            }
        }
        return "<span style=\"font-size:22px;font-weight:bold;color:#d8b75b;letter-spacing:2px\">"
                + "GHOST LOAD</span>";
    }

    private String mimeFor(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/png";
    }

    private String maturityLabel(MaturityLevel level) {
        return MATURITY_LABELS.getOrDefault(level, level.name());
    }

    private String number(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private String percent(double value) {
        return String.format(Locale.ROOT, "%.1f %%", value);
    }

    private String money(double value) {
        return String.format(Locale.ROOT, "%,.0f", value);
    }

    private String mw(double value) {
        return String.format(Locale.ROOT, "%,.1f MW", value);
    }

    private String formatDate(Instant instant) {
        if (instant == null) {
            return "-";
        }
        return DATE_FORMAT.format(instant.atZone(ZoneOffset.UTC));
    }

    private String esc(String value) {
        return value == null ? "-" : HtmlUtils.htmlEscape(value);
    }
}

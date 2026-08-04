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

// Plantilla institucional del reporte en HTML/CSS. Colores de marca:
// forest-green (#153c32) y gold (#d8b75b). El logo se lee de un archivo
// configurado (PDF_LOGO_PATH) y, si no está, se usa un wordmark por CSS.
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
                    @page { size: A4; margin: 0; }
                    * { box-sizing: border-box; }
                    body { margin:0; font-family: Arial, 'DejaVu Sans', sans-serif;
                           font-size: 12px; color:#153c32; background:#f4f1e8; }
                    .header { background:#153c32; padding:26px 40px 22px; color:#ffffff; }
                    .tagline { color:#d8b75b; font-size:11px; letter-spacing:3px;
                                text-transform:uppercase; margin-top:4px; }
                    .content { padding:28px 40px 8px; }
                    .card { background:#ffffff; border:1px solid #ddd6c5; border-radius:6px;
                            padding:18px 22px; margin-bottom:18px; }
                    .section-title { font-size:13px; font-weight:bold; color:#153c32;
                                     text-transform:uppercase; letter-spacing:1px;
                                     border-left:4px solid #d8b75b; padding-left:10px;
                                     margin:0 0 14px; }
                    .label { color:#66736e; font-size:11px; text-transform:uppercase; }
                    .value { font-size:14px; font-weight:bold; color:#153c32; }
                    .kpi-table { width:100%%; border-collapse:collapse; }
                    .kpi-table td { padding:8px 12px; border:1px solid #eee8d8;
                                    vertical-align:top; width:50%%; }
                    .benchmark-head { width:100%%; border-collapse:collapse; }
                    .score-box { background:#153c32; border-radius:6px; padding:16px;
                                 text-align:center; width:38%%; vertical-align:middle; }
                    .score-number { font-size:40px; font-weight:bold; color:#d8b75b; }
                    .score-unit { font-size:11px; color:#cfded8; }
                    .bench-detail { padding-left:20px; vertical-align:middle; }
                    .maturity-badge { display:inline-block; background:#d8b75b; color:#153c32;
                                      font-weight:bold; padding:4px 12px; border-radius:4px;
                                      font-size:12px; }
                    .module-row td { padding:7px 4px; vertical-align:middle; }
                    .module-label { font-weight:bold; color:#153c32; width:42%%; }
                    .module-value { text-align:right; color:#153c32; width:10%%; }
                    .bar-track { background:#e9e3d3; border-radius:3px; height:10px; }
                    .bar-fill { background:#d8b75b; height:10px; border-radius:3px; }
                    .founder-box { background:#f6f3ea; border:1px solid #ddd6c5;
                                   border-radius:6px; padding:16px 18px; }
                    .cta { display:inline-block; background:#153c32; color:#d8b75b;
                           font-weight:bold; text-decoration:none; padding:10px 18px;
                           border-radius:4px; margin-top:10px; }
                    .footer { padding:10px 40px 30px; font-size:9px; color:#8a948f; }
                  </style>
                </head>
                <body>
                  <div class="header">
                    <table width="100%%" cellspacing="0" cellpadding="0">
                      <tr>
                        <td>__LOGO__</td>
                        <td align="right">
                          <div style="font-size:20px;font-weight:bold;color:#ffffff">Ghost Load</div>
                          <div class="tagline">Intelligence for AI Data Centers</div>
                        </td>
                      </tr>
                    </table>
                  </div>

                  <div class="content">
                    <div class="card">
                      <p class="section-title">Empresa y operador</p>
                      <table class="kpi-table" cellspacing="0" cellpadding="0">
                        <tr>
                          <td><span class="label">Empresa</span><br /><span class="value">__COMPANY__</span></td>
                          <td><span class="label">Operador</span><br /><span class="value">__FULL_NAME__</span></td>
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

                    <div class="card">
                      <p class="section-title">KPIs de la calculadora</p>
                      <table class="kpi-table" cellspacing="0" cellpadding="0">
                        <tr>
                          <td><span class="label">Capacidad total</span><br /><span class="value">__TOTAL_CAPACITY__</span></td>
                          <td><span class="label">Capacidad productiva</span><br /><span class="value">__PRODUCTIVE_CAPACITY__</span></td>
                        </tr>
                        <tr>
                          <td><span class="label">Capacidad no productiva</span><br /><span class="value">__NON_PRODUCTIVE_CAPACITY__</span></td>
                          <td><span class="label">Utilización</span><br /><span class="value">__UTILIZATION__</span></td>
                        </tr>
                        <tr>
                          <td><span class="label">Capacidad no productiva</span><br /><span class="value">__NON_PRODUCTIVE_PERCENT__</span></td>
                          <td><span class="label">Costo mensual por kW</span><br /><span class="value">__MONTHLY_COST__</span></td>
                        </tr>
                        <tr>
                          <td colspan="2"><span class="label">Costo anual estimado de capacidad desperdiciada</span><br /><span class="value">__ANNUAL_COST__</span></td>
                        </tr>
                      </table>
                    </div>

                    <div class="card">
                      <p class="section-title">Resultado del benchmark</p>
                      <table class="benchmark-head" cellspacing="0" cellpadding="0">
                        <tr>
                          <td class="score-box">
                            <div class="score-number">__TOTAL_SCORE__</div>
                            <div class="score-unit">/ 100 puntos</div>
                          </td>
                          <td class="bench-detail">
                            <div style="margin-bottom:6px"><span class="label">Nivel de madurez</span><br /><span class="maturity-badge">__MATURITY__</span></div>
                            <div><span class="label">Posición vs. industria</span><br /><span class="value">Percentil __PERCENTILE__</span></div>
                          </td>
                        </tr>
                      </table>
                      <table class="module-row" width="100%%" cellspacing="0" cellpadding="0" style="margin-top:16px">
                        __MODULE_ROWS__
                      </table>
                    </div>

                    <div class="card">
                      <p class="section-title">Hablemos de tu capacidad desperdiciada</p>
                      <div class="founder-box">
                        <table width="100%%" cellspacing="0" cellpadding="0">
                          __FOUNDER_ROWS__
                        </table>
                        __CTA__
                      </div>
                    </div>
                  </div>

                  <div class="footer">
                    <p style="margin:0">
                      Reporte generado el __DATE__ para __COMPANY_NAME__.
                      El percentil es una referencia del MVP y se actualizará con datos agregados
                      de la industria cuando exista una muestra suficiente.
                      Este documento contiene información confidencial de tu infraestructura.
                    </p>
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
            rows.append("<tr class=\"module-row\">")
                    .append("<td class=\"module-label\">").append(esc(label)).append("</td>")
                    .append("<td class=\"module-value\">").append(number(value)).append("</td>")
                    .append("</tr>")
                    .append("<tr>")
                    .append("<td colspan=\"2\">")
                    .append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" class=\"bar-track\">")
                    .append("<tr>")
                    .append("<td width=\"")
                    .append(String.format(Locale.ROOT, "%.1f", Math.max(0, Math.min(100, value))))
                    .append("%\" class=\"bar-fill\"></td>")
                    .append("<td></td>")
                    .append("</tr>")
                    .append("</table>")
                    .append("</td>")
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
                .append("<td style=\"width:30%\"><span class=\"label\">")
                .append(esc(label))
                .append("</span></td>")
                .append("<td><span class=\"value\">")
                .append(esc(value))
                .append("</span></td>")
                .append("</tr>");
    }

    private String ctaMarkup() {
        String url = properties.founderBookingUrl();
        if (url == null || url.isBlank()) {
            return "";
        }
        return "<a class=\"cta\" href=\"" + esc(url) + "\">Agendar una llamada</a>";
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
                    return "<img src=\"" + dataUri + "\" alt=\"Ghost Load\" style=\"height:52px;width:auto\" />";
                } catch (IOException exception) {
                    LOGGER.warn("No se pudo leer el logo configurado: {}", logoPath);
                }
            } else {
                LOGGER.warn("El logo configurado no existe: {}", logoPath);
            }
        }
        return "<span style=\"font-size:26px;font-weight:bold;color:#d8b75b;letter-spacing:2px\">"
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

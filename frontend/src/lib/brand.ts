// ---------------------------------------------------------------------------
// Brand + founder contact constants for institutional-facing surfaces (the
// PDF report today; anywhere else "Project Ghost Load" needs to introduce
// itself tomorrow). Centralized so the on-screen preview, the printed/PDF
// export, and any future channel (email footer, admin settings) always
// read the same values instead of re-typing them per component.
//
// TODO(founder): swap the placeholder LinkedIn/scheduling links below for
// the real ones before this ships to operators.
// ---------------------------------------------------------------------------

export const REPORT_BRAND = {
  productName: "Project Ghost Load",
  reportTitle: "Benchmark de Infraestructura de Data Center IA",
  documentLabel: "Reporte Institucional",
  confidentialityNote: "Documento confidencial — preparado exclusivamente para el destinatario.",
};

export const FOUNDER_CONTACT = {
  name: "Founder — Project Ghost Load",
  role: "Fundador",
  email: "founder@ghostload.com",
  phone: "+1 (415) 555-0148",
  linkedinUrl: "https://www.linkedin.com/company/ghost-load",
  scheduleCallUrl: "https://cal.com/ghostload/intro",
};

/** Builds a short, human-readable document reference for the report footer/header. */
export function buildReportReference(companyId: string | undefined, generatedAtIso: string): string {
  const datePart = generatedAtIso.slice(0, 10).replace(/-/g, "");
  const idPart = (companyId ?? "unregistered").toUpperCase().replace(/[^A-Z0-9]/g, "").slice(-6) || "000000";
  return `GL-${datePart}-${idPart}`;
}

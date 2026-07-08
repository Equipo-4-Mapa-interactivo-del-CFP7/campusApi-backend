package com.cfp.mapa.model.enums;

public enum TipoAccionAuditoria {
  USUARIO_CREADO("creó un nuevo usuario"),
  PASSWORD_RESTABLECIDA("restableció la contraseña de un usuario"),
  ESTADO_ACTIVO_MODIFICADO("modificó el estado de activación de la cuenta"),
  PASSWORD_CAMBIADA("cambió su propia contraseña"),
  ROL_MODIFICADO("cambió el rol de un usuario"),
  USUARIO_ELIMINADO("eliminó a un usuario"),
  PASSWORD_OWNER_RECUPERADA("recuperó la contraseña de OWNER mediante clave de emergencia"),
  OWNER_TRANSFERIDO("transfirió su rol de OWNER a un usuario"),
  DNI_EDITADO("actualizó el número de DNI"),
  NOMBRE_APELLIDO_EDITADO("actualizó los datos de nombre y apellido"),
  REPORTE_CREADO("creó un reporte en el sistema"),
  REPORTE_ATENDIDO("comenzó a atender un reporte"),
  REPORTE_MODIFICADO("modificó los datos del reporte"),
  REPORTE_TIEMPO_ELIMINADO("eliminó el límite de tiempo del reporte"),
  REPORTE_CERRADO("cerró un reporte en el sistema"),
  REPORTE_CERRADO_AUTOMATICO("el sistema cerró automáticamente el reporte por expiración de tiempo");

  private final String descripcion;

  TipoAccionAuditoria(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }
}
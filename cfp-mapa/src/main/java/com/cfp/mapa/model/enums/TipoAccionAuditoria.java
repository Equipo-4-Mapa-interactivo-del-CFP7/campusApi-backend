package com.cfp.mapa.model.enums;

public enum TipoAccionAuditoria {
  USUARIO_CREADO("creó un nuevo usuario"),
  PASSWORD_RESTABLECIDA("restableció la contraseña de un usuario"),
  ESTADO_ACTIVO_MODIFICADO("modificó el estado activo de un usuario"),
  PASSWORD_CAMBIADA("cambió su propia contraseña"),
  ROL_MODIFICADO("cambió el rol de un usuario"),
  PERFIL_VISUALIZADO("consultó el perfil de un usuario"),
  USUARIO_ELIMINADO("eliminó a un usuario"),
  PASSWORD_OWNER_RECUPERADA("recuperó la contraseña de OWNER mediante clave de emergencia"),
  OWNER_TRANSFERIDO("trasfirió su rol de OWNER a un usuario"),
  DNI_EDITADO("cambió el dni de un usuario"),
  NOMBRE_APELLIDO_EDITADO("cambió el nombre y apellido de un usuario");

  private final String descripcion;

  TipoAccionAuditoria(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }
}
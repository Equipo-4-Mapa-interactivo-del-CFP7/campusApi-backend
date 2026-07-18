package com.cfp.mapa.service;

import com.cfp.mapa.dto.espacio.*;
import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.enums.EstadoEspacio;
import com.cfp.mapa.model.enums.TipoEspacio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EspacioService {

    // =========================
    // ADMIN
    // =========================

    Page<EspacioResponseDTO> listarEspacios(
            String nombre,
            String descripcion,
            TipoEspacio tipo,
            Boolean accesible,
            EstadoEspacio estado,
            Pageable pageable
    );

    EspacioResponseDTO actualizarEspacio(Long id, EspacioUpdateDTO dto);

    void cambiarEstado(Long id, EstadoEspacio estado);

    // =========================
    // PERSONAL
    // =========================

    EspacioDetalleDTO obtenerEspacioPorId(Long id);

    List<EspacioMapaDTO> obtenerMapa(TipoEspacio tipo);

    List<EspacioMapaDTO> buscarEspacios(String nombre);
}

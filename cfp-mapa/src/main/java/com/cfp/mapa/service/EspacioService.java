package com.cfp.mapa.service;

import com.cfp.mapa.dto.espacio.*;
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
            Boolean activo,
            Pageable pageable
    );

    EspacioResponseDTO crearEspacio(EspacioRequestDTO dto);

    EspacioResponseDTO actualizarEspacio(
            Long id,
            EspacioUpdateDTO dto
    );

    void activarEspacio(Long id);

    void desactivarEspacio(Long id);

    void eliminarEspacio(Long id);

    // =========================
    // PERSONAL
    // =========================

    EspacioDetalleDTO obtenerEspacioPorId(Long id);

    List<EspacioMapaDTO> obtenerMapa(
            TipoEspacio tipo
    );
}

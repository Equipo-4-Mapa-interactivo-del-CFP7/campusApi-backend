package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.conexion.ConexionMapaDTO;
import com.cfp.mapa.dto.espacio.EspacioMapaDTO;
import com.cfp.mapa.dto.recorrido.RutaResponseDTO;
import com.cfp.mapa.exception.EspacioNotFoundException;
import com.cfp.mapa.exception.RutaNoEncontradaException;
import com.cfp.mapa.mapper.ConexionMapper;
import com.cfp.mapa.mapper.EspacioMapper;
import com.cfp.mapa.model.Conexion;
import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.enums.EstadoConexion;
import com.cfp.mapa.model.enums.EstadoEspacio;
import com.cfp.mapa.repository.ConexionRepository;
import com.cfp.mapa.repository.EspacioRepository;
import com.cfp.mapa.service.RecorridoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecorridoServiceImpl implements RecorridoService {

    private final EspacioRepository espacioRepository;
    private final ConexionRepository conexionRepository;

    private final EspacioMapper espacioMapper;
    private final ConexionMapper conexionMapper;

    @Override
    @Transactional(readOnly = true)
    public RutaResponseDTO calcularRuta(Long origenId, Long destinoId, Boolean soloAccesible) {

        Espacio origen = espacioRepository.findById(origenId).orElseThrow(() ->
                new EspacioNotFoundException(origenId));

        Espacio destino = espacioRepository.findById(destinoId).orElseThrow(() ->
                new EspacioNotFoundException(destinoId));

        List<Conexion> conexiones;

        if (Boolean.TRUE.equals(soloAccesible)) {
            conexiones = conexionRepository.findByEstadoAndAccesibleTrueOrderByIdAsc(EstadoConexion.ACTIVA);
        } else {
            conexiones = conexionRepository.findByEstadoOrderByIdAsc(EstadoConexion.ACTIVA);
        }

        Map<Long, List<Conexion>> grafo = construirGrafo(conexiones);

        boolean accesible = Boolean.TRUE.equals(soloAccesible);

        List<Long> recorrido = buscarCamino(origen.getId(), destino.getId(), grafo, accesible);

        List<Conexion> conexionesRuta = obtenerConexionesRuta(recorrido, conexiones);

        Double distancia = calcularDistancia(conexionesRuta);

        List<EspacioMapaDTO> espaciosDTO =
                recorrido.stream()
                        .map(id -> espacioRepository.findById(id)
                                .orElseThrow(() -> new EspacioNotFoundException(id)))
                        .map(espacioMapper::espacioToMapaDTO)
                        .toList();

        List<ConexionMapaDTO> conexionesDTO =
                conexionesRuta.stream()
                        .map(conexionMapper::conexionToMapaDTO)
                        .toList();

        return new RutaResponseDTO(espaciosDTO, distancia);
    }

    private Map<Long, List<Conexion>> construirGrafo(List<Conexion> conexiones) {

        Map<Long, List<Conexion>> grafo = new HashMap<>();

        for (Conexion conexion : conexiones) {

            if (conexion.getOrigen().getEstado() == EstadoEspacio.INHABILITADO ||
                    conexion.getDestino().getEstado() == EstadoEspacio.INHABILITADO) {
                continue;
            }

            grafo.computeIfAbsent(conexion.getOrigen().getId(), k ->
                            new ArrayList<>()).add(conexion);

            // Conexión inversa
            Conexion inversa = new Conexion();
            inversa.setOrigen(conexion.getDestino());
            inversa.setDestino(conexion.getOrigen());

            inversa.setTipoTransito(conexion.getTipoTransito());
            inversa.setDistancia(conexion.getDistancia());
            inversa.setAncho(conexion.getAncho());
            inversa.setCumpleLey962(conexion.getCumpleLey962());
            inversa.setAccesible(conexion.getAccesible());
            inversa.setEstado(conexion.getEstado());

            grafo.computeIfAbsent(inversa.getOrigen().getId(), k ->
                            new ArrayList<>()).add(inversa);
        }
        return grafo;
    }

    private List<Long> buscarCamino(Long origenId, Long destinoId, Map<Long, List<Conexion>> grafo, boolean soloAccesible) {

        Map<Long, Double> distancia = new HashMap<>();
        Map<Long, Long> anterior = new HashMap<>();

        PriorityQueue<Long> cola = new PriorityQueue<>(Comparator.comparingDouble(distancia::get));

        for (Long id : grafo.keySet()) {
            distancia.put(id, Double.MAX_VALUE);
        }

        distancia.put(origenId, 0.0);
        cola.add(origenId);

        while (!cola.isEmpty()) {

            Long actual = cola.poll();

            if (actual.equals(destinoId))
                break;

            for (Conexion conexion : grafo.getOrDefault(actual, List.of())) {

                Long vecino = conexion.getDestino().getId();
                double nuevaDistancia = distancia.get(actual) + calcularCosto(conexion, soloAccesible);

                if (nuevaDistancia < distancia.getOrDefault(vecino, Double.MAX_VALUE)) {

                    distancia.put(vecino, nuevaDistancia);
                    anterior.put(vecino, actual);
                    cola.remove(vecino);
                    cola.add(vecino);
                }
            }
        }

        if (!origenId.equals(destinoId) && !anterior.containsKey(destinoId)) {

            throw new RutaNoEncontradaException(origenId, destinoId);
        }

        return reconstruirCamino(destinoId, anterior);
    }

    private List<Long> reconstruirCamino(Long destinoId, Map<Long, Long> anterior) {

        List<Long> camino = new ArrayList<>();

        Long actual = destinoId;

        while (actual != null) {
            camino.add(actual);
            actual = anterior.get(actual);
        }

        Collections.reverse(camino);
        return camino;
    }

    private List<Conexion> obtenerConexionesRuta(List<Long> recorrido, List<Conexion> conexiones) {

        List<Conexion> resultado = new ArrayList<>();

        for (int i = 0; i < recorrido.size() - 1; i++) {

            Long actual = recorrido.get(i);
            Long siguiente = recorrido.get(i + 1);

            conexiones.stream().filter(c -> (c.getOrigen().getId().equals(actual) && c.getDestino().getId().equals(siguiente)) || (c.getOrigen().getId().equals(siguiente) && c.getDestino().getId().equals(actual))).findFirst().ifPresent(resultado::add);
        }

        return resultado;
    }

    private Double calcularDistancia(List<Conexion> conexionesRuta) {

        return conexionesRuta.stream()
                .mapToDouble(Conexion::getDistancia)
                .sum();
    }

    private double calcularCosto(Conexion conexion, boolean soloAccesible) {

        double costo = conexion.getDistancia();

        if (!soloAccesible) {
            return costo;
        }

        switch (conexion.getTipoTransito()) {

            case RAMPA:
                return costo;
            case PASILLO:
                return costo * 1.0;
            case EXTERIOR:
                return costo * 1.3;
            case RIPIO:
                return costo * 3.0;
            default:
                return costo;
        }
    }
}
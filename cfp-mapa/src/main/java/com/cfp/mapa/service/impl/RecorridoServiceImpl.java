package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.conexion.ConexionResponseDTO;
import com.cfp.mapa.dto.espacio.EspacioMapaDTO;
import com.cfp.mapa.dto.recorrido.RutaResponseDTO;
import com.cfp.mapa.exception.EspacioNotFoundException;
import com.cfp.mapa.mapper.ConexionMapper;
import com.cfp.mapa.mapper.EspacioMapper;
import com.cfp.mapa.model.Conexion;
import com.cfp.mapa.model.Espacio;
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

        List<Conexion> conexiones = conexionRepository.findByActivaTrue();

        if (Boolean.TRUE.equals(soloAccesible)) {

            conexiones = conexiones.stream()
                    .filter(Conexion::getAccesible)
                    .toList();
        }

        Map<Espacio, List<Conexion>> grafo = construirGrafo(conexiones);

        List<Espacio> recorrido = buscarCamino(origen, destino, grafo);

        List<Conexion> conexionesRuta = obtenerConexionesRuta(recorrido, conexiones);

        Double distancia = calcularDistancia(conexionesRuta);

        List<EspacioMapaDTO> espaciosDTO =
                recorrido.stream()
                        .map(espacioMapper::espacioToMapaDTO)
                        .toList();

        List<ConexionResponseDTO> conexionesDTO =
                conexionesRuta.stream()
                        .map(conexionMapper::conexionToResponse)
                        .toList();

        return new RutaResponseDTO(espaciosDTO, conexionesDTO, distancia
        );
    }

    private Map<Espacio, List<Conexion>> construirGrafo(
            List<Conexion> conexiones) {
        Map<Espacio, List<Conexion>> grafo = new HashMap<>();

        for (Conexion conexion : conexiones) {

            grafo.computeIfAbsent(conexion.getOrigen(), _ ->
                    new ArrayList<>()).add(conexion);

            // Creamos conexión inversa
            Conexion inversa = new Conexion();
            inversa.setOrigen(conexion.getDestino());
            inversa.setDestino(conexion.getOrigen());
            inversa.setDistancia(conexion.getDistancia());
            inversa.setAccesible(conexion.getAccesible());
            inversa.setActiva(conexion.getActiva());

            grafo.computeIfAbsent(inversa.getOrigen(), _ ->
                    new ArrayList<>()).add(inversa);
        }
        return grafo;
    }

    private List<Espacio> buscarCamino(Espacio origen, Espacio destino, Map<Espacio, List<Conexion>> grafo) {

        Queue<Espacio> cola = new LinkedList<>();
        Map<Espacio, Espacio> anterior = new HashMap<>();

        Set<Espacio> visitados = new HashSet<>();

        cola.add(origen);
        visitados.add(origen);

        while (!cola.isEmpty()) {
            Espacio actual = cola.poll();

            if (actual.equals(destino)) {
                break;
            }

            for (Conexion conexion : grafo.getOrDefault(actual, List.of())) {
                Espacio siguiente = conexion.getDestino();

                if (!visitados.contains(siguiente)) {

                    visitados.add(siguiente);
                    anterior.put(siguiente, actual);
                    cola.add(siguiente);
                }
            }
        }

        return reconstruirCamino(destino, anterior);
    }

    private List<Espacio> reconstruirCamino(Espacio destino, Map<Espacio, Espacio> anterior) {

        List<Espacio> camino = new ArrayList<>();

        Espacio actual = destino;

        while (actual != null) {
            camino.add(actual);
            actual = anterior.get(actual);
        }

        Collections.reverse(camino);
        return camino;
    }

    private List<Conexion> obtenerConexionesRuta(List<Espacio> recorrido, List<Conexion> conexiones) {
        List<Conexion> resultado = new ArrayList<>();

        for(int i = 0; i < recorrido.size() - 1; i++) {

            Espacio actual = recorrido.get(i);
            Espacio siguiente = recorrido.get(i + 1);

            conexiones.stream()
                    .filter(c -> c.getOrigen().equals(actual) && c.getDestino().equals(siguiente))
                    .findFirst()
                    .ifPresent(resultado::add);

        }
        return resultado;
    }

    private Double calcularDistancia(
            List<Conexion> conexionesRuta) {

        return conexionesRuta.stream()
                .mapToDouble(Conexion::getDistancia)
                .sum();
    }
}
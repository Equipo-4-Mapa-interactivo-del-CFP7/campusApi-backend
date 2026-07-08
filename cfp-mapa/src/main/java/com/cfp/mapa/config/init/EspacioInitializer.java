package com.cfp.mapa.config.init;

import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.enums.TipoEspacio;
import com.cfp.mapa.repository.EspacioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

@Profile("local")
@Component
public class EspacioInitializer implements CommandLineRunner {


  private final EspacioRepository espacioRepository;

  public EspacioInitializer(EspacioRepository espacioRepository) {
    this.espacioRepository = espacioRepository;
  }

  @Override
  public void run(String... args) throws Exception {

    System.out.println("SE CREAN LOS ESPACIOS DE PRUEBA LOCAL");

    if (espacioRepository.count() == 0) {

      // Espacio 1: Laboratorio
      Espacio espacio1 = new Espacio(
          null,
          "Laboratorio de Computación 1",
          "Laboratorio equipado con 20 PCs de desarrollo",
          TipoEspacio.TALLER,
          -34.6037,
          -58.3816,
          true,
          true,
          new ArrayList<>(),
          new ArrayList<>()
      );

      // Espacio 2: Aula Comun
      Espacio espacio2 = new Espacio(
          null,
          "Aula Magna",
          "Pabellón Central - Planta Baja",
          TipoEspacio.AULA,
          -34.6040,
          -58.3820,
          true,
          true,
          new ArrayList<>(),
          new ArrayList<>()
      );

      espacioRepository.save(espacio1);
      espacioRepository.save(espacio2);

      System.out.println(">> [Initializer] Se han creado 2 espacios de prueba respetando el modelo.");
    }
  }
}
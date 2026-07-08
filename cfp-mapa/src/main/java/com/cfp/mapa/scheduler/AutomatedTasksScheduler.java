package com.cfp.mapa.scheduler;

import com.cfp.mapa.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AutomatedTasksScheduler {

  private final ReporteService reporteService;

  @Scheduled(
      initialDelayString = "${app.scheduler.initial-delay}",
      fixedRateString = "${app.shceduler.fixed-rate}"
  )
    public void ejecutarTareasCadaMinuto() {

    reporteService.cerrarReportesAutomaticamente();
  }
}

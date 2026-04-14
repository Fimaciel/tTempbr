package com.exemplo.service;

import com.exemplo.model.SystemSnapshot;

/**
 * Abstração para leitura de dados de hardware.
 * Permite trocar a implementação (ex: mock para testes) sem impactar o resto da aplicação.
 */
public interface HardwareService {

    SystemSnapshot snapshot();

    String getProcessorInfo();

    String getOSInfo();
}

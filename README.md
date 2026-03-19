# Monitor de Temperatura do Sistema

Aplicação Java com JavaFX para monitorar a temperatura dos componentes do computador em tempo real.

## Funcionalidades

- 🌡️ Monitoramento de temperatura da CPU
- 📊 Exibição da carga da CPU
- 💻 Compatível com Linux e Windows

## Requisitos

- Java 21 ou superior
- Maven 3.6+

## Como executar

```bash
mvn javafx:run
```

## Código de cores da temperatura

- 🟢 Verde: < 50°C (temperatura normal)
- 🟠 Laranja: 50-70°C (temperatura elevada)
- 🔴 Vermelho: > 70°C (temperatura alta)

## Observações

- Em alguns sistemas, pode ser necessário executar com privilégios administrativos para acessar os sensores de temperatura
- Nem todos os hardwares expõem sensores de temperatura através das APIs do sistema operacional
- A disponibilidade de informações pode variar dependendo do sistema e dos drivers instalados

## Tecnologias utilizadas

- Java 21
- JavaFX 21
- OSHI (Operating System and Hardware Information)
- Maven

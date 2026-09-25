# StockAndes - Backend REST Empresarial

API REST empresarial para el control de inventarios del almacén central de StockAndes, desarrollada con Spring Boot 4, Java 21 y Oracle Database
con la finalidad de aprobar el curso de Lenguaje de programacion profesor a cargo David Reyna Barreto , examen realizado por Andrew Serna Pereda
---

## 1. Estructura de Paquetes

```text
src/main/java/pe/edu/upeu/InventarioBackend/
├── config/
│   ├── CorsConfig.java
│   └── OpenApiConfig.java
├── controller/
│   ├── AreaController.java
│   ├── CategoriaController.java
│   ├── DespachoController.java
│   ├── HealthController.java
│   ├── ProductoController.java
│   └── ReporteController.java
├── dto/
│   ├── CategoriaRequestDTO.java
│   ├── CategoriaResponseDTO.java
│   ├── DespachoRequestDTO.java
│   ├── DespachoResponseDTO.java
│   ├── DetalleDespachoRequestDTO.java
│   ├── ProductoDespachadoDTO.java
│   └── ProductoRequestDTO.java
├── entity/
│   ├── Area.java
│   ├── Categoria.java
│   ├── Despacho.java
│   ├── DetalleDespacho.java
│   └── Producto.java
├── enums/
│   └── EstadoDespacho.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── RecursoNoEncontradoException.java
│   ├── ReglaNegocioException.java
│   └── dto/
│       └── ErrorResponseDTO.java
├── repository/
│   ├── AreaRepository.java
│   ├── CategoriaRepository.java
│   ├── DespachoRepository.java
│   └── ProductoRepository.java
└── service/
    ├── generic/
    │   └── CrudService.java
    ├── service/
    │   ├── AreaService.java
    │   ├── CategoriaService.java
    │   ├── DespachoService.java
    │   └── ProductoService.java
    └── impl/
        ├── AreaServiceImpl.java
        ├── CategoriaServiceImpl.java
        ├── DespachoServiceImpl.java
        └── ProductoServiceImpl.java

src/main/resources/
├── application.yaml
├── application-dev.yaml
└── application-prod.yaml
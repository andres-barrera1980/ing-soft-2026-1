# Parcial 2 — Ingeniería de Software con LLMs

## Datos del estudiante

| Campo | Valor |
|---|---|
| **Nombre completo** | Boris Nicolas Valbuena Gueirsman |
| **Fecha de entrega** | [FECHA] |

## LLM principal utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Es la que más usé durante el curso, por lo cual es la misma que usaré en el parcial. |

## Estructura de la entrega

| Pregunta | Tema | Puntos | Recomendada sin IA | Archivo |
|---|---|---|---|---|
| P.1 | SOLID — Principio en GestorLibro | 3 | ⭐ | `P01_solid_principio/respuesta.md` |
| P.2 | SOLID — Refactoring ProcesadorPago | 4 | | `P02_solid_refactor/respuesta.md` |
| P.3 | SOLID — Jerarquía de usuarios | 3 | ⭐ | `P03_solid_jerarquia/respuesta.md` |
| P.4 | Patrones — Estrategias de descuento | 3 | | `P04_patrones_descuento/respuesta.md` |
| P.5 | Patrones — Notificaciones combinadas | 5 | | `P05_patrones_notificaciones/respuesta.md` |
| P.6 | Clean Architecture — Capas | 4 | ⭐ | `P06_clean_architecture/respuesta.md` |
| P.7 | Clean Architecture — Pagos | 4 | | `P07_clean_architecture_pagos/respuesta.md` |
| P.8 | Pruebas — CarritoService | 5 | | `P08_pruebas_carrito/respuesta.md` |
| P.9 | TDD — ControlInventario | 4 | ⭐ | `P09_tdd_inventario/respuesta.md` |
| P.10 | VM vs Containers | 5 | | `P10_vm_vs_containers/respuesta.md` |
| P.11 | Pruebas manuales vs automatizadas | 4 | ⭐ | `P11_pruebas_manual_vs_auto/respuesta.md` |
| P.12 | Ciclo de vida de defectos | 3 | ⭐ | `P12_ciclo_vida_defectos/respuesta.md` |
| P.13 | Integración — Refactoring | 3 | | `P13_integracion_refactoring/respuesta.md` |
| | **TOTAL** | **50** | | |

> ⭐ = Recomendada para responder **sin usar IA** (bono +20% en esa pregunta).

## Instrucciones de entrega

### 1. Crear tu rama

Desde la terminal, clona el repo y crea tu rama desde `develop`:

```bash
git clone git@github.com:andres-barrera1980/ing-soft-2026-1.git
cd ing-soft-2026-1
git checkout develop
git checkout -b parcial2/apellido-nombre
```

Ejemplo para el estudiante **Andrés Barrera**:

```bash
git checkout -b parcial2/barrera-andres
```

### 2. Copiar las plantillas

```bash
cp -r parcial2/template parcial2/barrera-andres
```

### 3. Resolver el parcial

Completa cada archivo `respuesta.md` siguiendo la plantilla. Puedes responder con LLM o sin IA en cada pregunta. Si respondes sin IA, omite las secciones de prompt y respuesta.

### 4. Hacer commits

Haz **al menos 2 commits por pregunta** mostrando la evolución de tu trabajo.

### 5. Entregar

Haz push de tu rama a `origin` antes de la fecha límite:

```bash
git add .
git commit -m "Parcial 2 — Andrés Barrera"
git push -u origin parcial2/barrera-andres
```

```
parcial2/
├── template/                  ← carpeta base (no modificar)
│   ├── README.md
│   ├── P01_solid_principio/respuesta.md
│   ├── ...
│   └── P13_integracion_refactoring/respuesta.md
└── barrera-andres/            ← tu carpeta de trabajo
    ├── README.md
    ├── P01_solid_principio/respuesta.md
    ├── ...
    └── P13_integracion_refactoring/respuesta.md
```

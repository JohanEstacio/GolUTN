# GolUTN — Frontend Administrativo (JSF)

Panel de control administrativo del proyecto **GolUTN Mundial 2026**. Es una
aplicación **Jakarta EE 10 / Jakarta Faces 4** (JSF) con **PrimeFaces 13**,
empaquetada como WAR y desplegada en **WildFly**. No tiene base de datos ni
lógica de negocio propia: todo el estado real vive en dos servicios REST
externos que este panel consume por HTTP.

## 1. Requisitos

| Herramienta | Versión | Notas |
|---|---|---|
| JDK | 17 | `maven.compiler.source`/`target` están fijados en 17 en el `pom.xml`. |
| Maven | 3.8+ | Para compilar y empaquetar el WAR. |
| WildFly | 27 o superior | Es la primera versión con soporte Jakarta EE 10 / Jakarta Faces 4, requerido por `jakarta.jakartaee-api:10.0.0`. |

Todas las dependencias de Jakarta EE, Jakarta Faces (Mojarra) y el cliente
REST (RESTEasy) están declaradas con `scope=provided` en el `pom.xml`: **no
se empaquetan dentro del WAR**, se usan los módulos que WildFly ya trae
integrados. Esto evita conflictos de classloader, pero también significa que
la app **no arrancará en un Tomcat plano** ni en un WildFly con esos
subsistemas deshabilitados.


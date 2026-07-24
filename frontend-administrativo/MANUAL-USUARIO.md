# Manual de usuario — Panel Administrativo GolUTN

Este manual recorre pantalla por pantalla el panel administrativo de GolUTN
Mundial 2026, pensado para el equipo que administra el torneo (no para
jugadores ni apostadores finales, que usan otra aplicación). El acceso está
restringido a usuarios con rol **Administrador**.

## 1. Ingreso al panel (Login)

Al entrar a la aplicación, se muestra la pantalla de **Acceso**
(`login.xhtml`) con el logo de GolUTN y el formulario de ingreso:

1. Completá **Usuario** con tu username.
2. Completá **Contraseña**.
3. Presioná **Ingresar al panel**.

Si el usuario o la contraseña no son correctos, o si la cuenta no existe,
aparece un mensaje de error arriba del formulario (por ejemplo
"Credenciales incorrectas"). Si el servicio de autenticación no responde, el
mensaje indica que el servicio no está disponible por el momento — en ese
caso hay que reintentar más tarde o avisar a soporte técnico.

Solo pueden entrar cuentas con:

- Rol **Administrador**.
- Estado **activo**.

Cualquier otra cuenta (usuario común, o un administrador desactivado) recibe
un mensaje de "Acceso restringido" y no entra al panel, aunque la contraseña
sea correcta.

Una vez adentro, la sesión se mantiene activa durante **30 minutos de
inactividad**. Pasado ese tiempo, la próxima acción te devuelve al login.

## 2. Navegación general

Todas las pantallas del panel comparten el mismo layout:

- **Barra superior**: el logo de GolUTN, tu nombre de usuario, y el enlace
  **Cerrar sesión** (arriba a la derecha) para salir del panel en cualquier
  momento.
- **Menú lateral izquierdo** ("Gestión del torneo"), con acceso directo a
  cada módulo: Inicio, Gestión de partidos, Gestión de usuarios, Reportes y
  auditoría, Auditoría, y Bono anti-bancarrota. La sección en la que estás
  parado se resalta en el menú.
- **Zona de mensajes**: cualquier aviso de éxito, advertencia o error de la
  acción que acabás de hacer aparece arriba del contenido de la pantalla.

## 3. Dashboard (Inicio)

Es la primera pantalla que ves al iniciar sesión. Muestra un resumen rápido
del estado del torneo:

- **Tarjetas de resumen**: cantidad de partidos en el calendario, cantidad
  de selecciones participantes, y cantidad de grupos. Si alguno de estos
  datos no se pudo obtener del backend en ese momento, la tarjeta muestra un
  guion (`—`) en vez de un número, para que sepas que es un dato no
  disponible y no un cero real.
- **Accesos rápidos**: tres atajos directos a Gestionar partidos, Gestionar
  usuarios y Consultar reportes, para no tener que pasar siempre por el
  menú lateral.

## 4. Gestión de partidos y registro de resultados

Desde el menú lateral, entrá a **Gestión de partidos**. La pantalla muestra
una tabla con todo el calendario: selección local, visitante, sede, fase,
fecha y hora, estado (con una etiqueta de color) y el marcador (o "Sin
jugar" si todavía no se disputó).

### Crear un partido nuevo

1. Presioná **Nuevo partido** (arriba de la tabla).
2. Completá el formulario emergente:
   - **Número FIFA**: número de partido según la numeración oficial del
     torneo.
   - **Fase**: fase de grupos, octavos, cuartos, semifinal o final.
   - **Grupo**: solo aplica a partidos de la fase de grupos. Para partidos
     de fases eliminatorias (octavos en adelante, que no pertenecen a
     ningún grupo), dejá la opción **"— Sin grupo —"**; el campo no es
     obligatorio.
   - **Sede**, **Selección local** y **Selección visitante**: se eligen de
     listas desplegables ya cargadas con el catálogo del torneo.
   - **Fecha y hora** del partido.
   - **Estado**: por defecto queda en **Programado**. Los estados
     disponibles para elegir manualmente son Programado, En juego,
     Suspendido y Cancelado. **Finalizado no aparece en esta lista**: ese
     estado lo asigna automáticamente el sistema cuando se registra el
     resultado oficial (ver más abajo), para asegurar que las predicciones
     de los usuarios se liquiden correctamente. No hay forma de marcar un
     partido como finalizado a mano desde este formulario.
3. Presioná **Guardar partido**. Si todo está completo (selección local y
   visitante distintas entre sí, y fecha indicada), el partido se agrega al
   calendario y el diálogo se cierra solo. Si falta algo, un mensaje indica
   qué corregir y el diálogo permanece abierto.

### Editar un partido existente

1. En la fila del partido, presioná **Editar**. Se abre el mismo formulario,
   precargado con los datos actuales.
2. Si el partido ya está **Finalizado**, vas a ver un aviso: modificarlo
   puede afectar resultados y predicciones ya liquidadas. En ese caso, el
   campo Estado queda bloqueado (no editable) para que no se pueda revertir
   accidentalmente un partido finalizado a otro estado; el resto de los
   campos sí se pueden corregir.
3. Presioná **Guardar partido** para confirmar los cambios.

### Registrar el resultado oficial

1. En la fila del partido, presioná **Registrar resultado**.
2. Se abre un diálogo simple con el nombre de los dos equipos y dos campos
   numéricos: **Goles local** y **Goles visitante**.
3. Presioná **Guardar resultado**. Al confirmarse, el sistema actualiza la
   tabla de posiciones y liquida automáticamente las predicciones de los
   usuarios asociadas a ese partido, y el partido pasa a estado
   **Finalizado**.

> Nota: la lista de **Selecciones** (los 48 equipos participantes), las
> **Fases**, **Grupos** y **Sedes** no tienen una pantalla propia de alta o
> edición en este panel: son catálogos que se consultan y se usan como
> listas desplegables al crear o editar un partido, como se describió
> arriba.

## 5. Gestión de usuarios

Desde el menú lateral, entrá a **Gestión de usuarios**. La tabla lista cada
cuenta registrada con su nombre, correo, rol y estado activo/inactivo.

Para cada usuario podés, en la misma fila:

- Cambiar su **Rol** (Administrador o Usuario) desde el desplegable.
- Marcar o desmarcar la casilla **Activo** para habilitar o deshabilitar su
  acceso al sistema (un usuario inactivo no puede iniciar sesión en ninguna
  de las dos aplicaciones del proyecto).
- Presionar **Guardar** en esa fila para confirmar el cambio. Un mensaje
  confirma que el rol se actualizó, o avisa si no se pudo guardar por un
  problema de conexión con el servicio.

## 6. Auditoría

Desde el menú lateral, entrá a **Auditoría**. Esta pantalla muestra el
historial completo de cambios registrados por el sistema: fecha, usuario
que hizo el cambio, tipo de acción (crear/actualizar), tabla afectada y
número de registro. La tabla se ordena por fecha, del más reciente al más
antiguo, y se puede paginar.

Para ver el detalle completo de un cambio puntual, presioná **Ver cambios**
en esa fila. Se abre un diálogo con:

- Los datos del registro (tabla, número de registro, acción, fecha).
- **Datos anteriores** y **Datos nuevos**: el contenido completo antes y
  después del cambio, en formato legible, útil para entender exactamente
  qué se modificó (por ejemplo, un resultado de partido o un cambio de rol
  de usuario).

## 7. Reportes

Desde el menú lateral, entrá a **Reportes y auditoría**. Es una pantalla de
solo lectura con indicadores generales del torneo y de la moneda virtual:

- **Predicciones totales** realizadas por los usuarios.
- **UTNGolCoin en circulación**, el total de la moneda virtual entre todas
  las billeteras.
- **Usuarios registrados**.
- **Partidos finalizados**.
- Un destacado con el **partido con más predicciones** hechas por los
  usuarios.

## 8. Simulación del bono anti-bancarrota

Desde el menú lateral, entrá a **Bono anti-bancarrota**. Esta pantalla
existe para poder mostrar y probar la regla de negocio del bono
anti-bancarrota sin tener que esperar días reales: cada usuario cuya
billetera de UTNGolCoin llega a saldo cero recibe automáticamente 1
UTNGolCoin al pasar un día.

La pantalla muestra:

- El **día simulado actual** (por ejemplo "Día simulado: 24/07/2026") y
  cuántos días de simulación se llevan avanzados desde que se abrió la
  pantalla.
- Un resumen de la **última simulación ejecutada**: cuántas billeteras
  estaban en cero antes de simular, cuántas fueron acreditadas y cuántas
  quedaron sin cambios (esto último puede pasar si el servicio no llegó a
  procesar el bono para alguna billetera puntual).
- Una tabla con todas las billeteras del sistema: usuario, saldo actual, y
  una marca visual en rojo ("Saldo en cero") para las que están en cero.

### Simular un nuevo día

1. Presioná **Simular nuevo día**.
2. Si ninguna billetera tiene saldo cero en ese momento, el botón aparece
   deshabilitado y se muestra el motivo: "Ninguna billetera tiene saldo
   cero: no hay nada que acreditar todavía".
3. Si hay al menos una billetera en cero, al presionar el botón se pide
   confirmación ("¿Deseas continuar?"), porque la acción avanza la fecha
   simulada un día y acredita el bono a todas las billeteras en cero en ese
   momento.
4. Al confirmar, se ejecuta la simulación y aparece un mensaje resumen:
   cuántas billeteras estaban en cero, cuántas fueron efectivamente
   acreditadas (se verifica comparando el saldo antes y después, no
   solamente si el servicio respondió sin error) y cuántas quedaron sin
   cambios.

### Reiniciar la simulación

Presioná **Reiniciar simulación** para volver la fecha simulada a la fecha
real de hoy, poner en cero el contador de días avanzados, y recargar el
estado real de las billeteras. Es útil para volver a demostrar el flujo
desde cero sin tener que recargar la página.

## 9. Cerrar sesión

En cualquier pantalla, desde la barra superior, presioná **Cerrar sesión**
para salir del panel de forma segura. Esto invalida la sesión del servidor,
así que si volvés a entrar, vas a tener que iniciar sesión de nuevo aunque
todavía no hayan pasado los 30 minutos de inactividad.

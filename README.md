# Migración a Folia 1.21.8

Así cambié SignShop para que funcione en Folia 1.21.8:

- Cambié la API del servidor en `pom.xml`: reemplacé `spigot-api` por `folia-api` y usé el repositorio oficial de Paper (`repo.papermc.io`).
- Declaré compatibilidad en `plugin.yml`: `api-version: "1.21"` y `folia-supported: true`.
- Reemplacé el uso de `BukkitScheduler/BukkitRunnable` por los programadores de Folia:
  - Tareas periódicas con `GlobalRegionScheduler` (delay inicial ≥ 1 tick).
  - Trabajo asíncrono con `AsyncScheduler`.
  - Código que toca jugadores o ubicaciones con `EntityScheduler` y `RegionScheduler`.
- Quité dependencias de compilación que no resolvían y mantuve los hooks opcionales por reflexión (si el plugin externo está presente, se usa; si no, se ignora).
- Validé compilación y arranque: el plugin construye y carga sin el error de delay inicial.

### Dependencias modificadas
- Agregada/cambiada: `dev.folia:folia-api` (provided) y repo de Paper.
- Quitadas del `pom.xml`: Dynmap, Essentials, Residence, BlockLocker, LWC, CMI-API (se usan por reflexión si están instaladas).
- Mantenidas: VaultAPI, GriefPrevention, WorldGuard, WorldEdit, Towny, BentoBox, LandsAPI, sqlite-jdbc, bstats-bukkit.


# Migración a Paper 1.21.8

Así cambié SignShop para que funcione en Paper 1.21.8:

- Cambié la API del servidor en `pom.xml`: reemplacé `dev.folia:folia-api` por `io.papermc.paper:paper-api` y usé el repositorio oficial de Paper (`repo.papermc.io`).
- Actualicé `plugin.yml`: mantuve `api-version: "1.21"` y eliminé `folia-supported`.
- Reemplacé los programadores de Folia por el `BukkitScheduler`:
  - Tareas periódicas con `Bukkit.getScheduler().runTaskTimer(...)`.
  - Trabajo asíncrono con `Bukkit.getScheduler().runTaskTimerAsynchronously(...)` y `runTaskAsynchronously(...)`.
  - Código que toca jugadores o ubicaciones con `Bukkit.getScheduler().runTask(...)` y `runTaskLater(...)`.
- Eliminé imports y clases específicas de Folia (`io.papermc.paper.threadedregions.*`) y limpié `TimeUnit` donde ya no era necesario.
- Validé compilación y arranque: el plugin construye y carga correctamente en Paper 1.21.8.

### Dependencias modificadas
- Agregada/cambiada: `io.papermc.paper:paper-api` (provided) y repo de Paper.
- Mantenidas: VaultAPI, GriefPrevention, WorldGuard, WorldEdit, Towny, BentoBox, LandsAPI, sqlite-jdbc, bstats-bukkit.

Author: ThxmasDev - Discord: thxmasdev

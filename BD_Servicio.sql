/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.7.2-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: utngolcoin_db
-- ------------------------------------------------------
-- Server version	12.3.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `auditoria`
--

DROP TABLE IF EXISTS `auditoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `usuario_admin_id` int(10) unsigned NOT NULL,
  `accion` varchar(10) NOT NULL,
  `tabla_afectada` varchar(50) NOT NULL,
  `registro_id` bigint(20) unsigned DEFAULT NULL,
  `datos_anteriores` longtext DEFAULT NULL,
  `datos_nuevos` longtext DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `fk_auditoria_usuario` (`usuario_admin_id`),
  KEY `idx_auditoria_fecha` (`fecha`),
  KEY `idx_auditoria_tabla` (`tabla_afectada`),
  CONSTRAINT `fk_auditoria_usuario` FOREIGN KEY (`usuario_admin_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `chk_accion_auditoria` CHECK (`accion` in ('INSERT','UPDATE','DELETE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria`
--

LOCK TABLES `auditoria` WRITE;
/*!40000 ALTER TABLE `auditoria` DISABLE KEYS */;
/*!40000 ALTER TABLE `auditoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `billeteras`
--

DROP TABLE IF EXISTS `billeteras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `billeteras` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `usuario_id` int(10) unsigned NOT NULL,
  `saldo` decimal(12,2) NOT NULL DEFAULT 0.00,
  `fecha_creacion` datetime NOT NULL DEFAULT current_timestamp(),
  `fecha_actualizacion` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_billetera_usuario` (`usuario_id`),
  CONSTRAINT `fk_billetera_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `chk_saldo_no_negativo` CHECK (`saldo` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `billeteras`
--

LOCK TABLES `billeteras` WRITE;
/*!40000 ALTER TABLE `billeteras` DISABLE KEYS */;
/*!40000 ALTER TABLE `billeteras` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bonos_diarios`
--

DROP TABLE IF EXISTS `bonos_diarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `bonos_diarios` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `usuario_id` int(10) unsigned NOT NULL,
  `fecha` date NOT NULL,
  `monto` decimal(12,2) NOT NULL DEFAULT 1.00,
  `transaccion_id` bigint(20) unsigned DEFAULT NULL,
  `fecha_otorgado` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_bono_usuario_fecha` (`usuario_id`,`fecha`),
  KEY `fk_bono_transaccion` (`transaccion_id`),
  CONSTRAINT `fk_bono_transaccion` FOREIGN KEY (`transaccion_id`) REFERENCES `transacciones` (`id`),
  CONSTRAINT `fk_bono_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bonos_diarios`
--

LOCK TABLES `bonos_diarios` WRITE;
/*!40000 ALTER TABLE `bonos_diarios` DISABLE KEYS */;
/*!40000 ALTER TABLE `bonos_diarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `partidos`
--

DROP TABLE IF EXISTS `partidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `partidos` (
  `id` int(10) unsigned NOT NULL,
  `seleccion_local_id` int(10) unsigned NOT NULL,
  `seleccion_visitante_id` int(10) unsigned NOT NULL,
  `nombre_local` varchar(60) NOT NULL,
  `nombre_visitante` varchar(60) NOT NULL,
  `fase_codigo` varchar(20) NOT NULL,
  `fase_nombre` varchar(80) NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'PROGRAMADO',
  `fecha_hora_utc` datetime NOT NULL,
  `cuota_local` decimal(5,2) NOT NULL DEFAULT 1.80,
  `cuota_empate` decimal(5,2) NOT NULL DEFAULT 3.00,
  `cuota_visitante` decimal(5,2) NOT NULL DEFAULT 2.50,
  `fecha_sync` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_partidos_fecha` (`fecha_hora_utc`),
  KEY `idx_partidos_estado` (`estado`),
  KEY `idx_partidos_fase` (`fase_codigo`),
  CONSTRAINT `chk_estado_partido` CHECK (`estado` in ('PROGRAMADO','EN_JUEGO','FINALIZADO','SUSPENDIDO','CANCELADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `partidos`
--

LOCK TABLES `partidos` WRITE;
/*!40000 ALTER TABLE `partidos` DISABLE KEYS */;
/*!40000 ALTER TABLE `partidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `predicciones`
--

DROP TABLE IF EXISTS `predicciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `predicciones` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `usuario_id` int(10) unsigned NOT NULL,
  `partido_id` int(10) unsigned NOT NULL,
  `tipo_resultado` char(1) NOT NULL,
  `monto` decimal(12,2) NOT NULL,
  `cuota_aplicada` decimal(5,2) NOT NULL,
  `estado` varchar(10) NOT NULL DEFAULT 'PENDIENTE',
  `fecha_creacion` datetime NOT NULL DEFAULT current_timestamp(),
  `fecha_liquidacion` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_prediccion_usuario_partido` (`usuario_id`,`partido_id`),
  KEY `idx_prediccion_partido` (`partido_id`),
  KEY `idx_prediccion_estado` (`estado`),
  CONSTRAINT `fk_prediccion_partido` FOREIGN KEY (`partido_id`) REFERENCES `partidos` (`id`),
  CONSTRAINT `fk_prediccion_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `chk_tipo_resultado` CHECK (`tipo_resultado` in ('1','X','2')),
  CONSTRAINT `chk_estado_prediccion` CHECK (`estado` in ('PENDIENTE','GANADA','PERDIDA')),
  CONSTRAINT `chk_monto_positivo` CHECK (`monto` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `predicciones`
--

LOCK TABLES `predicciones` WRITE;
/*!40000 ALTER TABLE `predicciones` DISABLE KEYS */;
/*!40000 ALTER TABLE `predicciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` smallint(5) unsigned NOT NULL,
  `nombre` varchar(30) NOT NULL,
  `descripcion` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_roles_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transacciones`
--

DROP TABLE IF EXISTS `transacciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `transacciones` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `billetera_id` bigint(20) unsigned NOT NULL,
  `tipo` varchar(20) NOT NULL,
  `monto` decimal(12,2) NOT NULL,
  `saldo_resultante` decimal(12,2) NOT NULL,
  `prediccion_id` bigint(20) unsigned DEFAULT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `fk_transaccion_prediccion` (`prediccion_id`),
  KEY `idx_transaccion_billetera_fecha` (`billetera_id`,`fecha`),
  KEY `idx_transaccion_tipo` (`tipo`),
  CONSTRAINT `fk_transaccion_billetera` FOREIGN KEY (`billetera_id`) REFERENCES `billeteras` (`id`),
  CONSTRAINT `fk_transaccion_prediccion` FOREIGN KEY (`prediccion_id`) REFERENCES `predicciones` (`id`),
  CONSTRAINT `chk_tipo_transaccion` CHECK (`tipo` in ('BONO_BIENVENIDA','APUESTA_PREDICCION','PREMIO_PREDICCION','BONO_DIARIO','AJUSTE_ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transacciones`
--

LOCK TABLES `transacciones` WRITE;
/*!40000 ALTER TABLE `transacciones` DISABLE KEYS */;
/*!40000 ALTER TABLE `transacciones` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_uca1400_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_transacciones_before_insert
BEFORE INSERT ON transacciones
FOR EACH ROW
BEGIN
  DECLARE v_saldo DECIMAL(12,2);

  SELECT saldo INTO v_saldo
  FROM billeteras
  WHERE id = NEW.billetera_id
  FOR UPDATE;

  IF (v_saldo + NEW.monto) < 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Saldo insuficiente: la transaccion dejaria el saldo en negativo';
  END IF;

  SET NEW.saldo_resultante = v_saldo + NEW.monto;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_uca1400_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_transacciones_after_insert
AFTER INSERT ON transacciones
FOR EACH ROW
BEGIN
  UPDATE billeteras
  SET saldo = NEW.saldo_resultante
  WHERE id = NEW.billetera_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int(10) unsigned NOT NULL,
  `username` varchar(40) NOT NULL,
  `nombre` varchar(120) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `rol_id` smallint(5) unsigned NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_registro` datetime NOT NULL,
  `fecha_sync` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_usuarios_username` (`username`),
  KEY `fk_usuarios_rol` (`rol_id`),
  CONSTRAINT `fk_usuarios_rol` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `vista_circulacion_monedas`
--

DROP TABLE IF EXISTS `vista_circulacion_monedas`;
/*!50001 DROP VIEW IF EXISTS `vista_circulacion_monedas`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `vista_circulacion_monedas` AS SELECT
 1 AS `total_utngolcoin_circulante`,
  1 AS `total_billeteras`,
  1 AS `saldo_promedio` */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `vista_historial_transacciones`
--

DROP TABLE IF EXISTS `vista_historial_transacciones`;
/*!50001 DROP VIEW IF EXISTS `vista_historial_transacciones`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `vista_historial_transacciones` AS SELECT
 1 AS `id`,
  1 AS `username`,
  1 AS `nombre`,
  1 AS `tipo`,
  1 AS `monto`,
  1 AS `saldo_resultante`,
  1 AS `descripcion`,
  1 AS `fecha` */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `vista_partidos_mas_predicciones`
--

DROP TABLE IF EXISTS `vista_partidos_mas_predicciones`;
/*!50001 DROP VIEW IF EXISTS `vista_partidos_mas_predicciones`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `vista_partidos_mas_predicciones` AS SELECT
 1 AS `partido_id`,
  1 AS `nombre_local`,
  1 AS `nombre_visitante`,
  1 AS `fase_nombre`,
  1 AS `total_predicciones`,
  1 AS `total_utngolcoin_apostado` */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `vista_ranking_usuarios`
--

DROP TABLE IF EXISTS `vista_ranking_usuarios`;
/*!50001 DROP VIEW IF EXISTS `vista_ranking_usuarios`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `vista_ranking_usuarios` AS SELECT
 1 AS `usuario_id`,
  1 AS `username`,
  1 AS `nombre`,
  1 AS `saldo`,
  1 AS `aciertos`,
  1 AS `predicciones_totales` */;
SET character_set_client = @saved_cs_client;

--
-- Dumping routines for database 'utngolcoin_db'
--
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_liquidar_predicciones_partido` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_uca1400_ai_ci */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_liquidar_predicciones_partido`(
  IN p_partido_id      INT UNSIGNED,
  IN p_goles_local     TINYINT UNSIGNED,
  IN p_goles_visitante TINYINT UNSIGNED
)
BEGIN
  DECLARE done           INT DEFAULT 0;
  DECLARE v_pred_id      BIGINT UNSIGNED;
  DECLARE v_usuario_id   INT UNSIGNED;
  DECLARE v_monto        DECIMAL(12,2);
  DECLARE v_cuota        DECIMAL(5,2);
  DECLARE v_tipo_res     CHAR(1);
  DECLARE v_billetera_id BIGINT UNSIGNED;
  DECLARE v_resultado    CHAR(1);

  DECLARE cur CURSOR FOR
    SELECT id, usuario_id, monto, cuota_aplicada, tipo_resultado
    FROM predicciones
    WHERE partido_id = p_partido_id AND estado = 'PENDIENTE';
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

  UPDATE partidos
  SET estado = 'FINALIZADO',
      goles_local = p_goles_local,
      goles_visitante = p_goles_visitante
  WHERE id = p_partido_id;

  IF p_goles_local > p_goles_visitante THEN
    SET v_resultado = '1';
  ELSEIF p_goles_local < p_goles_visitante THEN
    SET v_resultado = '2';
  ELSE
    SET v_resultado = 'X';
  END IF;

  OPEN cur;
  liquidar_loop: LOOP
    FETCH cur INTO v_pred_id, v_usuario_id, v_monto, v_cuota, v_tipo_res;
    IF done THEN LEAVE liquidar_loop; END IF;

    SELECT id INTO v_billetera_id FROM billeteras WHERE usuario_id = v_usuario_id;

    IF v_tipo_res = v_resultado THEN
      UPDATE predicciones
      SET estado = 'GANADA', fecha_liquidacion = NOW()
      WHERE id = v_pred_id;

      INSERT INTO transacciones (billetera_id, tipo, monto, saldo_resultante, prediccion_id, descripcion)
      VALUES (v_billetera_id, 'PREMIO_PREDICCION', ROUND(v_monto * v_cuota, 2), 0.00,
              v_pred_id, CONCAT('Premio prediccion #', v_pred_id));
    ELSE
      UPDATE predicciones
      SET estado = 'PERDIDA', fecha_liquidacion = NOW()
      WHERE id = v_pred_id;
    END IF;
  END LOOP;
  CLOSE cur;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_otorgar_bono_diario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_uca1400_ai_ci */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_otorgar_bono_diario`(IN p_usuario_id INT UNSIGNED)
BEGIN
  DECLARE v_billetera_id BIGINT UNSIGNED;
  DECLARE v_saldo        DECIMAL(12,2);
  DECLARE v_ya_otorgado  INT DEFAULT 0;
  DECLARE v_transaccion  BIGINT UNSIGNED;

  SELECT id, saldo INTO v_billetera_id, v_saldo
  FROM billeteras WHERE usuario_id = p_usuario_id;

  SELECT COUNT(*) INTO v_ya_otorgado
  FROM bonos_diarios WHERE usuario_id = p_usuario_id AND fecha = CURDATE();

  IF v_saldo <= 0 AND v_ya_otorgado = 0 THEN
    INSERT INTO transacciones (billetera_id, tipo, monto, saldo_resultante, descripcion)
    VALUES (v_billetera_id, 'BONO_DIARIO', 1.00, 0.00, 'Bono diario anti-bancarrota');
    SET v_transaccion = LAST_INSERT_ID();

    INSERT INTO bonos_diarios (usuario_id, fecha, monto, transaccion_id)
    VALUES (p_usuario_id, CURDATE(), 1.00, v_transaccion);
  END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_registrar_prediccion` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_uca1400_ai_ci */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_registrar_prediccion`(
  IN p_usuario_id     INT UNSIGNED,
  IN p_partido_id     INT UNSIGNED,
  IN p_tipo_resultado CHAR(1),
  IN p_monto          DECIMAL(12,2)
)
BEGIN
  DECLARE v_fecha_utc    DATETIME;
  DECLARE v_cuota        DECIMAL(5,2);
  DECLARE v_billetera_id BIGINT UNSIGNED;
  DECLARE v_saldo        DECIMAL(12,2);
  DECLARE v_pred_id      BIGINT UNSIGNED;
  DECLARE v_ya_existe    INT DEFAULT 0;

  SELECT fecha_hora_utc,
    CASE p_tipo_resultado
      WHEN '1' THEN cuota_local
      WHEN 'X' THEN cuota_empate
      WHEN '2' THEN cuota_visitante
    END
  INTO v_fecha_utc, v_cuota
  FROM partidos WHERE id = p_partido_id;

  IF v_fecha_utc IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Partido no encontrado';
  END IF;

  IF UTC_TIMESTAMP() >= v_fecha_utc THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Prediccion cerrada: el partido ya inicio (RF17)';
  END IF;

  SELECT COUNT(*) INTO v_ya_existe
  FROM predicciones WHERE usuario_id = p_usuario_id AND partido_id = p_partido_id;

  IF v_ya_existe > 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Ya existe una prediccion de este usuario para este partido (RF18)';
  END IF;

  SELECT id, saldo INTO v_billetera_id, v_saldo
  FROM billeteras WHERE usuario_id = p_usuario_id;

  IF v_saldo < p_monto THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Saldo insuficiente para realizar la prediccion (RF16)';
  END IF;

  INSERT INTO predicciones (usuario_id, partido_id, tipo_resultado, monto, cuota_aplicada, estado)
  VALUES (p_usuario_id, p_partido_id, p_tipo_resultado, p_monto, v_cuota, 'PENDIENTE');
  SET v_pred_id = LAST_INSERT_ID();

  INSERT INTO transacciones (billetera_id, tipo, monto, saldo_resultante, prediccion_id, descripcion)
  VALUES (v_billetera_id, 'APUESTA_PREDICCION', -p_monto, 0.00, v_pred_id,
          CONCAT('Apuesta partido #', p_partido_id));
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_registrar_usuario_billetera` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_uca1400_ai_ci */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_registrar_usuario_billetera`(
  IN p_id             INT UNSIGNED,
  IN p_username       VARCHAR(40),
  IN p_nombre         VARCHAR(120),
  IN p_email          VARCHAR(150),
  IN p_rol_id         SMALLINT UNSIGNED,
  IN p_fecha_registro DATETIME
)
BEGIN
  DECLARE v_billetera_id BIGINT UNSIGNED;
  DECLARE v_existe INT DEFAULT 0;

  INSERT INTO usuarios (id, username, nombre, email, rol_id, fecha_registro)
  VALUES (p_id, p_username, p_nombre, p_email, p_rol_id, p_fecha_registro)
  ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    nombre   = VALUES(nombre),
    email    = VALUES(email),
    rol_id   = VALUES(rol_id);

  SELECT COUNT(*) INTO v_existe FROM billeteras WHERE usuario_id = p_id;

  IF v_existe = 0 THEN
    INSERT INTO billeteras (usuario_id, saldo) VALUES (p_id, 0.00);
    SET v_billetera_id = LAST_INSERT_ID();

    INSERT INTO transacciones (billetera_id, tipo, monto, saldo_resultante, descripcion)
    VALUES (v_billetera_id, 'BONO_BIENVENIDA', 10.00, 0.00, 'Bono de bienvenida UTNGolCoin');
  END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `vista_circulacion_monedas`
--

/*!50001 DROP VIEW IF EXISTS `vista_circulacion_monedas`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_uca1400_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vista_circulacion_monedas` AS select sum(`billeteras`.`saldo`) AS `total_utngolcoin_circulante`,count(0) AS `total_billeteras`,avg(`billeteras`.`saldo`) AS `saldo_promedio` from `billeteras` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vista_historial_transacciones`
--

/*!50001 DROP VIEW IF EXISTS `vista_historial_transacciones`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_uca1400_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vista_historial_transacciones` AS select `t`.`id` AS `id`,`u`.`username` AS `username`,`u`.`nombre` AS `nombre`,`t`.`tipo` AS `tipo`,`t`.`monto` AS `monto`,`t`.`saldo_resultante` AS `saldo_resultante`,`t`.`descripcion` AS `descripcion`,`t`.`fecha` AS `fecha` from ((`transacciones` `t` join `billeteras` `b` on(`b`.`id` = `t`.`billetera_id`)) join `usuarios` `u` on(`u`.`id` = `b`.`usuario_id`)) order by `t`.`fecha` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vista_partidos_mas_predicciones`
--

/*!50001 DROP VIEW IF EXISTS `vista_partidos_mas_predicciones`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_uca1400_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vista_partidos_mas_predicciones` AS select `pr`.`partido_id` AS `partido_id`,`pa`.`nombre_local` AS `nombre_local`,`pa`.`nombre_visitante` AS `nombre_visitante`,`pa`.`fase_nombre` AS `fase_nombre`,count(0) AS `total_predicciones`,sum(`pr`.`monto`) AS `total_utngolcoin_apostado` from (`predicciones` `pr` join `partidos` `pa` on(`pa`.`id` = `pr`.`partido_id`)) group by `pr`.`partido_id`,`pa`.`nombre_local`,`pa`.`nombre_visitante`,`pa`.`fase_nombre` order by count(0) desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vista_ranking_usuarios`
--

/*!50001 DROP VIEW IF EXISTS `vista_ranking_usuarios`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_uca1400_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vista_ranking_usuarios` AS select `u`.`id` AS `usuario_id`,`u`.`username` AS `username`,`u`.`nombre` AS `nombre`,`b`.`saldo` AS `saldo`,sum(case when `p`.`estado` = 'GANADA' then 1 else 0 end) AS `aciertos`,count(`p`.`id`) AS `predicciones_totales` from ((`usuarios` `u` join `billeteras` `b` on(`b`.`usuario_id` = `u`.`id`)) left join `predicciones` `p` on(`p`.`usuario_id` = `u`.`id`)) group by `u`.`id`,`u`.`username`,`u`.`nombre`,`b`.`saldo` order by `b`.`saldo` desc,sum(case when `p`.`estado` = 'GANADA' then 1 else 0 end) desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2026-07-14  7:44:24

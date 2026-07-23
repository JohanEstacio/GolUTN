package ec.edu.utn.golutn.admin.service;

import ec.edu.utn.golutn.admin.dto.BonoDiarioRequestDto;
import ec.edu.utn.golutn.admin.model.Billetera;
import ec.edu.utn.golutn.admin.model.ReporteResumen;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cliente hacia el Servicio UTNGolCoin, usado solo para el modulo de
 * Reportes y auditoria (RF27: total de UTNGolCoin en circulacion).
 *
 * Igual que EstadisticasApiClient: si el otro backend (el de tu compañero
 * de apuestas / UTNGolCoin) no responde, se usan datos de ejemplo.
 */
@ApplicationScoped
public class UtnGolCoinApiClient {

    private static final Logger LOG = Logger.getLogger(UtnGolCoinApiClient.class.getName());

    private Client client;
    private String baseUrl;
    private boolean usarMockSiFalla;

    @PostConstruct
    public void init() {
        this.client = ClientBuilder.newClient().register(JacksonObjectMapperResolver.class);
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null) {
            this.baseUrl = fc.getExternalContext().getInitParameter("UTNGOLCOIN_API_URL");
            this.usarMockSiFalla = Boolean.parseBoolean(
                    fc.getExternalContext().getInitParameter("USAR_MOCK_SI_FALLA_API"));
        } else {
            this.baseUrl = "http://localhost:8081/utngolcoin-api/api";
            this.usarMockSiFalla = true;
        }
    }

    public ReporteResumen obtenerResumenReportes() {
        try {
            Response resp = client.target(baseUrl).path("/reportes/resumen")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(ReporteResumen.class);
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio UTNGolCoin, usando datos de ejemplo", e);
        }
        return usarMockSiFalla ? datosEjemploResumen() : new ReporteResumen();
    }

    private ReporteResumen datosEjemploResumen() {
        return new ReporteResumen(
                "Argentina vs Canada",
                37,
                845.50,
                4,
                1
        );
    }

    // ---------------------------------------------------------------
    // BONO ANTI-BANCARROTA (RF20, CP11)
    // ---------------------------------------------------------------

    public List<Billetera> listarBilleteras() {
        try {
            Response resp = client.target(baseUrl).path("/billeteras")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Billetera>>() {});
            }
            LOG.warning("API de billeteras respondio codigo " + resp.getStatus());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio UTNGolCoin (billeteras), usando datos de ejemplo", e);
        }
        return usarMockSiFalla ? datosEjemploBilleteras() : new ArrayList<>();
    }

    /**
     * El procedimiento almacenado del backend responde 200 aunque no haya
     * acreditado nada, asi que este resultado solo indica que la llamada
     * se completo, no que el bono se haya otorgado realmente.
     */
    public boolean otorgarBonoDiario(Long usuarioId, LocalDate fecha) {
        try {
            Response resp = client.target(baseUrl).path("/bonos/otorgar")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(new BonoDiarioRequestDto(usuarioId, fecha)));
            return resp.getStatus() == 200 || resp.getStatus() == 201 || resp.getStatus() == 204;
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo otorgar el bono diario en la API (modo mock)", e);
            return usarMockSiFalla;
        }
    }

    private List<Billetera> datosEjemploBilleteras() {
        List<Billetera> lista = new ArrayList<>();
        lista.add(new Billetera(1L, 1L, "admin", 120.0));
        lista.add(new Billetera(2L, 2L, "ana.torres", 0.0));
        lista.add(new Billetera(3L, 3L, "luis.perez", 0.0));
        return lista;
    }
}

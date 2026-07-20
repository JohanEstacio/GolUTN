package ec.edu.utn.golutn.admin.service;

import ec.edu.utn.golutn.admin.model.ReporteResumen;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
        this.client = ClientBuilder.newClient();
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
}

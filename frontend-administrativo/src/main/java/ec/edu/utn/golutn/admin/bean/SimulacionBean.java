package ec.edu.utn.golutn.admin.bean;

import ec.edu.utn.golutn.admin.model.Billetera;
import ec.edu.utn.golutn.admin.service.UtnGolCoinApiClient;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simulacion del bono anti-bancarrota para demostraciones (RF20, CP11).
 *
 * Permite avanzar un dia simulado sin esperar dias reales: acredita 1
 * UTNGolCoin a cada billetera con saldo cero. El procedimiento almacenado
 * del backend responde 200 aunque no haya acreditado nada, asi que la
 * confirmacion real se hace comparando el saldo de cada billetera antes y
 * despues de la operacion, en vez de confiar en el codigo de respuesta.
 */
@Named("simulacionBean")
@ViewScoped
public class SimulacionBean implements Serializable {

    @Inject
    private UtnGolCoinApiClient apiClient;

    private final LocalDate fechaReal = LocalDate.now();
    private LocalDate fechaSimulada = LocalDate.now();
    private int diasAvanzados = 0;

    private List<Billetera> billeteras = new ArrayList<>();

    private Integer ultimoEnCero;
    private Integer ultimoAcreditados;
    private Integer ultimoSinCambios;

    @PostConstruct
    public void init() {
        cargarBilleteras();
    }

    private void cargarBilleteras() {
        this.billeteras = apiClient.listarBilleteras();
    }

    public void simularNuevoDia() {
        List<Billetera> enCero = billeterasEnCero();
        if (enCero.isEmpty()) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "No hay billeteras en cero",
                    "Ninguna billetera tiene saldo cero, no se otorgó ningún bono.");
            return;
        }

        fechaSimulada = fechaSimulada.plusDays(1);
        diasAvanzados++;

        Map<Long, BigDecimal> saldosPrevios = new HashMap<>();
        for (Billetera b : enCero) {
            saldosPrevios.put(b.getBilleteraId(), b.getSaldo());
            apiClient.otorgarBonoDiario(b.getUsuarioId(), fechaSimulada);
        }

        cargarBilleteras();

        int acreditados = 0;
        for (Billetera actual : billeteras) {
            BigDecimal previo = saldosPrevios.get(actual.getBilleteraId());
            if (previo != null && actual.getSaldo().compareTo(previo) > 0) {
                acreditados++;
            }
        }
        int sinCambios = enCero.size() - acreditados;

        ultimoEnCero = enCero.size();
        ultimoAcreditados = acreditados;
        ultimoSinCambios = sinCambios;

        agregarMensaje(FacesMessage.SEVERITY_INFO, "Simulación ejecutada",
                ultimoEnCero + " billetera(s) en cero, " + acreditados + " acreditada(s) y "
                        + sinCambios + " sin cambios.");
    }

    public void reiniciarSimulacion() {
        fechaSimulada = fechaReal;
        diasAvanzados = 0;
        ultimoEnCero = null;
        ultimoAcreditados = null;
        ultimoSinCambios = null;
        cargarBilleteras();
        agregarMensaje(FacesMessage.SEVERITY_INFO, "Simulación reiniciada",
                "La fecha simulada volvió a la fecha real y se recargaron las billeteras.");
    }

    private void agregarMensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    public List<Billetera> billeterasEnCero() {
        List<Billetera> lista = new ArrayList<>();
        for (Billetera b : billeteras) {
            if (b.isEnCero()) {
                lista.add(b);
            }
        }
        return lista;
    }

    public boolean isHayBilleterasEnCero() {
        return !billeterasEnCero().isEmpty();
    }

    public LocalDate getFechaSimulada() { return fechaSimulada; }

    public int getDiasAvanzados() { return diasAvanzados; }

    public List<Billetera> getBilleteras() { return billeteras; }

    public Integer getUltimoEnCero() { return ultimoEnCero; }

    public Integer getUltimoAcreditados() { return ultimoAcreditados; }

    public Integer getUltimoSinCambios() { return ultimoSinCambios; }
}

package com.matheus.services;

import com.matheus.entidades.Reserva;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Matheus Fassicollo
 */
@Singleton
@Startup
public class NotificacaoService {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private ReservaService reservaService; 

    @EJB
    private WhatsAppService whatsAppService; 

    /**
     * Roda automaticamente a cada 5 minutos.
     * Verifica se existem reservas próximas que precisam de lembrete.
     */
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void verificarProximasReservas() {
        
        System.out.println(">>> ROBÔ: Verificando reservas para notificar...");

        try {
            String sql = "SELECT r.* FROM reservas r " +
                         "JOIN usuarios u ON r.res_user_id = u.user_id " +
                         "WHERE r.res_status = 'CONFIRMADA' " +
                         "  AND r.res_lembrete_enviado = FALSE " +
                         "  AND u.user_notifica_whatsapp = TRUE " +
                         "  AND TIMESTAMPADD(MINUTE, -u.user_notifica_antecedencia_min, r.res_dt_inicio) " +
                         "      BETWEEN NOW() AND TIMESTAMPADD(MINUTE, 5, NOW())";

            Query q = em.createNativeQuery(sql, Reserva.class);
            List<Reserva> reservasParaNotificar = q.getResultList();
            
            if (reservasParaNotificar.isEmpty()) {
                System.out.println(">>> ROBÔ: Nenhuma reserva para notificar agora.");
                return;
            }

            System.out.println(">>> ROBÔ: Encontradas " + reservasParaNotificar.size() + " reservas.");

            for (Reserva reserva : reservasParaNotificar) {
                try {
                    String telefoneCliente = reserva.getUsuario().getUserTelefone();
                    
                    boolean enviado = whatsAppService.enviarLembrete(telefoneCliente);

                    if (enviado) {
                        reserva.setResLembreteEnviado(true);
                        reservaService.salvar(reserva); 
                        System.out.println("Sucesso! Reserva " + reserva.getResId() + " notificada.");
                    } else {
                        System.err.println("Falha ao enviar para reserva " + reserva.getResId());
                    }

                } catch (Exception e) {
                    System.err.println("Erro ao processar reserva " + reserva.getResId() + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erro fatal na verificação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
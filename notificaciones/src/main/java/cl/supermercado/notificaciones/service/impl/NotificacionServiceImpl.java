package cl.supermercado.notificaciones.service.impl;
import cl.supermercado.notificaciones.dto.remote.LoginDto;
import cl.supermercado.notificaciones.dto.request.NotificacionRequestDto;
import cl.supermercado.notificaciones.dto.response.NotificacionResponseDto;
import cl.supermercado.notificaciones.mapper.NotificacionMapper;
import cl.supermercado.notificaciones.model.Notificacion;
import cl.supermercado.notificaciones.repository.NotificacionRepository;
import cl.supermercado.notificaciones.service.NotificacionService;

import cl.supermercado.notificaciones.service.api.UsuariosClient;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository repository;
    private final NotificacionMapper mapper;
    private final UsuariosClient usuariosClient;

    // REST: valida que el que llama sea FUNCIONARIO
    @Override
    @Transactional
    public NotificacionResponseDto enviarNotificacion(Long userId, NotificacionRequestDto request) {
        validarFuncionario(userId);
        return guardarNotificacion(request);
    }

    // Kafka: llamada interna del sistema, sin validación de rol
    // El CompraKafkaConsumer sigue llamando a este metodos sin cambios
    @Override
    @Transactional
    public NotificacionResponseDto enviarNotificacion(NotificacionRequestDto request) {
        log.info("Notificación automática vía Kafka para usuarioId={}", request.getUsuarioId());
        return guardarNotificacion(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDto> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public NotificacionResponseDto marcarComoLeida(Long id) {
        Notificacion n = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));
        n.setLeido(true);
        repository.save(n);
        return mapper.toDto(n);
    }


    // Metodos privados
    private void validarFuncionario(Long userId) {
        log.info("Validando que usuario ID {} sea FUNCIONARIO", userId);
        LoginDto login;
        try {
            login = usuariosClient.getRol(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("El usuario con ID " + userId + " no existe");
        }
        if (login.getRol() == null || !login.getRol().getName().equalsIgnoreCase("FUNCIONARIO")) {
            throw new IllegalArgumentException("Solo los funcionarios pueden enviar notificaciones");
        }
        log.info("Usuario ID {} validado como FUNCIONARIO", userId);
    }

    private NotificacionResponseDto guardarNotificacion(NotificacionRequestDto request) {
        Notificacion n = mapper.toEntity(request);
        repository.save(n);
        log.info("Notificación guardada para usuarioId={}", request.getUsuarioId());
        return mapper.toDto(n);
    }
    @Override
    @Transactional(readOnly = true)
    public NotificacionResponseDto obtenerPorId(Long id) {
        Notificacion n = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada con id: " + id));
        return mapper.toDto(n);
    }
}

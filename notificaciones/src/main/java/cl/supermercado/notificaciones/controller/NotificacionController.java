package cl.supermercado.notificaciones.controller;

import cl.supermercado.notificaciones.dto.request.NotificacionRequestDto;
import cl.supermercado.notificaciones.dto.response.NotificacionResponseDto;
import cl.supermercado.notificaciones.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    // AGREGADO: Necesario para el enlace "self" en HATEOAS
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDto>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.listarPorUsuario(usuarioId));
    }

    @PutMapping("/{id}/leida")
    public ResponseEntity<NotificacionResponseDto> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<NotificacionResponseDto> enviar(
            @PathVariable Long userId,
            @Valid @RequestBody NotificacionRequestDto request) {
        return ResponseEntity.ok(notificacionService.enviarNotificacion(userId, request));
    }
}